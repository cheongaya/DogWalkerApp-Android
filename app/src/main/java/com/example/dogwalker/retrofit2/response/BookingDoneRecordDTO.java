package com.example.dogwalker.retrofit2.response;

import java.util.ArrayList;

public class BookingDoneRecordDTO {

    int idx;                    //산책기록번호
    int booking_id;             //산책예약번호
    String done_current_time;   //산책 끝난 시간
    String done_walking_time;   //산책한 시간
    String done_distance;       //산책한 거리
    int done_poo_count;         //배변횟수
    String done_memo;           //산책 메모
    ArrayList<String> multiFileArrayList = new ArrayList<>(); //산책 첨부파일

    public BookingDoneRecordDTO(int idx, int booking_id, String done_current_time, String done_walking_time, String done_distance, int done_poo_count, String done_memo, ArrayList<String> multiFileArrayList) {
        this.idx = idx;
        this.booking_id = booking_id;
        this.done_current_time = done_current_time;
        this.done_walking_time = done_walking_time;
        this.done_distance = done_distance;
        this.done_poo_count = done_poo_count;
        this.done_memo = done_memo;
        this.multiFileArrayList = multiFileArrayList;
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

    public ArrayList<String> getMultiFileArrayList() {
        return multiFileArrayList;
    }

    public void setMultiFileArrayList(ArrayList<String> multiFileArrayList) {
        this.multiFileArrayList = multiFileArrayList;
    }
}
