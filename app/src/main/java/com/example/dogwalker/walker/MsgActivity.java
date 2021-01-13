package com.example.dogwalker.walker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.MsgService;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.ChatDTO;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MsgActivity extends BaseActivity {

    private static final String TAG = "SERVICE";
    String className = getClass().getSimpleName().trim();

    private static final int PICK_FROM_ALBUM = 1001;

    RecyclerView message_list;
    TextView tv_title;
    EditText et_message;
    Button btn_send;
    ImageButton btn_img;

    public static Handler msghandler;

    ArrayList<MsgDTO> msgArrayList;
    MsgAdapter adapter;

    String others_id;
    String my_id;

    String roomNum;             //채팅방번호 -> @고유키
    String chatUser;            //채팅유저 (본인)
    String chatPartner;         //채팅상대방
    String chatPartnerProfile; //채팅상대방 프로필 이미지

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
        chatPartnerProfile = intent.getStringExtra("chatPartnerProfile");
//        makeToast(chatPartnerProfile);

        my_id = chatUser;
        others_id = chatPartner;

        message_list = findViewById(R.id.recycler_view);
        tv_title = findViewById(R.id.textView4);
        et_message = findViewById(R.id.et_chat);
        btn_send = findViewById(R.id.btn_send);
        btn_img = findViewById(R.id.btn_img);

        //채팅방 타이틀
        tv_title.setText(chatPartner+" 채팅방");

        //리사이클러뷰 초기 셋팅
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        message_list.setLayoutManager(linearLayoutManager);
        msgArrayList = new ArrayList<>();
        adapter = new MsgAdapter(msgArrayList, this);
        message_list.setAdapter(adapter);

        //상대방 프로필 이미지
        adapter.chatPartnerProfile = ApplicationClass.BASE_URL+chatPartnerProfile;

        //채팅 메세지 데이터 조회
        selectChatMessageToDB(roomNum, chatUser);

        btn_send.setOnClickListener(onClickListener);
        btn_img.setOnClickListener(onClickListener);

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

    //유저 이미지보내기
    public void sendChatImgToServer(String sendImg){
        MsgService.SendThread sendThread = new MsgService.SendThread(MsgService.socket, roomNum, chatUser, "IMAGE", sendImg);
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
                        MsgDTO data3 = new MsgDTO(3, sender, MsgType, "ENTER", "999", "time");
                        msgArrayList.add(data3);
                        //채팅방 입장했을 때 실시간 읽음 처리 (나/상대방 둘다)
                        if(!sender.equals(ApplicationClass.currentWalkerID)){
                            for(int i=0; i<msgArrayList.size(); i++){
                                msgArrayList.get(i).setReader("user/user/");
                            }
                        }else if(sender.equals(ApplicationClass.currentWalkerID)){
                            for(int i=0; i<msgArrayList.size(); i++){
                                msgArrayList.get(i).setReader("user/user/");
                            }
                        }
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
                            MsgDTO data3 = new MsgDTO(2, sender, MsgType, Msg, reader, time);
                            msgArrayList.add(data3);
                        }else{
                            MsgDTO data3 = new MsgDTO(1, sender, MsgType, Msg, reader, time);
                            msgArrayList.add(data3);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new MsgAdapter(msgArrayList, getApplicationContext());
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

//                        Date time = new Date();
//                        SimpleDateFormat time_format = new SimpleDateFormat("a h:mm");
//                        String send_time = time_format.format(time);
//
//                        // 구분, 보낸사람, 메세지타입, 메세지내용, 읽은사람
//                        MsgDTO data = new MsgDTO(1, chatUser, "TEXT", my_message, chatUser+"/", send_time);
//                        msgArrayList.add(data);
////                        adapter.notifyDataSetChanged();
//                        adapter = new MsgAdapter(msgArrayList, getApplicationContext());
//                        message_list.setAdapter(adapter);
//                        message_list.scrollToPosition(msgArrayList.size() - 1);

                        //시작후 editText 입력창 초기화
                        et_message.setText("");

                    }
                    break;
                case R.id.btn_img :
                    //카메라&사진 권한 승인 후 사진 추가 다이얼로그 띄움
                    tedPermission();
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
                    adapter = new MsgAdapter(msgArrayList, getApplicationContext());
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

    //카메라&앨범에 관한 권한 허용을 사용자로부터 받는 메소드
    public void tedPermission(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //권한 요청 성공
//                makeToast("권한 요청 성공");

                //사진 추가 다이얼로그 띄우기
                addImgAlertDialog();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //권한 요청 실패
//                makeToast("권한 요청 실패");
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    //사진 추가할때 카메라 or 앨범 선택 다이얼로그
    public void addImgAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("채팅 사진")
                .setMessage("채팅 사진 보내기");

        builder.setPositiveButton("앨범", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //앨범에서 사진 가져오기
                getImageFromAlbum();
            }
        }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setNegativeButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //카메라에서 사진 가져오기
//                getImageFromCamera();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //앨범에서 이미지 가져오는 메소드
    public void getImageFromAlbum(){
        //인텐트를 통해 앨범 화면으로 이동시켜줌
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        intent.setAction(Intent.ACTION_GET_CONTENT);    //추가 코드
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {

            case PICK_FROM_ALBUM:

                //앨범에서 getData Uri 받아온 후
                Uri photoUri = data.getData();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "photoUri : " + photoUri);
                //photoUri 값을 경로 변환 -> File 객체 생성해주는 메소드에 보내준다
                MultipartBody.Part body = applicationClass.updateAlbumImgToServer(photoUri, "uploaded_file");

                //위 메소드의 return 값은 return body(MultipartBody.Part) 형태로 반환된다
                Call<ResultDTO> call = retrofitApi.insertChatImageData(body);
                call.enqueue(new Callback<ResultDTO>() {
                    @Override
                    public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                        ResultDTO resultDTO = response.body();
                        String resultDataStr = resultDTO.getResponceResult();
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "채팅이미지 서버 저장 성공");
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", resultDataStr);

                        //resultDataStr = common/chat/photo-1526047932273-341f2a7631f9.jpeg (반환값 = 서버에 저장된 이미지 경로)
                        String sendImg = ApplicationClass.BASE_URL + resultDataStr;

                        makeLog("보낼 이미지 : "+sendImg);
                        //서버소켓에 채팅 메세지 이미지 URL 보내기
                        sendChatImgToServer(sendImg);
                    }

                    @Override
                    public void onFailure(Call<ResultDTO> call, Throwable t) {
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "채팅이미지 서버 저장 실패");
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
                    }
                });
                break;

            default:
                break;
        }
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