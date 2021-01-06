package com.example.dogwalker.walker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.MsgService;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.ChatDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MsgActivity extends BaseActivity {

    private static final String TAG = "SERVICE";
    String className = getClass().getSimpleName().trim();

    private static final int PICK_FROM_ALBUM = 1001;

    RecyclerView message_list;
    EditText et_message;
    Button btn_send;

    public static Handler msghandler;

    ArrayList<MsgDTO> msgArrayList;
    MsgAdapter adapter;

    String others_id;
    String my_id;

    String roomNum;     //채팅방번호 -> @고유키
    String chatUser;    //채팅유저 (본인)
    String chatPartner;    //채팅상대방

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

//        delayHandler();
        receivedHandler();

        Intent intent = getIntent();
        roomNum = intent.getStringExtra("roomNum");
        chatUser = intent.getStringExtra("chatUser");
        chatPartner = intent.getStringExtra("chatPartner");

        my_id = chatUser;
        others_id = chatPartner;

        message_list = findViewById(R.id.recycler_view);
        et_message = findViewById(R.id.et_chat);
        btn_send = findViewById(R.id.btn_send);

        //리사이클러뷰 초기 셋팅
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        message_list.setLayoutManager(linearLayoutManager);
        msgArrayList = new ArrayList<>();
        adapter = new MsgAdapter(msgArrayList);
        message_list.setAdapter(adapter);

        //채팅 메세지 데이터 조회
        selectChatMessageToDB(roomNum, chatUser);

        btn_send.setOnClickListener(onClickListener);

        //유저 입장
        enterChatUserToServer();

//        getSocket.start();
//        readMessage.start();
    }

