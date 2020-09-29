package com.example.dogwalker.retrofit2.response;

public class BookingDoneRecordDTO {

    int idx;
    int booking_id;             //예약번호
    String done_current_time;   //산책 끝난 시간
    String done_walking_time;   //산책한 시간
    String done_distance;       //산책한 거리
    int done_poo_count;         //배변횟수
    String done_memo;           //산책 메모
    String done_upload_img;     //산책 사진

    public BookingDoneRecordDTO(int idx, int booking_id, String done_current_time, String done_walking_time, String done_distance, int done_poo_count, String done_memo, String done_upload_img) {
        this.idx = idx;
        this.booking_id = booking_id;
        this.done_current_time = done_current_time;
        this.done_walking_time = done_walking_time;
        this.done_distance = done_distance;
        this.done_poo_count = done_poo_count;
        this.done_memo = done_memo;
        this.done_upload_img = done_upload_img;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public String getDone_current_time() {
        return done_current_time;
    }

    public void setDone_current_time(String done_current_time) {
        this.done_current_time = done_current_time;
    }

    public String getDone_walking_time() {
        return done_walking_time;
    }

    public void setDone_walking_time(String done_walking_time) {
        this.done_walking_time = done_walking_time;
    }

    public String getDone_distance() {
        return done_distance;
    }

    public void setDone_distance(String done_distance) {
        this.done_distance = done_distance;
    }

    public int getDone_poo_count() {
        return done_poo_count;
    }

    public void setDone_poo_count(int done_poo_count) {
        this.done_poo_count = done_poo_count;
    }

    public String getDone_memo() {
        return done_memo;
    }

    public void setDone_memo(String done_memo) {
        this.done_memo = done_memo;
    }

    public String getDone_upload_img() {
        return done_upload_img;
    }

    public void setDone_upload_img(String done_upload_img) {
        this.done_upload_img = done_upload_img;
    }
}
