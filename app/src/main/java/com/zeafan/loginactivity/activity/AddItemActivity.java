package com.zeafan.loginactivity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import ru.katso.livebutton.LiveButton;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.zeafan.loginactivity.data.ProductItem;
import com.zeafan.loginactivity.data.User;
import com.zeafan.loginactivity.interfaces.callbackSaveProductItem;
import com.zeafan.loginactivity.interfaces.callbackUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;

public class AddItemActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputEditText etName, etLatinName, etBarcode, etPrice,et_UnitName;
    LinearLayout ln_takeImage,ln_FolderImage;
    ImageView IV_productImage;
    Spinner sp_Category;
    RadioGroup Rg_salesUnit;
    RadioButton rb_unit,rb_weight;
    ArrayList<String> categories_values;
    LiveButton btn_save;
    EditText et_more_details;
    boolean isNewItem = false;
    ProgressBar Pb_upload_image;
    LottieAnimationView lottieAnimationView;
    final private int requsetCodeGetImageFromGallery = 2002;
    final private int requsetCodeTakePhoto = 2003;
    ProductItem currentProductItem;
    ProductItem loadedProduct;
   final private int requestIntentOpenAddCategory=2004;
   int index =-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getProductItemFromIntentOrGenerateItem();

        initUI();
        setValue();
        actioView();
    }

    private ArrayList<String> getCategoriesValues() {
        ArrayList<String> values = new ArrayList<>();
        for(int i =0;i<GlobalClass.currentUser.categories.size();i++){
            values.add(GlobalClass.currentUser.categories.get(i).getName());
        }
        values.add(getString(R.string.new_category));
        return values;
    }

    private void actioView() {
        sp_Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==categories_values.size()-1){
                    startActivityForResult(new Intent(AddItemActivity.this,AddCategoryActivity.class), requestIntentOpenAddCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setValue() {

        if(!isNewItem){
             etName.setText(currentProductItem.itemName);
             etLatinName.setText(currentProductItem.latinItemName);
             etBarcode.setText(currentProductItem.barcode);
             etPrice.setText(String.valueOf(currentProductItem.price));
             et_UnitName.setText(String.valueOf(currentProductItem.UnitName));
             et_more_details.setText(currentProductItem.describeItem);
             Glide.with(this).asBitmap().load(currentProductItem.imagePaths).into(IV_productImage);
            if(currentProductItem.UnitType==0){
                rb_unit.setChecked(true);
            } else {
                rb_weight.setChecked(true);
            }
            btn_save.setText(getString(R.string.update));
        }

    }

    private int setSelectionCategory() {
        if(!isNewItem){
            for(int i=0;i<GlobalClass.currentUser.categories.size();i++){
                if(currentProductItem.categoryGuid.equals(GlobalClass.currentUser.categories.get(i).CategoryGuid)){
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        categories_values = getCategoriesValues();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories_values);
        sp_Category.setAdapter(categoryAdapter);
        sp_Category.setSelection(setSelectionCategory());
    }

    private void initUI() {
        etName = findViewById(R.id.et_name);
        etLatinName = findViewById(R.id.et_latinName);
        etBarcode = findViewById(R.id.et_barcode);
        etPrice = findViewById(R.id.et_price);
        et_UnitName = findViewById(R.id.et_UnitName);
        ln_takeImage = findViewById(R.id.ln_take_photo_item);
        ln_FolderImage = findViewById(R.id.ln_load_folder);
        IV_productImage = findViewById(R.id.iv_product_image);
        sp_Category = findViewById(R.id.sp_category);
        Rg_salesUnit = findViewById(R.id.rg_salesType);
        Pb_upload_image = findViewById(R.id.progress_upload_img);
        lottieAnimationView = findViewById(R.id.done_upload_img);
        rb_unit = findViewById(R.id.rbUnit);
        rb_weight = findViewById(R.id.rbWeight);
        btn_save = findViewById(R.id.btn_save);
        et_more_details = findViewById(R.id.more_details);
        ln_takeImage.setOnClickListener(this);
        ln_FolderImage.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }


    private void getProductItemFromIntentOrGenerateItem() {
        index = getIntent().getIntExtra("index", -1);
        if (index > -1) {
            loadedProduct = GlobalClass.currentUser.productItems.get(index);
            currentProductItem = new ProductItem(loadedProduct.itemName, loadedProduct.ItemGuid,
                    loadedProduct.latinItemName, loadedProduct.categoryGuid, loadedProduct.barcode,
                    loadedProduct.UnitType, loadedProduct.price, loadedProduct.imagePaths,
                    loadedProduct.describeItem,loadedProduct.ParentKey,loadedProduct.UnitName);
            isNewItem = false;
        } else {
            isNewItem = true;
            currentProductItem = new ProductItem();
        }
    }

    @Override
    public void onClick(View view) {
        GlobalClass.StaticCore.Click(view,AddItemActivity.this);
        switch (view.getId()){
            case R.id.ln_load_folder:
                GlobalClass.StaticCore.checkPermissionGetImage(AddItemActivity.this,requsetCodeGetImageFromGallery);
                break;
            case R.id.ln_take_photo_item:
                GlobalClass.StaticCore.checkPermissionTakeImage(AddItemActivity.this,requsetCodeTakePhoto);
                break;
            case R.id.btn_save:
                if(isValidationProduct()) {
                  Dialog d =  Utilities.showWaitDialog(AddItemActivity.this);
                    sendItem(d);
                }
                break;
        }
    }



    private boolean isValidationProduct() {
        String name = etName.getText().toString();
        String price = etPrice.getText().toString();
        if(name.isEmpty()){
            etName.setError(getString(R.string.empty_name));
            etName.requestFocus();
            return false;
        }
        if(price.isEmpty()){
            etPrice.setError(getString(R.string.empty_price));
            etPrice.requestFocus();
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
            Utilities.showConfrimationDialog(AddItemActivity.this,R.string.confirm, R.string.delete_confirmation, new IResult() {
                @Override
                public void notifySuccess(Object response) {
                    currentProductItem.deleteFromServer(new callbackSaveProductItem(){
                        @Override
                        public void onsuccessfully() {
                            Firebase.onProductItemsChange(new callbackUser() {
                                @Override
                                public void onSuccessful() {
                                    Toast.makeText(AddItemActivity.this, getString(R.string.done), Toast.LENGTH_SHORT).show();
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

    private void sendItem(Dialog dialog) {
        currentProductItem.price = Double.valueOf(etPrice.getText().toString());
        currentProductItem.UnitName = et_UnitName.getText().toString();
        currentProductItem.UnitType = rb_unit.isChecked() ? 0 : 1;
        currentProductItem.barcode = etBarcode.getText().toString();
        currentProductItem.latinItemName = etLatinName.getText().toString();
        currentProductItem.itemName = etName.getText().toString();
        currentProductItem.describeItem = et_more_details.getText().toString();
        currentProductItem.categoryGuid = GlobalClass.currentUser.categories.get(sp_Category.getSelectedItemPosition()).CategoryGuid;
        if (isNewItem) {
            try {
                FirebaseDatabase.getInstance().getReference(GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        .child(User.key_firebase).child(ProductItem.Key_firebase_list)
                        .child(currentProductItem.ItemGuid).setValue(currentProductItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if(task.isSuccessful()){
                            Firebase.onProductItemsChange(new callbackUser() {
                                @Override
                                public void onSuccessful() {
                                    Toast.makeText(AddItemActivity.this, getString(R.string.successful_upload_database), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(AddItemActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();

                    }
                });

            } catch (Exception e) {
                GlobalClass.SendExceptionToFirebaseServer(e);
                Toast.makeText(AddItemActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else {
            try {
                HashMap<String,Object> ItemProduct = currentProductItem.ConvertObjectToHashMap();
                FirebaseDatabase.getInstance().getReference(GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        .child(User.key_firebase).child(ProductItem.Key_firebase_list)
                        .child(currentProductItem.ItemGuid).updateChildren(ItemProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if(task.isSuccessful()){
                            Firebase.onProductItemsChange(new callbackUser() {
                                @Override
                                public void onSuccessful() {
                                    Toast.makeText(AddItemActivity.this, getString(R.string.successful_upload_database), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(AddItemActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();

                    }
                });

            } catch (Exception e) {
                GlobalClass.SendExceptionToFirebaseServer(e);
                Toast.makeText(AddItemActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    IV_productImage.setImageURI(selectedImage);
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
                    final StorageReference ref =  FirebaseStorage.getInstance().getReference().child("productImage").child(currentProductItem.ItemGuid);
                    ref.putBytes(imageAsArrayBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Pb_upload_image.setVisibility(View.GONE);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadPhotoUrl) {
                                   currentProductItem.imagePaths = downloadPhotoUrl.toString();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Pb_upload_image.setVisibility(View.GONE);
                            Toast.makeText(AddItemActivity.this, R.string.failure_upload_backup, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                break;
            case requsetCodeTakePhoto:
                Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                if(bitmap!=null) {
                    Pb_upload_image.setVisibility(View.VISIBLE);
                    IV_productImage.setImageBitmap(bitmap);
                    byte[] Imagedata = GlobalClass.convertBitmapToArrayByte(bitmap,300);
                    final StorageReference ref =  FirebaseStorage.getInstance().getReference().child("productImage").child(currentProductItem.ItemGuid);
                    ref.putBytes(Imagedata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Pb_upload_image.setVisibility(View.GONE);
                            lottieAnimationView.setVisibility(View.VISIBLE);
                            lottieAnimationView.playAnimation();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadPhotoUrl) {
                                    currentProductItem.imagePaths = downloadPhotoUrl.toString();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Pb_upload_image.setVisibility(View.GONE);
                            Toast.makeText(AddItemActivity.this, R.string.failure_upload_backup, Toast.LENGTH_SHORT).show();
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
                GlobalClass.StaticCore.GetImageFromGallery(AddItemActivity.this,requsetCodeGetImageFromGallery);
            }else if(requestCode == requsetCodeTakePhoto) {
                GlobalClass.StaticCore.takeImagefromCamera(AddItemActivity.this,requsetCodeTakePhoto);
            }
        } else {
            Toast.makeText(AddItemActivity.this, "try again", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}