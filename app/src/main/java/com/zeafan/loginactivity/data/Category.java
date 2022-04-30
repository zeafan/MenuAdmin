package com.zeafan.loginactivity.data;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zeafan.loginactivity.core.GlobalClass;
import com.zeafan.loginactivity.interfaces.callbackSaveProductItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;

public class Category implements Serializable {
    public static String Key_load_category = "loadCategoryKey";
    public static String Key_load_categories = "loadCategoriesKey";
    public static String Key_firebase_list = "categories";

    public String CategoryName;
    public String CategoryGuid;
    public String LatinCategoryName;
    public String ImagePath;
    public String ParentKey;

    public Category() {
        CategoryName = "";
        CategoryGuid = UUID.randomUUID().toString();
        LatinCategoryName="";
        ImagePath ="";
        ParentKey = CategoryGuid;
    }

    public Category(String categoryName, String categoryGuid, String latinCategoryName, String imagePath,String parentKey) {
        CategoryName = categoryName;
        CategoryGuid = categoryGuid;
        LatinCategoryName = latinCategoryName;
        ImagePath = imagePath;
        ParentKey = parentKey;
    }

    public String getName() {
        final String language = Locale.getDefault().getLanguage();
        if (!language.equals("ar") && LatinCategoryName != null && !LatinCategoryName.isEmpty())
            return LatinCategoryName;

        return CategoryName;
    }

    public void deleteFromServer(callbackSaveProductItem saveProductItem) {
        FirebaseDatabase.getInstance().getReference(GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .child(User.key_firebase).child(Category.Key_firebase_list)
                .child(ParentKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                saveProductItem.onsuccessfully();
            }
        });
    }

    public HashMap<String, Object> ConvertObjectToHashMap() {
        HashMap<String, Object> item = new HashMap<>();
        item.put("CategoryName",CategoryName);
        item.put("LatinCategoryName",LatinCategoryName);
        item.put("ImagePath",ImagePath);
        return item;

    }
}
