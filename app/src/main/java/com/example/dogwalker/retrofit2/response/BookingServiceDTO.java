package com.example.dogwalker.retrofit2.response;

public class BookingServiceDTO {

    int idx;
    String walker_id;
    String owner_id;
    String owner_dog_name;
    int walk_total_time;
    String walk_date;
    String walk_time;
    String walking_status;

    public BookingServiceDTO(int idx, String walker_id, String owner_id, String owner_dog_name, int walk_total_time, String walk_date, String walk_time, String walking_status) {
        this.idx = idx;
        this.walker_id = walker_id;
        this.owner_id = owner_id;
        this.owner_dog_name = owner_dog_name;
        this.walk_total_time = walk_total_time;
        this.walk_date = walk_date;
        this.walk_time = walk_time;
        this.walking_status = walking_status;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getWalker_id() {
        return walker_id;
    }

    public void setWalker_id(String walker_id) {
        this.walker_id = walker_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_dog_name() {
        return owner_dog_name;
    }

    public void setOwner_dog_name(String owner_dog_name) {
        this.owner_dog_name = owner_dog_name;
    }

    public int getWalk_total_time() {
        return walk_total_time;
    }

    public void setWalk_total_time(int walk_total_time) {
        this.walk_total_time = walk_total_time;
    }

    public String getWalk_date() {
        return walk_date;
    }

    public void setWalk_date(String walk_date) {
        this.walk_date = walk_date;
    }

    public String getWalk_time() {
        return walk_time;
    }

    public void setWalk_time(String walk_time) {
        this.walk_time = walk_time;
    }

    public String getWalking_status() {
        return walking_status;
    }

    public void setWalking_status(String walking_status) {
        this.walking_status = walking_status;
    }
}
