package com.example.dogwalker.walker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Application;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.ChatService;
import com.example.dogwalker.MsgService;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerChattingBinding;
import com.example.dogwalker.retrofit2.response.ChatDTO;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerChattingActivity extends BaseActivity {

    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    ActivityWalkerChattingBinding binding;

    private static final int PICK_FROM_ALBUM = 1001;
    private static final int PICK_FROM_CAMERA = 1002;

//    public static String serverIP = "192.168.1.17"; //서버 연결 IP (남성집 Wifi)
//    public static String serverIP = "52.78.138.74"; //서버 연결 IP (AWS 서버)
//    public static String serverPort = "3333"; //서버 연결 Port

//    ChatClient chatClient; //채팅클라이언트 객체
    Socket socket;

    String roomNum;     //채팅방번호 -> @고유키
    String chatUser;    //채팅유저 (본인)
    String chatPartner;    //채팅상대방

    TextView tvChatTitle, tvChatShowMsg;
    EditText etChatMsg;
    Button btnConnect, btnSendMsg, btnSendImg;

    public static Handler msghandler;
//    ReceiveThread receiveThread;
//    SendThread sendThread;

    //TODO: 2020.12.24 06:53 채팅 메세지 리사이클러뷰로 보여주기 작업 시작
    //리사이클러뷰 관련 변수 aa
    private List<ChatDTO> messageList = new ArrayList<>();
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_chatting);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_chatting);
        binding.setActivity(this);

        Intent intent = getIntent();
        roomNum = intent.getStringExtra("roomNum");
        chatUser = intent.getStringExtra("chatUser");
        chatPartner = intent.getStringExtra("chatPartner");

        tvChatTitle = (TextView)findViewById(R.id.textView_chat_title); //채팅방 타이틀
        etChatMsg = (EditText)findViewById(R.id.editText_msg);          //채팅 메세지 입력하는 부분
        btnSendMsg = (Button)findViewById(R.id.button_send_msg);        //서버에 메세지 보내는 버튼
        btnSendImg = (Button)findViewById(R.id.button_send_img);        //채팅 이미지 보내는 버튼

        tvChatTitle.setText(roomNum+"번방 "+chatUser+"이 "+chatPartner+"와 채팅");

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        //채팅 메세지 데이터 조회
        selectChatMessageToDB(roomNum, chatUser);

        //메세지 받는 핸들러 생성
        setMsghandler();

        //채팅 액티비티 화면 들어오면 서버소켓으로 [채팅방 입장 = ENTER] 메세지를 보낸다 (소켓, 방번호, 보낼사람, 메세지타입, 메세지내용)
        MsgService.SendThread sendThread = new MsgService.SendThread(MsgService.socket, roomNum, chatUser, "ENTER", "enter");
        sendThread.start();

        //버튼 - 전송 클릭시 이벤트 (입력한 메세지를 전송한다)
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeLog("[채팅] 메세지를 전송함");

                //입력창의 메세지가 null 이 아닐시
                if (etChatMsg.getText().toString() != null) {

                    String sendMsg = etChatMsg.getText().toString();

                    makeLog("[채팅] 보낼 메세지 : "+sendMsg);
                    //서버로 메세지 보내기 //(소켓, 방번호, 보낼사람, 메세지타입, 메세지내용)
                    MsgService.SendThread sendThread = new MsgService.SendThread(MsgService.socket, roomNum, chatUser, "TEXT", sendMsg);
                    sendThread.start();

                    //시작후 editTextMessage 입력창 초기화
                    etChatMsg.setText("");
                }
            }
        });

        //버튼 - 이미지 클릭시 이벤트 (이미지를 전송한다)
        btnSendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //카메라&사진 권한 승인 후 사진 추가 다이얼로그 띄움
                tedPermission();
            }
        });
    }

    //메세지 핸들러
    public void setMsghandler(){
        // @ 채팅 메세지 화면에 보여주는 코드
        // ReceiveThread 를 통해서 받은 메세지를 Handler로 MainThread에서 처리(UI 변경을 하기위해)
        msghandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 1111) {
//                    tvChatShowMsg.append(message.obj.toString() + "\n");
                    makeLog("받은 메세지 : "+message.obj);

                    String[] receivedMsg = message.obj.toString().split("@#@#"); //5@#@#user1@#@#hi@#@#오전1시
                    String toRoomID = receivedMsg[0]; //방번호
                    String sender = receivedMsg[1];   //보낸유저
                    String MsgType = receivedMsg[2];  //메세지타입

                    //메세지타입 = ENTER 일때 (방번호/입장한유저/타입)
                    if(MsgType.equals("ENTER")){
                        makeLog("[채팅] 받은 메세지 타입 : "+MsgType);
//                        selectChatMessageToDB(roomNum, chatUser);
                    }
                    //메세지타입 = TEXT, IMAGE 일 때 (방번호, 유저아이디, 메세지타입, 읽은유저, 메세지, 메세지시간)
                    else {
                        makeLog("[채팅] 받은 메세지 타입 : "+MsgType);
                        // 58@#@#user3@#@#TEXT@#@#user3/@#@#hu@#@#오후 8:06
                        String reader = receivedMsg[3]; //읽은유저
                        String Msg = receivedMsg[4];    //메세지내용
                        String time = receivedMsg[5];   //메세지시간

                        //받은 채팅 메세지가 리사이클러뷰에 추가되는 형식으로 구현
                        ChatDTO chat = new ChatDTO(Msg, sender, MsgType, reader, chatUser.equals(sender));
//                        messageList.add(chat);
                        chatAdapter.addItem(chat);
                        chatAdapter.notifyItemInserted(chatAdapter.chatMessageList.size() -1);
                        chatRecyclerView.scrollToPosition(chatAdapter.chatMessageList.size() -1);
                    }
                }
            }
        };
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){

        chatRecyclerView = findViewById(R.id.recyclerView_chat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
//        chatAdapter = new ChatAdapter(messageList, this);
        chatAdapter = new ChatAdapter(messageList, this);
        chatRecyclerView.setAdapter(chatAdapter);
    }

    //채팅 메세지 데이터 조회
    public void selectChatMessageToDB(String roomNum, String chatUser){

        int chatRoom = Integer.valueOf(roomNum);

        //채팅 방번호 , 유저아이디
        Call<List<ChatDTO>> call = retrofitApi.selectChatMessage(chatRoom, chatUser);
        call.enqueue(new Callback<List<ChatDTO>>() {
            @Override
            public void onResponse(Call<List<ChatDTO>> call, Response<List<ChatDTO>> response) {
                makeLog("채팅방 메세지 데이터 조회 = 성공");
                messageList = response.body();    //List

                //채팅 메세지 데이터 setText 해주기 (position 번째)
                if(messageList.size() > 0){

                    makeLog("채팅방 메세지 데이터 조회 != null");

                    ArrayList<ChatDTO> chatDTOArrayList = new ArrayList<>();
                    chatDTOArrayList.addAll(messageList);
//
                    makeLog(messageList.get(0).getRead());
//                    makeLog(chatDTOArrayList.get(0).read);

                    chatAdapter.setChattingDTOArrayList(chatDTOArrayList);
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(messageList.size() -1); //스크롤 맨 아래로

                }else{
                    //검색된 도그워커 데이터가 없을때
                    makeLog("채팅방 메세지 데이터 조회 = null");
                }
            }

            @Override
            public void onFailure(Call<List<ChatDTO>> call, Throwable t) {
                makeLog("채팅방 메세지 데이터 조회 = 실패");
                makeLog(t.toString());
            }
        });

    }

//    class ChatClient extends Thread {
//
//        boolean threadAlive;
//        String serverIp;
//        String serverPort;
//        String roomNum;
//
//        BufferedReader bufferedReader;
//        DataInputStream dataInputStream;
//        DataOutputStream dataOutputStream;
//
//        public ChatClient(String ip, String port, String roomNum) {
//            threadAlive = true;
//
//            this.serverIp = ip; //서버 IP
//            this.serverPort = port; //서버 PORT
//            this.roomNum = roomNum;  //방숫자 키
//        }
//
//        @Override
//        public void run() {
//            try {
//                // 연결후 바로 ReceiveThread 시작
//                socket = new Socket(serverIp, Integer.parseInt(serverPort));
//
//                bufferedReader = new BufferedReader(new InputStreamReader(System.in));
////                dataInputStream = new DataInputStream(socket.getInputStream());
//                dataOutputStream = new DataOutputStream(socket.getOutputStream());
//
//                //접속 버튼 클릭시 -> 서버로 방번호 고유키 전송 //@1번방 접속
//                dataOutputStream.writeUTF(roomNum+"@#@#"+chatUser);
////                dataOutputStream.writeUTF("이름");
////                tvChatTitle.setText(roomNum+"번방 "+chatUser+" 접속");
//                tvChatTitle.setText(roomNum+"번방 "+chatUser+"이 "+chatPartner+"와 채팅 //접속");
//                makeLog(chatUser+"가 "+roomNum+"번방에 접속");
//
//                //데이터 받기 쓰레드 실행
//                receiveThread = new ReceiveThread(socket);
//                receiveThread.start();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //메세지 받기 쓰레드
//    class ReceiveThread extends Thread {
//
//        Socket socket;
//        DataInputStream dataInputStream;
//
//        public ReceiveThread(Socket socket) {
//            this.socket = socket;
//
//            try {
//                dataInputStream = new DataInputStream(socket.getInputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//        // 서버에서 메세지 받으면 Handler 로 해당 메세지 전달
//        public void run() {
//            while (true) {
//                //서버로부터 메세지를 읽어온다
//                String receiveMsg = null;
//                try {
//                    receiveMsg = dataInputStream.readUTF(); //TODO: 이부분에서 반복문이 계속 돈다 이것도 한번 봐야한다
//                    makeLog("받은 메세지 : "+receiveMsg);
//
//                    if (receiveMsg != null) {
//
////                        if(receiveMsg.equals("exit") || receiveMsg.equals("wrong password")){
////                            makeLog("연결 끊김 - exit 나가기 !!");
////                            return;
////                        }
//
//                        Message message = msghandler.obtainMessage();
//                        message.what = 1111;
//                        message.obj = receiveMsg;
//                        msghandler.sendMessage(message);
//
//                    }else {
//                        makeLog("error 111");
//                        break;
//                    }
//
//                } catch (IOException e) {
//                    makeLog("error 222");
//                    e.printStackTrace();
//                    try {
////                        socket.close();
//                        dataInputStream.close(); //dataInputStream 안닫아주면 계속 반복문 돈다!!
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                    break;
//                }
//            }
//        }
//    }
//
//    class SendThread extends Thread {
//
//        Socket socket;
//        String sendUser;
//        String sendMsg;
//        String typeMsg; //메세지 타입
//        DataOutputStream dataOutputStream;
//
//        public SendThread(Socket socket, String sendUser, String sendMsg, String typeMsg) {
//            this.socket = socket;
//            this.sendUser = sendUser;
//            this.sendMsg = sendMsg;
//            this.typeMsg = typeMsg;
//
//            try {
//                //서버로 보낼 메세지
//                dataOutputStream = new DataOutputStream(socket.getOutputStream());
//            } catch (Exception e) {
//            }
//        }
//
//        public void run() {
//
////            while (true) { //반복문 도는 이유는 여기에 있돠!!!!
//            //채팅 메세지를 서버로 보낸다
//            try {
//                //채팅 보내는 시간
//                Date time = new Date();
//                SimpleDateFormat time_format = new SimpleDateFormat("a h:mm");
//                String send_time = time_format.format(time); //메세지 보내는 시간
//
////                        String msg = bufferedReader.readLine();
//
//                // @채팅 메세지 서버로 출력
////                    dataOutputStream.writeUTF(roomNum+"@#@#"+sendMsg+ "@#@#" + send_time); //5@#@#hi@#@#오전1시
//                dataOutputStream.writeUTF(roomNum+"@#@#"+sendUser+"@#@#"+sendMsg+"@#@#"+typeMsg+"@#@#"+send_time); //5@#@#user1@#@#hi@#@#오전1시
//                dataOutputStream.flush();
//
//                makeLog("보낼 메세지 최종 : "+sendMsg);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
////            } //반복문 도는 이유는 여기에 있돠!!!!
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //채팅방 나가기
//        stopClientSocket();

        //채팅 액티비티 화면 나가면 서버소켓으로 [채팅방 퇴장 = EXIT] 메세지를 보낸다 //MsgService.socket, roomNum, chatUser, "EXIT", "TEXT"
        MsgService.SendThread sendThread = new MsgService.SendThread(MsgService.socket, roomNum, chatUser, "EXIT", "exit");
        sendThread.start();
    }

