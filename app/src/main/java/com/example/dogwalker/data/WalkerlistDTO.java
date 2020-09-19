package com.example.dogwalker.data;

public class WalkerlistDTO {

    String id;
    String name;
    String profile_img;
    String location;
    int review_score;
    String introduce;
    int price_thirty_minutes;
    String bmk_user_id;
    String bmk_walker_id;

    public WalkerlistDTO(String id, String name, String profile_img, String location, int review_score, String introduce, int price_thirty_minutes, String bmk_user_id, String bmk_walker_id) {
        this.id = id;
        this.name = name;
        this.profile_img = profile_img;
        this.location = location;
        this.review_score = review_score;
        this.introduce = introduce;
        this.price_thirty_minutes = price_thirty_minutes;
        this.bmk_user_id = bmk_user_id;
        this.bmk_walker_id = bmk_walker_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getReview_score() {
        return review_score;
    }

    public void setReview_score(int review_score) {
        this.review_score = review_score;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getPrice_thirty_minutes() {
        return price_thirty_minutes;
    }

    public void setPrice_thirty_minutes(int price_thirty_minutes) {
        this.price_thirty_minutes = price_thirty_minutes;
    }

    public String getBmk_user_id() {
        return bmk_user_id;
    }

    public void setBmk_user_id(String bmk_user_id) {
        this.bmk_user_id = bmk_user_id;
    }

    public String getBmk_walker_id() {
        return bmk_walker_id;
    }

    public void setBmk_walker_id(String bmk_walker_id) {
        this.bmk_walker_id = bmk_walker_id;
    }
}
