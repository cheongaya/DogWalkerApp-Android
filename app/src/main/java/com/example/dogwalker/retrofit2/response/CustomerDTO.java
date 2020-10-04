package com.example.dogwalker.retrofit2.response;

public class CustomerDTO {

    String customer_id;
    String profile_img_url;

    public CustomerDTO(String customer_id, String profile_img_url) {
        this.customer_id = customer_id;
        this.profile_img_url = profile_img_url;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getProfile_img_url() {
        return profile_img_url;
    }

    public void setProfile_img_url(String profile_img_url) {
        this.profile_img_url = profile_img_url;
    }
}
