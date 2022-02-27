package com.zeafan.loginactivity.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.core.Utilities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextInputEditText et_Email;
    String email;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        auth = FirebaseAuth.getInstance();
        et_Email = findViewById(R.id.et_email);
        findViewById(R.id.btnResetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = et_Email.getText().toString().trim();
                if(email.isEmpty()){
                    et_Email.setError(getString(R.string.empty_email));
                    et_Email.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    et_Email.setError(getString(R.string.invalid_email));
                    et_Email.requestFocus();
                    return;
                }
                Dialog d = Utilities.showWaitDialog(ForgotPasswordActivity.this);
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        d.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            Toast.makeText(ForgotPasswordActivity.this, getString(R.string.check_email_reset_password), Toast.LENGTH_LONG).show();
                        }else {
                            Snackbar.make(view, getString(R.string.try_again_wrong_happen), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
            }
        });
    }
}