//    public void delayHandler(){
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                changeChatState();
//            }
//        },1500);
//    }

    //유저 입장하기
    public void enterChatUserToServer(){
        //채팅 액티비티 화면 들어오면 서버소켓으로 [채팅방 입장 = ENTER] 메세지를 보낸다 (소켓, 방번호, 보낼사람, 메세지타입, 메세지내용)
        MsgService.SendThread sendThread = new MsgService.SendThread(MsgService.socket, roomNum, chatUser, "ENTER", "enter");
        sendThread.start();

    }
    //유저 퇴장하기
    public void exitChatUserToServer(){
        //채팅 액티비티 화면 나가면 서버소켓으로 [채팅방 퇴장 = EXIT] 메세지를 보낸다 //MsgService.socket, roomNum, chatUser, "EXIT", "TEXT"
        MsgService.SendThread sendThread = new MsgService.SendThread(MsgService.socket, roomNum, chatUser, "EXIT", "exit");
        sendThread.start();
    }

    //유저 메세지보내기
    public void sendChatMsgToServer(String sendMsg){
        //서버로 메세지 보내기 //(소켓, 방번호, 보낼사람, 메세지타입, 메세지내용)
        MsgService.SendThread sendThread = new MsgService.SendThread(MsgService.socket, roomNum, chatUser, "TEXT", sendMsg);
        sendThread.start();
    }

    public void receivedHandler(){
        msghandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message message) {
//                super.handleMessage(message);
                if (message.what == 1111) {
                    makeLog("받은 메세지 : "+message.obj);
                    String[] receivedMsg = message.obj.toString().split("@#@#"); //5@#@#user1@#@#hi@#@#오전1시
                    String toRoomID = receivedMsg[0]; //방번호
                    String sender = receivedMsg[1];   //보낸유저
                    String MsgType = receivedMsg[2];  //메세지타입

                    //메세지타입 = ENTER 일때 (방번호/입장한유저/타입) -> 서버 수신 메세지
                    if(MsgType.equals("ENTER")){
                        makeLog("[채팅] 받은 메세지 타입 : "+MsgType);
                        //TODO: 안읽은 메세지 1을 모두 사라지게 해야한다 (할일)
                        //서버 수신 메세지  (구분, 보낸사람, 메세지타입, 메세지내용, 읽은사람)
//                    Chat_RecyclerItem data3 = new Chat_RecyclerItem(3, others_message, received_time);
                        MsgDTO data3 = new MsgDTO(3, sender, MsgType, "ENTER", "kcaReader");
                        msgArrayList.add(data3);
                    }
                    //메세지타입 = TEXT, IMAGE 일 때 (방번호, 유저아이디, 메세지타입, 읽은유저, 메세지, 메세지시간)
                    else {
                        makeLog("[채팅] 받은 메세지 타입 : "+MsgType);
                        // 58@#@#user3@#@#TEXT@#@#user3/@#@#hu@#@#오후 8:06
                        String reader = receivedMsg[3]; //읽은유저
                        String Msg = receivedMsg[4];    //메세지내용
                        String time = receivedMsg[5];   //메세지시간

                        //상대가 보낸 메세지일 때 (구분, 보낸사람, 메세지타입, 메세지내용, 읽은사람)
                        if(!sender.equals(ApplicationClass.currentWalkerID)){
                            MsgDTO data3 = new MsgDTO(2, sender, MsgType, Msg, reader);
                            msgArrayList.add(data3);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new MsgAdapter(msgArrayList);
                            message_list.setAdapter(adapter);
                            message_list.scrollToPosition(msgArrayList.size() - 1);
                        }
                    });
                }
            }
        };
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_send :

                    if (!et_message.getText().toString().equals("")) {
                        String my_message = et_message.getText().toString();

                        //서버로 입력한 메세지 보내기
                        sendChatMsgToServer(my_message);

                        Date time = new Date();
                        SimpleDateFormat time_format = new SimpleDateFormat("a h:mm");
                        String send_time = time_format.format(time);

                        // 구분, 보낸사람, 메세지타입, 메세지내용, 읽은사람
                        MsgDTO data = new MsgDTO(1, chatUser, "TEXT", my_message, send_time);
                        msgArrayList.add(data);
//                        adapter.notifyDataSetChanged();
                        adapter = new MsgAdapter(msgArrayList);
                        message_list.setAdapter(adapter);
                        message_list.scrollToPosition(msgArrayList.size() - 1);

                        //시작후 editText 입력창 초기화
                        et_message.setText("");

                    }
                    break;
                default:
                    break;
            }
        }
    };

    //채팅 메세지 데이터 조회
    public void selectChatMessageToDB(String roomNum, String chatUser){

        int chatRoom = Integer.valueOf(roomNum);

        //채팅 방번호 , 유저아이디
        Call<ArrayList<MsgDTO>> call = retrofitApi.selectChatMsg(chatRoom, chatUser);
        call.enqueue(new Callback<ArrayList<MsgDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<MsgDTO>> call, Response<ArrayList<MsgDTO>> response) {
                makeLog("채팅방 메세지 데이터 조회 = 성공");
                msgArrayList = response.body();    //List

                //채팅 메세지 데이터 setText 해주기 (position 번째)
                if(msgArrayList.size() > 0){

                    makeLog("채팅방 메세지 데이터 조회 != null");
//                    ArrayList<ChatDTO> chatDTOArrayList = new ArrayList<>();
//                    chatDTOArrayList.addAll(messageList);
//                    makeLog(messageList.get(0).getRead());
//                    chatAdapter.setChattingDTOArrayList(chatDTOArrayList);
//                    chatAdapter.notifyDataSetChanged();
//                    chatRecyclerView.scrollToPosition(messageList.size() -1); //스크롤 맨 아래로
                    adapter = new MsgAdapter(msgArrayList);
                    message_list.setAdapter(adapter);
                    message_list.scrollToPosition(msgArrayList.size() - 1);


                }else{
                    //검색된 도그워커 데이터가 없을때
                    makeLog("채팅방 메세지 데이터 조회 = null");
                }
            }
            @Override
            public void onFailure(Call<ArrayList<MsgDTO>> call, Throwable t) {
                makeLog("채팅방 메세지 데이터 조회 = 실패");
                makeLog(t.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //유저 퇴장하기
        exitChatUserToServer();
    }

    //로그 : 액티비티명 + 함수명 + 원하는 데이터를 한번에 보기위한 로그
    public void makeLog(String strData) {
        Log.d(TAG, className + "_[채팅] " + strData);
    }

}