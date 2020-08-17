package com.example.dogwalker.retrofit2.response;

public class NonServiceDateDTO {

    String walker_id;
    String date;
    String time;

    public NonServiceDateDTO(String walker_id, String date, String time) {
        this.walker_id = walker_id;
        this.date = date;
        this.time = time;
    }

    public String getWalker_id() {
        return walker_id;
    }

    public void setWalker_id(String walker_id) {
        this.walker_id = walker_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
