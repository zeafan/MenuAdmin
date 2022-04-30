package com.zeafan.loginactivity.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.adapter.ProductItemsAdapter;
import com.zeafan.loginactivity.core.GlobalClass;
import com.zeafan.loginactivity.data.Category;
import com.zeafan.loginactivity.data.ProductItem;
import com.zeafan.loginactivity.data.User;

import java.util.ArrayList;

public class ItemsListActivity extends AppCompatActivity {
    RecyclerView lstItems;
    LinearLayout ln_empty_info;
    ProductItemsAdapter productItemsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);
        initView();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open = new Intent(ItemsListActivity.this, AddItemActivity.class);
                startActivity(open);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(GlobalClass.currentUser.productItems.isEmpty()){
            ln_empty_info.setVisibility(View.VISIBLE);
        }
        productItemsAdapter = new ProductItemsAdapter(this,GlobalClass.currentUser.productItems,new ProductSelect(){
            @Override
            public void onProductSelected(int index) {
                Intent open = new Intent(ItemsListActivity.this, AddItemActivity.class);
                open.putExtra("index",index);
                startActivity(open);
            }
        });
        lstItems.setHasFixedSize(true);
        lstItems.setLayoutManager(new GridLayoutManager(this,1));
        lstItems.setAdapter(productItemsAdapter);
    }
    private void initView() {
         lstItems = findViewById(R.id.ls_items);
         ln_empty_info = findViewById(R.id.ln_empty_info);
    }
   public interface ProductSelect{
       void onProductSelected(int index);
    }
}