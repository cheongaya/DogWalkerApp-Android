package com.example.dogwalker.retrofit2.response;

public class ChatDTO {

    public static final String TYPE_TEXT = "TEXT";     //채팅메세지가 텍스트일 때
    public static final String TYPE_IMAGE = "IMAGE";   //채팅메세지가 이미지일 때
    public static final String TYPE_FILE = "FILE";     //채팅메세지가 파일일 때
    public static final String TYPE_ENTER = "ENTER";   //채팅유저가 입할할 때
    public static final String TYPE_EXIT = "EXIT";     //채팅유저가 퇴장할 때

    String content; //메세지
    String sender;  //메세지 보낸 사람
    String type;    //메세지 타입
    String read;    //읽음
    boolean sendByMyself;

//    public ChatDTO(String content, String sender, String type, boolean sendByMyself) {
//        this.content = content;
//        this.sender = sender;
//        this.type = type;
//        this.sendByMyself = sendByMyself;
//    }


    public ChatDTO(String content, String sender, String type, String read, boolean sendByMyself) {
        this.content = content;
        this.sender = sender;
        this.type = type;
        this.read = read;
        this.sendByMyself = sendByMyself;
    }

    public static String getTypeText() {
        return TYPE_TEXT;
    }

    public static String getTypeImage() {
        return TYPE_IMAGE;
    }

    public static String getTypeFile() {
        return TYPE_FILE;
    }

    public static String getTypeEnter() {
        return TYPE_ENTER;
    }

    public static String getTypeExit() {
        return TYPE_EXIT;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public boolean isSendByMyself() {
        return sendByMyself;
    }

    public void setSendByMyself(boolean sendByMyself) {
        this.sendByMyself = sendByMyself;
    }
}
