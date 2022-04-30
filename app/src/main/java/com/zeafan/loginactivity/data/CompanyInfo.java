package com.zeafan.loginactivity.data;

import java.io.Serializable;

public class CompanyInfo implements Serializable {
   public String Name, CompanyGuid,Password,licence,email;

    public CompanyInfo() {
    }

    public CompanyInfo(String name, String guid, String password, String licence, String e_mail) {
        Name = name;
        CompanyGuid = guid;
        Password = password;
        this.licence = licence;
        this.email = e_mail;
    }
}
