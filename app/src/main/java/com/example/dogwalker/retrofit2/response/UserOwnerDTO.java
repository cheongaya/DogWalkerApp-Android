package com.example.dogwalker.retrofit2.response;

public class UserOwnerDTO {

    public final String ownerID;
    public final String ownerPW;
    public final String ownerName;
    public final String ownerPhoneNumber;

    public UserOwnerDTO(String ownerID, String ownerPW, String ownerName, String ownerPhoneNumber) {
        this.ownerID = ownerID;
        this.ownerPW = ownerPW;
        this.ownerName = ownerName;
        this.ownerPhoneNumber = ownerPhoneNumber;
    }

}
