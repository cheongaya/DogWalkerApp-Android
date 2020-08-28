package com.example.dogwalker.data;

public class WalkerlistDTO {

    String id;
    String name;
    String phonenumber;
    String profile_img;
    String location;
    int review_score;
    int satisfation_score;
    int customer_score;
    String introduce;
    int walkable_type_s;
    int walkable_type_m;
    int walkable_type_l;
    int walkable_dog_number;
    int price_thirty_minutes;
    int price_sixty_minutes;
    int addprice_large_size;
    int addprice_one_dog;
    int addprice_holiday;

    public WalkerlistDTO(String id, String name, String phonenumber, String profile_img, String location, int review_score, int satisfation_score, int customer_score, String introduce, int walkable_type_s, int walkable_type_m, int walkable_type_l, int walkable_dog_number, int price_thirty_minutes, int price_sixty_minutes, int addprice_large_size, int addprice_one_dog, int addprice_holiday) {
        this.id = id;
        this.name = name;
        this.phonenumber = phonenumber;
        this.profile_img = profile_img;
        this.location = location;
        this.review_score = review_score;
        this.satisfation_score = satisfation_score;
        this.customer_score = customer_score;
        this.introduce = introduce;
        this.walkable_type_s = walkable_type_s;
        this.walkable_type_m = walkable_type_m;
        this.walkable_type_l = walkable_type_l;
        this.walkable_dog_number = walkable_dog_number;
        this.price_thirty_minutes = price_thirty_minutes;
        this.price_sixty_minutes = price_sixty_minutes;
        this.addprice_large_size = addprice_large_size;
        this.addprice_one_dog = addprice_one_dog;
        this.addprice_holiday = addprice_holiday;
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

    public int getSatisfation_score() {
        return satisfation_score;
    }

    public void setSatisfation_score(int satisfation_score) {
        this.satisfation_score = satisfation_score;
    }

    public int getCustomer_score() {
        return customer_score;
    }

    public void setCustomer_score(int customer_score) {
        this.customer_score = customer_score;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getWalkable_type_s() {
        return walkable_type_s;
    }

    public void setWalkable_type_s(int walkable_type_s) {
        this.walkable_type_s = walkable_type_s;
    }

    public int getWalkable_type_m() {
        return walkable_type_m;
    }

    public void setWalkable_type_m(int walkable_type_m) {
        this.walkable_type_m = walkable_type_m;
    }

    public int getWalkable_type_l() {
        return walkable_type_l;
    }

    public void setWalkable_type_l(int walkable_type_l) {
        this.walkable_type_l = walkable_type_l;
    }

    public int getWalkable_dog_number() {
        return walkable_dog_number;
    }

    public void setWalkable_dog_number(int walkable_dog_number) {
        this.walkable_dog_number = walkable_dog_number;
    }

    public int getPrice_thirty_minutes() {
        return price_thirty_minutes;
    }

    public void setPrice_thirty_minutes(int price_thirty_minutes) {
        this.price_thirty_minutes = price_thirty_minutes;
    }

    public int getPrice_sixty_minutes() {
        return price_sixty_minutes;
    }

    public void setPrice_sixty_minutes(int price_sixty_minutes) {
        this.price_sixty_minutes = price_sixty_minutes;
    }

    public int getAddprice_large_size() {
        return addprice_large_size;
    }

    public void setAddprice_large_size(int addprice_large_size) {
        this.addprice_large_size = addprice_large_size;
    }

    public int getAddprice_one_dog() {
        return addprice_one_dog;
    }

    public void setAddprice_one_dog(int addprice_one_dog) {
        this.addprice_one_dog = addprice_one_dog;
    }

    public int getAddprice_holiday() {
        return addprice_holiday;
    }

    public void setAddprice_holiday(int addprice_holiday) {
        this.addprice_holiday = addprice_holiday;
    }
}
