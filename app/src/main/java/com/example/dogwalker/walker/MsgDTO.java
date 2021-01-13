package com.example.dogwalker.walker;

public class MsgDTO {

    public static final String TYPE_TEXT = "TEXT";     //채팅메세지가 텍스트일 때
    public static final String TYPE_IMAGE = "IMAGE";   //채팅메세지가 이미지일 때
    public static final String TYPE_ENTER = "ENTER";   //채팅유저가 입할할 때
    public static final String TYPE_EXIT = "EXIT";     //채팅유저가 퇴장할 때

    int viewType;       //메세지 구분 -> 1은 나 / 2는 상대방 / 3은 시스템(입장,퇴장)
    String sender;      //메세지 보낸 사람
    String msgType;     //메세지 타입
    String msg;         //메시지 내용
    String reader;      //메세지 읽은 사람
    String msgTime;     //메세지 시간

    //구분, 보낸사람, 메세지타입, 메세지내용, 읽은사람, 메세지시간
    public MsgDTO(int viewType, String sender, String msgType, String msg, String reader, String msgTime) {
        this.viewType = viewType;
        this.sender = sender;
        this.msgType = msgType;
        this.msg = msg;
        this.reader = reader;
        this.msgTime = msgTime;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }
}
