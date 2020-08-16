package com.example.dogwalker.retrofit2.response;

public class UserWalkerDTO {

    public final String id;
    public final String pw;
    public final String name;
    public final String phonenumber;

    public UserWalkerDTO(String id, String pw, String name, String phonenumber) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.phonenumber = phonenumber;
    }
}
