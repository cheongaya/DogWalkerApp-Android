package com.example.dogwalker.retrofit.ResponseBody;

/**
 * Created by sonchangwoo on 2017. 1. 6..
 */

public class ResponseGet {

    public final String userId;
    public final String id;
    public final String title;
    public final String body;

    public ResponseGet(String userId, String id, String title, String body) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
    }

}
