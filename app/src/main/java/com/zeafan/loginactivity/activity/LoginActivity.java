package com.zeafan.loginactivity.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.core.Utilities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextInputEditText et_Email,et_password;
    private FirebaseAuth firebaseAuth;
    private  String email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.tv_forgotPassword).setOnClickListener(this);
        findViewById(R.id.btn_Login).setOnClickListener(this);;
        et_Email = findViewById(R.id.lgin_et_email);
        et_password = findViewById(R.id.lgin_et_password);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_register:
                startActivity(new Intent(this,RegistrationActivity.class));
                break;
            case R.id.tv_forgotPassword:
                startActivity(new Intent(this,ForgotPasswordActivity.class));
                break;
            case R.id.btn_Login:
                if(checkValidationData()){
                    Dialog waitDailog = Utilities.showWaitDialog(LoginActivity.this);
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            waitDailog.dismiss();
                            if(task.isSuccessful()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user!=null) {
                                    if (user.isEmailVerified()) {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    } else {
                                        user.sendEmailVerification();
                                        Utilities.showWarningDialog(LoginActivity.this, "", R.string.check_email);
                                    }
                                }
                            }else {
                                Toast.makeText(LoginActivity.this, getString(R.string.failed_login), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                break;
        }
    }
    private boolean checkValidationData() {
        email = et_Email.getText().toString().trim();
        password = et_password.getText().toString().trim();
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
        return true;
    }
}