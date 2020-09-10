package com.example.dogwalker.retrofit2.response;

public class WalkerLocationDTO {

    String id;
    int satisfation_score;
    String latitude;
    String longitude;

    public WalkerLocationDTO(String id, int satisfation_score, String latitude, String longitude) {
        this.id = id;
        this.satisfation_score = satisfation_score;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSatisfation_score() {
        return satisfation_score;
    }

    public void setSatisfation_score(int satisfation_score) {
        this.satisfation_score = satisfation_score;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
