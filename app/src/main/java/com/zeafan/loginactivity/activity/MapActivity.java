package com.zeafan.loginactivity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zeafan.loginactivity.R;

import java.util.ArrayList;
import java.util.Arrays;

public class MapActivity extends AppCompatActivity {
    public static String list_key = "key_array";
    String[][] lst_data;
    ArrayList<String> header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getDataFromIntent();
        header  = getHeader();


    }

    private ArrayList<String> getHeader() {
        int Columes = lst_data[0].length;
        ArrayList<String> header = new ArrayList<>();
        header.addAll(Arrays.asList(lst_data[0]).subList(0, Columes));
        return header;
    }

    private void getDataFromIntent() {
        lst_data = (String[][]) getIntent().getSerializableExtra(list_key);
    }
}