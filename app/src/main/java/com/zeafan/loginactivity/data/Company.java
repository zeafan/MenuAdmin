package com.zeafan.loginactivity.data;

public class Company {
   public String Name,Guid,Password,licence,email;

    public Company(String name, String guid, String password, String licence,String e_mail) {
        Name = name;
        Guid = guid;
        Password = password;
        this.licence = licence;
        this.email = e_mail;
    }
}
