package com.zeafan.loginactivity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zeafan.loginactivity.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.ln_products).setOnClickListener(this);
        findViewById(R.id.ln_groups).setOnClickListener(this);
        findViewById(R.id.ln_company_info).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ln_products:
                startActivity(new Intent(MainActivity.this,ItemsListActivity.class));
                break;
            case R.id.ln_groups:
                startActivity(new Intent(MainActivity.this,GroupsListActivity.class));
                break;
            case R.id.ln_company_info:
                startActivity(new Intent(MainActivity.this,CompanyInfo.class));
                break;
        }
    }
}