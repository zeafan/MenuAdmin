package com.zeafan.loginactivity.data;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zeafan.loginactivity.core.GlobalClass;
import com.zeafan.loginactivity.interfaces.callbackSaveProductItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import androidx.annotation.NonNull;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ProductItem implements Serializable {
    public static String serial_Key = "KEY_loadedItem";
    public static String Key_load_items = "KEY_loadedItems";
    public static String Key_firebase_list = "productItems";
    public String itemName;
    public String ItemGuid;
    public String latinItemName;
    public String categoryGuid;
    public String barcode;
    public int UnitType;
    public String UnitName;
    public double price;
    public String imagePaths;
    public String describeItem;
    public String ParentKey;

    public ProductItem(String itemName, String itemGuid, String latinItemName, String categoryGuid,
                       String barcode, int unitType, double price, String imagePaths, String describeItem,String ParentKey,String UnitName) {
        this.itemName = itemName;
        ItemGuid = itemGuid;
        this.latinItemName = latinItemName;
        this.categoryGuid = categoryGuid;
        this.barcode = barcode;
        UnitType = unitType;
        this.price = price;
        this.imagePaths = imagePaths;
        this.describeItem = describeItem;
        this.ParentKey = ParentKey;
        this.UnitName = UnitName;
    }
    public ProductItem(String itemName, String itemGuid, String latinItemName,
                       String barcode, double price, String imagePaths, String describeItem,String UnitName) {
        this.itemName = itemName;
        ItemGuid = itemGuid;
        this.latinItemName = latinItemName;
        this.categoryGuid = GlobalClass.EmptyGuid;
        this.barcode = barcode;
        this.price = price;
        ParentKey = itemGuid;
        this.imagePaths = imagePaths;
        this.describeItem = describeItem;
        this.UnitName = UnitName;
    }

    public ProductItem() {
        ItemGuid = UUID.randomUUID().toString();
        itemName = "";
        latinItemName = "";
        categoryGuid = GlobalClass.EmptyGuid;
        barcode = "";
        UnitType = 0;
        price = 0;
        imagePaths = "";
        describeItem = "";
        UnitName = "";
        ParentKey =UUID.randomUUID().toString();
    }

    public static String getString(Cell cell) {
        if(cell != null && !cell.getStringCellValue().isEmpty())
            return cell.getStringCellValue();
        return "";
    }

    public static String getString(Row row, int name_index) {
        if(name_index>0 && row.getCell(name_index-1)!= null)
            try {
                return row.getCell(name_index-1).getStringCellValue();
            }catch (Exception e){
                return "";
            }
        return "";
    }
    public static double getDouble(Row row, int index) {
        if(index>0 && row.getCell(index-1)!= null)
            try {
                return row.getCell(index-1).getNumericCellValue();
            }catch (Exception e){
                return 0;
            }
            
        return 0;
    }
    public void deleteFromServer(callbackSaveProductItem successful) {
        FirebaseDatabase.getInstance().getReference(GlobalClass.getSimpleUUID(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .child(User.key_firebase).child(ProductItem.Key_firebase_list)
                .child(ParentKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successful.onsuccessfully();
            }
        });

    }

    public HashMap<String, Object> ConvertObjectToHashMap() {
        HashMap<String, Object> item = new HashMap<>();
        item.put("itemName",itemName);
        item.put("latinItemName",latinItemName);
        item.put("imagePaths",imagePaths);
        item.put("UnitType",UnitType);
        item.put("barcode",barcode);
        item.put("categoryGuid",categoryGuid);
        item.put("describeItem",describeItem);
        item.put("price",price);
        item.put("UnitName",UnitName);
        return item;
    }
}
