package com.zeafan.loginactivity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.adapter.CategoryAdapter;
import com.zeafan.loginactivity.adapter.ProductItemsAdapter;
import com.zeafan.loginactivity.core.GlobalClass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupsListActivity extends AppCompatActivity {
    RecyclerView rv_items;
    LinearLayout ln_empty_info;
    CategoryAdapter categoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);
        rv_items = findViewById(R.id.rv_items);
        ln_empty_info = findViewById(R.id.ln_empty_info);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GroupsListActivity.this, AddCategoryActivity.class));
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(GlobalClass.currentUser.categories.isEmpty()){
            ln_empty_info.setVisibility(View.VISIBLE);
        }
        categoryAdapter = new CategoryAdapter(this,GlobalClass.currentUser.categories,new CategorySelected(){
            @Override
            public void onCategorySelected(int index) {
                Intent open = new Intent(GroupsListActivity.this, AddCategoryActivity.class);
                open.putExtra("index",index);
                startActivity(open);
            }
        });
        rv_items.setHasFixedSize(true);
        rv_items.setLayoutManager(new GridLayoutManager(this,1));
        rv_items.setAdapter(categoryAdapter);
    }

    public interface CategorySelected{
        void onCategorySelected(int index);
    }
}