//    //채팅방 나가기 - 클라이언트 소켓 종료 (연결 끊기)
//    public void stopClientSocket() {
//        try {
//
//            //퇴장 메세지 보내는 쓰레드 시작
//            sendThread = new SendThread(socket, chatUser, "exit", "TEXT"); //소켓, 채팅(보내는)유저, 채팅메세지
//            sendThread.start();
//
//            makeLog("[채팅방 나기기]");
//
//            //Socket이 null이 아니고, 현재 닫혀있지 않으면
//            if (socket != null && !socket.isClosed()) {
//
//                sendThread.dataOutputStream.close();
//                receiveThread.dataInputStream.close(); //dataInputStream 안닫아주면 계속 반복문 돈다!!
//                socket.close();
//                makeLog("[연결 끊음]");
//            }
//        } catch (IOException e) { }
//    }

    //로그 : 액티비티명 + 함수명 + 원하는 데이터를 한번에 보기위한 로그
    public void makeLog(String strData) {
        Log.d(TAG, className + "_" + new Object() {}.getClass().getEnclosingMethod().getName() + "_" + strData);
    }

    //토스트메세지 : 귀찮음을 없애기 위해 토스트를 띄우는 함수를 만듦
    public void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
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

                        //이미지 로딩 라이브러리 (반려인 프로필 사진 변경)
//                        profileImg.setImageURI(data.getData());

                        String sendImg = ApplicationClass.BASE_URL + resultDataStr;

                        makeLog("보낼 이미지 : "+sendImg);
                        //메세지 보내는 쓰레드 시작
//                        sendThread = new SendThread(socket, chatUser, sendImg, typeMsg); //소켓, 채팅(보내는)유저, 채팅메세지
//                        sendThread.start();
                        //채팅 액티비티 화면 나가면 서버소켓으로 [채팅방 퇴장 = EXIT] 메세지를 보낸다
                        MsgService.SendThread sendThread = new MsgService.SendThread(MsgService.socket, roomNum, chatUser, "IMAGE", sendImg);
                        sendThread.start();
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




}