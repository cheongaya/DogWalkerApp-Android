package com.example.dogwalker.retrofit2.response;

public class MsgDTO {

    public static final String TYPE_TEXT = "TEXT";     //채팅메세지가 텍스트일 때
    public static final String TYPE_IMAGE = "IMAGE";   //채팅메세지가 이미지일 때
//    public static final String TYPE_FILE = "FILE";     //채팅메세지가 파일일 때
//    public static final String TYPE_ENTER = "ENTER";   //채팅유저가 입장할 때
//    public static final String TYPE_EXIT = "EXIT";     //채팅유저가 퇴장할 때

    //전 : 메세지내용, 보낸사람, 메세지타입, 읽음
    //방번호, 유저아이디, 메세지타입, 읽은유저, 메세지, 메세지시간

    String content; //메세지
    String sender;  //메세지 보낸 사람
    String type;    //메세지 타입
    String read;    //읽음
    boolean sendByMyself;

}
