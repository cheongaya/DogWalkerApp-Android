package com.example.dogwalker.retrofit.ResponseBody;

public class UserDTO {

    public final String userID;
    public final String userPW;
    public final String userName;
    public final String userPhoneNumber;
//
//    public String getUserID() {
//        return userID;
//    }
//
//    public void setUserID(String userID) {
//        this.userID = userID;
//    }
//
//    public String getUserPW() {
//        return userPW;
//    }
//
//    public void setUserPW(String userPW) {
//        this.userPW = userPW;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getUserPhoneNumber() {
//        return userPhoneNumber;
//    }
//
//    public void setUserPhoneNumber(String userPhoneNumber) {
//        this.userPhoneNumber = userPhoneNumber;
//    }

    public UserDTO(String userID, String userPW, String userName, String userPhoneNumber) {
        this.userID = userID;
        this.userPW = userPW;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
    }
}
