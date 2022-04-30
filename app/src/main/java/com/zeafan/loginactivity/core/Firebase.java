package com.zeafan.loginactivity.core;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeafan.loginactivity.R;
import com.zeafan.loginactivity.activity.AddItemActivity;
import com.zeafan.loginactivity.activity.MainActivity;
import com.zeafan.loginactivity.activity.SplashActivity;
import com.zeafan.loginactivity.data.Category;
import com.zeafan.loginactivity.data.CompanyInfo;
import com.zeafan.loginactivity.data.ProductItem;
import com.zeafan.loginactivity.data.User;
import com.zeafan.loginactivity.interfaces.callbackSaveProductItem;
import com.zeafan.loginactivity.interfaces.callbackUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Firebase {
    public static void onProductItemsChange(callbackUser callbackUser) {
        String uid = GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
         FirebaseDatabase.getInstance().getReference(GlobalClass.getSimpleUUID(uid)).child("user").child(ProductItem.Key_firebase_list).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GlobalClass.currentUser.productItems = getProductItem(snapshot);
                callbackUser.onSuccessful();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void GetUser(callbackUser callbackUser) {
        String uid = GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(uid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CompanyInfo companyInfo =  snapshot.child("user").child("companyInfo").getValue(CompanyInfo.class);
                ArrayList<Category> categories = getCategories(snapshot.child("user").child("categories"));
                ArrayList<ProductItem>productItems = getProductItem(snapshot.child("user").child("productItems"));
                GlobalClass.currentUser = new User(companyInfo,categories,productItems);
                callbackUser.onSuccessful();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private static ArrayList<Category> getCategories(DataSnapshot snapshot) {
        ArrayList<Category> categories = new ArrayList<>();
        for(DataSnapshot data : snapshot.getChildren()){
           Category category = data.getValue(Category.class);
           if(category!=null) {
               category.ParentKey = data.getKey();
               categories.add(category);
           }
        }
        return categories;
    }
    private static ArrayList<ProductItem> getProductItem(DataSnapshot snapshot) {
        ArrayList<ProductItem> items = new ArrayList<>();
        if(snapshot.exists()) {
            for (DataSnapshot data : snapshot.getChildren()) {
                ProductItem productItem = data.getValue(ProductItem.class);
                productItem.ParentKey = data.getKey();
                items.add(productItem);
            }
        }
        return items;
    }
    public static void onCategoryChange(callbackUser callbackUser) {
        String uid = GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference(uid).child("user").child(Category.Key_firebase_list).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GlobalClass.currentUser.categories = getCategories(snapshot);
                callbackUser.onSuccessful();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
