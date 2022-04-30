package com.zeafan.loginactivity.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.core.GlobalClass;
import com.zeafan.loginactivity.core.Utilities;
import com.zeafan.loginactivity.data.Category;
import com.zeafan.loginactivity.data.CompanyInfo;
import com.zeafan.loginactivity.data.User;

import java.util.ArrayList;
import java.util.UUID;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    TextInputEditText et_Email,et_password,et_Confirm_password,et_companyName;
    EditText et_licence;
    private FirebaseAuth firebaseAuth;
    String email,password,companyName, licence,confirm_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initUI();
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidationData()) {
                    Dialog waitDialog = Utilities.showWaitDialog(RegistrationActivity.this);
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        firebaseAuth.getCurrentUser().sendEmailVerification();
                                        CompanyInfo companyInfo = new CompanyInfo(companyName, UUID.randomUUID().toString(), password, licence, email);
                                        Category category = new Category("بدون فئة", GlobalClass.EmptyGuid, "without", "",GlobalClass.EmptyGuid);
                                        ArrayList<Category> categories = new ArrayList<>();
                                        categories.add(category);
                                        User _user = new User(companyInfo);
                                        FirebaseDatabase.getInstance().getReference(GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                                .child(User.key_firebase).setValue(_user)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        waitDialog.dismiss();
                                                        Toast.makeText(RegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                waitDialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    FirebaseDatabase.getInstance().getReference(GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                                            .child(User.key_firebase).child(Category.Key_firebase_list).child(category.CategoryGuid).setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(RegistrationActivity.this, getString(R.string.check_email), Toast.LENGTH_LONG).show();
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(RegistrationActivity.this, getString(R.string.failure_upload_database), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean checkValidationData() {
         email = et_Email.getText().toString().trim();
         password = et_password.getText().toString().trim();
         confirm_password = et_Confirm_password.getText().toString().trim();
        companyName= et_companyName.getText().toString().trim();
         licence = et_licence.getText().toString().trim();
        if(email.isEmpty()){
            et_Email.setError(getString(R.string.empty_email));
            et_Email.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_Email.setError(getString(R.string.invalid_email));
            et_Email.requestFocus();
            return false;
        }
        if(companyName.isEmpty()){
            et_companyName.setError(getString(R.string.empty_company));
            et_companyName.requestFocus();
            return false;
        }
        if(password.isEmpty()){
            et_password.setError(getString(R.string.empty_password));
            et_password.requestFocus();
            return false;
        }
        if(password.length()<6){
            et_password.setError(getString(R.string.count_password));
            et_password.requestFocus();
            return false;
        }
        if(confirm_password.isEmpty()){
            et_Confirm_password.setError(getString(R.string.empty_password));
            et_Confirm_password.requestFocus();
            return false;
        }
        if(confirm_password.length()<6){
            et_Confirm_password.setError(getString(R.string.count_password));
            et_Confirm_password.requestFocus();
            return false;
        }
        if(!password.equals(confirm_password)){
            et_Confirm_password.setError(getString(R.string.incorrect_password));
            et_Confirm_password.requestFocus();
            return false;
        }
        if(licence.isEmpty()){
            et_licence.setError(getString(R.string.empty_licence));
            et_licence.requestFocus();
            return false;
        }
        return true;
    }

    private void initUI() {
        et_Email = findViewById(R.id.rg_et_email);
        et_companyName = findViewById(R.id.rg_et_company_name);
        et_password = findViewById(R.id.rg_et_password);
        et_Confirm_password = findViewById(R.id.rg_et_confirm_password);
        et_licence = findViewById(R.id.rg_et_license);
    }
}