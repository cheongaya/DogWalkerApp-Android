package com.example.dogwalker.retrofit.RequestBody;

import java.util.HashMap;

/**
 * Created by sonchangwoo on 2017. 1. 6..
 */

public class RequestPut {

//    private final int userId;
//    private final int id;
//    private final String title;
//    private final String body;
//
//    public RequestPut(HashMap<String, Object> parameters) {
//        this.userId = (int) parameters.get("userId");
//        this.id = (int) parameters.get("id");
//        this.title = (String) parameters.get("title");
//        this.body = (String) parameters.get("body");
//    }

    private final String userId;
    private final String id;
    private final String title;
    private final String body;

    public RequestPut(HashMap<String, Object> parameters) {
        this.userId = (String) parameters.get("userId");
        this.id = (String) parameters.get("id");
        this.title = (String) parameters.get("title");
        this.body = (String) parameters.get("body");
    }
}
