package com.example.dogwalker.retrofit2.response;

public class WalkerMypageDTO {

    String id;
    String profile_img;
    String location;
    String review_count;
    String satisfation_score;
    String customer_count;

    public WalkerMypageDTO(String id, String profile_img, String location, String review_count, String satisfation_score, String customer_count) {
        this.id = id;
        this.profile_img = profile_img;
        this.location = location;
        this.review_count = review_count;
        this.satisfation_score = satisfation_score;
        this.customer_count = customer_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReview_count() {
        return review_count;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }

    public String getSatisfation_score() {
        return satisfation_score;
    }

    public void setSatisfation_score(String satisfation_score) {
        this.satisfation_score = satisfation_score;
    }

    public String getCustomer_count() {
        return customer_count;
    }

    public void setCustomer_count(String customer_count) {
        this.customer_count = customer_count;
    }
}
