package com.example.dogwalker.retrofit2.response;

public class UserOwnerDTO {

    String id;
    String pw;
    String name;
    String phonenumber;
    String profile_img;

    public UserOwnerDTO(String id, String pw, String name, String phonenumber, String profile_img) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.phonenumber = phonenumber;
        this.profile_img = profile_img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
