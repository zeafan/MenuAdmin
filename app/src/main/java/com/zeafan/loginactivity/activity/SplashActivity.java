package com.zeafan.loginactivity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.core.Firebase;
import com.zeafan.loginactivity.core.GlobalClass;
import com.zeafan.loginactivity.data.Category;
import com.zeafan.loginactivity.data.CompanyInfo;
import com.zeafan.loginactivity.data.ProductItem;
import com.zeafan.loginactivity.data.User;
import com.zeafan.loginactivity.interfaces.callbackUser;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lottieAnimationView = findViewById(R.id.animation_view);
        Firebase.GetUser(new callbackUser(){
            @Override
            public void onSuccessful() {

                Intent mainActivity = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainActivity);
                lottieAnimationView.cancelAnimation();
                finish();
            }
        });

    }


}