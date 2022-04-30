package com.zeafan.loginactivity.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.core.GlobalClass;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressBar progressBar;
    ImageView qr_view;
    TextView txt_uuid;
    ImageButton Ib_share,Ib_share_txt;
    String ReferenceDatabase;
    Bitmap qr_bitmap;
    final private int requsetCodeGetFile=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.ln_products).setOnClickListener(this);
        findViewById(R.id.ln_groups).setOnClickListener(this);
        progressBar = findViewById(R.id.pg_qr);
        qr_view = findViewById(R.id.iv_qr);
        txt_uuid = findViewById(R.id.tv_uuid);
        Ib_share_txt = findViewById(R.id.ib_share2);
        Ib_share = findViewById(R.id.ib_share);
        ReferenceDatabase = GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        txt_uuid.setText(ReferenceDatabase);
        setQRIntoView();
        Ib_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(qr_bitmap!=null){
                    shareQrCode();
                }
            }
        });

        Ib_share_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    shareTxtUuid();
            }
        });
    }

    private void shareTxtUuid() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,ReferenceDatabase);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_by)));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            Logout();
        }
        else if(item.getItemId() == R.id.file_excel){
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (!shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requsetCodeGetFile);
                        return false;
                    }
                }

            }
            getExcelFileFromStorage(new onGetExcelFill(){
                @Override
                public void onGetFile(File file) {
                   String [][] readFile = read(file);
                   if(readFile.length>1){
                       Intent i = new Intent(MainActivity.this,MapActivity.class);
                               i.putExtra(MapActivity.list_key,readFile);
                               startActivity(i);
                   }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
    public  void  getExcelFileFromStorage(onGetExcelFill getFile){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        String Format = ".xls";
        String Format2 = ".xlsx";
        builderSingle.setTitle(getResources().getString(R.string.select)+" (.xls OR xlsx) Format" );
        ArrayList<String> items = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();

        getFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath()).listFiles(), Format,Format2, items, files);
        final ArrayList<File> copy_files = files;
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice, items);
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getFile.onGetFile(copy_files.get(which));
            }
        });
        builderSingle.show();
    }
    private void getFiles(File[] files, String format,String format2, ArrayList<String> items, ArrayList<File> fs) {
        try {
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        getFiles(file.listFiles(), format,format2, items, fs);
                    } else {
                        if (file.getPath().endsWith(format)||file.getPath().endsWith(format2)) {
                            items.add(file.getName());
                            fs.add(file);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            GlobalClass.SendExceptionToFirebaseServer(ex);
        }
    }
    private void Logout() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("password","");
        editor.putString("email","");
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this,LoginActivity.class));
    }
    private void shareQrCode() {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            Uri uri = null;
            try {
                uri = getImageUri(qr_bitmap);
            } catch (Exception e) {
                GlobalClass.SendExceptionToFirebaseServer(e);
            }
            if (uri != null) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_by)));
            }
    }
    private Uri getImageUri(Bitmap image) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.zeafan.provider", file);

        } catch (IOException e) {
            GlobalClass.SendExceptionToFirebaseServer(e);
        }
        return uri;
    }
    private void setQRIntoView() {
        try {
            qr_bitmap= GlobalClass.GenerateQRCode(ReferenceDatabase,350);
            qr_view.setImageBitmap(qr_bitmap);
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            GlobalClass.SendExceptionToFirebaseServer(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        GlobalClass.StaticCore.Click(view,MainActivity.this);
        switch (view.getId()){
            case R.id.ln_products:
                startActivity(new Intent(MainActivity.this,ItemsListActivity.class));
                break;
            case R.id.ln_groups:
                startActivity(new Intent(MainActivity.this,GroupsListActivity.class));
                break;
        }
    }
    interface  onGetExcelFill {
        void  onGetFile(File file);
    }
    public String[][] read(File ExcelFile) {
//        Workbook workbook = null;
//        try {
//            WorkbookSettings ws = new WorkbookSettings();
//            ws.setGCDisabled(true);
//
//
//
//            workbook = Workbook.getWorkbook(ExcelFile, ws);
//            Sheet sheet = workbook.getSheet(0);
//
//            int rowCount = sheet.getRows();
//            String[][] result = new String[rowCount][];
//            for (int i = 0; i < rowCount; i++) {
//                Cell[] row = sheet.getRow(i);
//
//                result[i] = new String[row.length];
//                for (int j = 0; j < row.length; j++) {
//                    result[i][j] = row[j].getContents();
//                }
//            }
//            return result;
//
//
//        } catch (BiffException e) {
//
//        } catch (IOException e) {
//        } catch (Exception e) {
//        } finally {
//            if (workbook != null) {
//                workbook.close();
//            }
//        }
        return null;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(requestCode == requsetCodeGetFile) {
                getExcelFileFromStorage(new onGetExcelFill(){
                    @Override
                    public void onGetFile(File file) {
                        String [][] readFile = read(file);
                    }
                });
            }
        } else {
            Toast.makeText(this, "try again", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}