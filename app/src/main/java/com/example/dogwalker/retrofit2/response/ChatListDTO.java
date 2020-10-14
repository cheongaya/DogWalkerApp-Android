package com.example.dogwalker.retrofit2.response;

public class ChatListDTO {

    String chatImg;  //채팅 프로필 이미지
    String chatName; //채팅 이름
    String chatText; //채팅 텍스트
    String chatDate; //채팅 날짜

    public ChatListDTO(String chatImg, String chatName, String chatText, String chatDate) {
        this.chatImg = chatImg;
        this.chatName = chatName;
        this.chatText = chatText;
        this.chatDate = chatDate;
    }

    public String getChatImg() {
        return chatImg;
    }

    public void setChatImg(String chatImg) {
        this.chatImg = chatImg;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }
}
