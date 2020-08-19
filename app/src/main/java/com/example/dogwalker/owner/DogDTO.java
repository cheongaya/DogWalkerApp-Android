package com.example.dogwalker.owner;

public class DogDTO {

    String profile_img;
    String name;
    int birthday_year;
    int birthday_month;
    int birthday_day;
    String sex;
    String size;
    String weight;
    String kind;
    String number;

    public DogDTO(String profile_img, String name, int birthday_year, int birthday_month, int birthday_day, String sex, String size, String weight, String kind, String number) {
        this.profile_img = profile_img;
        this.name = name;
        this.birthday_year = birthday_year;
        this.birthday_month = birthday_month;
        this.birthday_day = birthday_day;
        this.sex = sex;
        this.size = size;
        this.weight = weight;
        this.kind = kind;
        this.number = number;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthday_year() {
        return birthday_year;
    }

    public void setBirthday_year(int birthday_year) {
        this.birthday_year = birthday_year;
    }

    public int getBirthday_month() {
        return birthday_month;
    }

    public void setBirthday_month(int birthday_month) {
        this.birthday_month = birthday_month;
    }

    public int getBirthday_day() {
        return birthday_day;
    }

    public void setBirthday_day(int birthday_day) {
        this.birthday_day = birthday_day;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
