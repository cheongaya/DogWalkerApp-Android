package com.example.dogwalker.retrofit2.response;

public class ChatListDTO {

    String roomNum;
    String chatUser;    //채팅 유저 (본인)
    String chatPartner; //채팅 상대방
    String chatLastMsg; //채팅 마지막 메세지
    String chatDate;
    int chatReadNum; //채팅 안읽은 메세지 숫자

    public ChatListDTO(String roomNum, String chatUser, String chatPartner, String chatLastMsg, String chatDate, int chatReadNum) {
        this.roomNum = roomNum;
        this.chatUser = chatUser;
        this.chatPartner = chatPartner;
        this.chatLastMsg = chatLastMsg;
        this.chatDate = chatDate;
        this.chatReadNum = chatReadNum;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
    }

    public String getChatUser() {
        return chatUser;
    }

    public void setChatUser(String chatUser) {
        this.chatUser = chatUser;
    }

    public String getChatPartner() {
        return chatPartner;
    }

    public void setChatPartner(String chatPartner) {
        this.chatPartner = chatPartner;
    }

    public String getChatLastMsg() {
        return chatLastMsg;
    }

    public void setChatLastMsg(String chatLastMsg) {
        this.chatLastMsg = chatLastMsg;
    }

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }

    public int getChatReadNum() {
        return chatReadNum;
    }

    public void setChatReadNum(int chatReadNum) {
        this.chatReadNum = chatReadNum;
    }
}
