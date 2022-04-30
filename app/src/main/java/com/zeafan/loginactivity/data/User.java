package com.zeafan.loginactivity.data;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public static String key_firebase = "user";
    public ArrayList<Category> categories;
    public ArrayList <ProductItem> productItems;
    public CompanyInfo companyInfo;

    public User(CompanyInfo companyInfo,ArrayList<Category> categories,ArrayList <ProductItem> productItems) {
        this.companyInfo = companyInfo;
        this.categories = categories;
        this.productItems = productItems;
    }
    public User(CompanyInfo companyInfo) {
        this.companyInfo = companyInfo;
    }
}
