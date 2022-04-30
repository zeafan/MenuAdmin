package com.zeafan.loginactivity.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.core.Firebase;
import com.zeafan.loginactivity.core.GlobalClass;
import com.zeafan.loginactivity.core.IResult;
import com.zeafan.loginactivity.core.Utilities;
import com.zeafan.loginactivity.data.Category;
import com.zeafan.loginactivity.data.User;
import com.zeafan.loginactivity.interfaces.callbackSaveProductItem;
import com.zeafan.loginactivity.interfaces.callbackUser;

import java.io.IOException;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ru.katso.livebutton.LiveButton;

public class AddCategoryActivity extends AppCompatActivity implements View.OnClickListener  {
    TextInputEditText EtName,EtLatinName;
    LinearLayout ln_takeImage,ln_FolderImage;
    ImageView IV_CategoryImage;
    Category CurrentCategory;
    Category LoadCategory;
    LiveButton btn_save;
    ProgressBar Pb_upload_image;
    LottieAnimationView lottieAnimationView;
    int index =-1;
    boolean isNewItem = false;
    final private int requsetCodeGetImageFromGallery = 2004;
    final private int requsetCodeTakePhoto = 2005;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        getCategoryFromIntentOrCreateNewCategory();
        initUI();
        setValue();
        actioView();
    }

    private void actioView() {
        ln_takeImage.setOnClickListener(this);
        ln_FolderImage.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    private void setValue() {
        if(!isNewItem){
            EtName.setText(CurrentCategory.CategoryName);
            EtLatinName.setText(CurrentCategory.LatinCategoryName);
            Glide.with(this).asBitmap().load(CurrentCategory.ImagePath).into(IV_CategoryImage);
            btn_save.setText(getString(R.string.update));
        }
    }

    private void initUI() {
        EtName = findViewById(R.id.et_name);
        EtLatinName = findViewById(R.id.et_latinName);
        ln_takeImage = findViewById(R.id.ln_take_photo);
        ln_FolderImage = findViewById(R.id.ln_load_folder);
        IV_CategoryImage = findViewById(R.id.iv_product_image);
        btn_save = findViewById(R.id.btn_save);
        Pb_upload_image = findViewById(R.id.progress_upload_img);
        lottieAnimationView = findViewById(R.id.done_upload_img);
    }


    private void getCategoryFromIntentOrCreateNewCategory() {
        index = getIntent().getIntExtra("index", -1);
        if(index>-1){
            LoadCategory = GlobalClass.currentUser.categories.get(index);
            CurrentCategory = new Category(LoadCategory.CategoryName,LoadCategory.CategoryGuid,LoadCategory.LatinCategoryName,LoadCategory.ImagePath,LoadCategory.ParentKey);
            isNewItem = false;
        }else {
            isNewItem =true;
            CurrentCategory = new Category();
        }
    }
    @Override
    public void onClick(View view) {
        GlobalClass.StaticCore.Click(view,AddCategoryActivity.this);
        switch (view.getId()){
            case R.id.ln_load_folder:
                GlobalClass.StaticCore.checkPermissionGetImage(AddCategoryActivity.this,requsetCodeGetImageFromGallery);
                break;
            case R.id.ln_take_photo:
                GlobalClass.StaticCore.checkPermissionTakeImage(AddCategoryActivity.this,requsetCodeTakePhoto);
                break;
            case R.id.btn_save:
                if(isValidationCategory()) {
                    Dialog d =  Utilities.showWaitDialog(AddCategoryActivity.this);
                    sendCategory(d);
                }
                break;
        }
    }
    private boolean isValidationCategory() {
        String name = EtName.getText().toString();
        if(name.isEmpty()){
            EtName.setError(getString(R.string.empty_name));
            EtName.requestFocus();
            return false;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save,menu);
        if(isNewItem){
            menu.findItem(R.id.delete).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.delete){
            Utilities.showConfrimationDialog(AddCategoryActivity.this,R.string.confirm, R.string.delete_confirmation, new IResult() {
                @Override
                public void notifySuccess(Object response) {
                    CurrentCategory.deleteFromServer(new callbackSaveProductItem(){
                        @Override
                        public void onsuccessfully() {
                            Firebase.onCategoryChange(new callbackUser() {
                                @Override
                                public void onSuccessful() {
                                    Toast.makeText(AddCategoryActivity.this, getString(R.string.done), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    });
                }

                @Override
                public void notifyError(Object error) {

                }
            });
        }
        else if(item.getItemId() == R.id.back){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendCategory(Dialog dialog) {
        CurrentCategory.CategoryName = EtName.getText().toString();
        CurrentCategory.LatinCategoryName = EtLatinName.getText().toString();
        if (isNewItem) {
            try {
                FirebaseDatabase.getInstance().getReference(GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        .child(User.key_firebase).child(CurrentCategory.Key_firebase_list)
                        .child(CurrentCategory.CategoryGuid).setValue(CurrentCategory).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if(task.isSuccessful()){
                            Firebase.onCategoryChange(new callbackUser() {
                                @Override
                                public void onSuccessful() {
                                    Toast.makeText(AddCategoryActivity.this, getString(R.string.successful_upload_database), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(AddCategoryActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();

                    }
                });

            } catch (Exception e) {
                GlobalClass.SendExceptionToFirebaseServer(e);
                Toast.makeText(AddCategoryActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else {
            try {
                HashMap<String,Object> category = CurrentCategory.ConvertObjectToHashMap();
                FirebaseDatabase.getInstance().getReference(GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        .child(User.key_firebase).child(Category.Key_firebase_list)
                        .child(CurrentCategory.CategoryGuid).updateChildren(category).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if(task.isSuccessful()){
                            Firebase.onCategoryChange(new callbackUser() {
                                @Override
                                public void onSuccessful() {
                                    Toast.makeText(AddCategoryActivity.this, getString(R.string.successful_upload_database), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(AddCategoryActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();

                    }
                });

            } catch (Exception e) {
                GlobalClass.SendExceptionToFirebaseServer(e);
                Toast.makeText(AddCategoryActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case requsetCodeGetImageFromGallery:
                if (resultCode == android.app.Activity.RESULT_OK && imageReturnedIntent != null) {
                    Pb_upload_image.setVisibility(View.VISIBLE);
                    String BackGroundImagePath = imageReturnedIntent.getData().toString();
                    Bitmap bitmap = null;
                    Uri selectedImage = Uri.parse(BackGroundImagePath);
                    IV_CategoryImage.setImageURI(selectedImage);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        GlobalClass.SendExceptionToFirebaseServer(e);
                        e.printStackTrace();
                    }
                    if(bitmap == null){
                        return;
                    }
                    byte[] imageAsArrayBytes = GlobalClass.convertBitmapToArrayByte(bitmap,300);
                    final StorageReference ref =  FirebaseStorage.getInstance().getReference().child("productImage").child(CurrentCategory.CategoryGuid);
                    ref.putBytes(imageAsArrayBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Pb_upload_image.setVisibility(View.GONE);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadPhotoUrl) {
                                    CurrentCategory.ImagePath = downloadPhotoUrl.toString();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Pb_upload_image.setVisibility(View.GONE);
                            Toast.makeText(AddCategoryActivity.this, R.string.failure_upload_backup, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                break;
            case requsetCodeTakePhoto:
                Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                if(bitmap!=null) {
                    Pb_upload_image.setVisibility(View.VISIBLE);
                    IV_CategoryImage.setImageBitmap(bitmap);
                    byte[] Imagedata = GlobalClass.convertBitmapToArrayByte(bitmap,300);
                    final StorageReference ref =  FirebaseStorage.getInstance().getReference().child("productImage").child(CurrentCategory.CategoryGuid);
                    ref.putBytes(Imagedata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Pb_upload_image.setVisibility(View.GONE);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadPhotoUrl) {
                                    CurrentCategory.ImagePath = downloadPhotoUrl.toString();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Pb_upload_image.setVisibility(View.GONE);
                            Toast.makeText(AddCategoryActivity.this, R.string.failure_upload_backup, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(requestCode == requsetCodeGetImageFromGallery) {
                GlobalClass.StaticCore.GetImageFromGallery(AddCategoryActivity.this,requsetCodeGetImageFromGallery);
            }else if(requestCode == requsetCodeTakePhoto) {
                GlobalClass.StaticCore.takeImagefromCamera(AddCategoryActivity.this,requsetCodeTakePhoto);
            }
        } else {
            Toast.makeText(AddCategoryActivity.this, "try again", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}