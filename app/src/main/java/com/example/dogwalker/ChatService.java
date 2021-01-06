package com.example.dogwalker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.dogwalker.owner.OwnerChatListActivity;
import com.example.dogwalker.walker.WalkerChatListActivity;
import com.example.dogwalker.walker.WalkerChattingActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatService extends Service {

    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    public static String serverIP = "52.78.138.74"; //서버 연결 IP (AWS 서버)
    public static String serverPort = "3333"; //서버 연결 Port

    ChatClient chatClient; //채팅클라이언트 객체
    public static Socket socket;

    public static ReceiveThread receiveThread;
    public static SendThread sendThread;

    String chatUser, roomArr;

    public ChatService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //서비스가 최초 생성될 때 한번 호출된다
        makeLog("[서비스] onCreate()");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //앱의 다른 구성요소에서 서비스를 실행하면 이 함수가 호출된다
        //이 함수가 호출되면 서비스가 시작된 것이며, 백그라운드에서 작업을 수행한다
        makeLog("[서비스] onStartCommand()");

        if(intent == null){
//            return Service.START_NOT_STICKY;    //서비스가 종료 시 자동으로 재시작 옵션
            makeLog("[서비스] 인텐트 == null");
        }else{
            //액티비티에서 데이터를 받음
            String command = intent.getStringExtra("command");
            chatUser = intent.getStringExtra("chatUser");
            roomArr = intent.getStringExtra("roomArr");
            makeLog("[서비스] 받은 메세지 : "+command + "@#@#"+chatUser + "@#@#"+roomArr);

            //소켓 연결 시킴
            //Client 연결부
//            chatClient = new ChatActivity.ChatClient(serverIP, serverPort, roomNum);
            chatClient = new ChatClient(serverIP, serverPort, roomArr);
            chatClient.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //서비스가 소멸될 때 호출된다
        makeLog("[서비스] onDestroy()");
        //소켓 종료
        stopClientSocket();
    }

    //로그 : 액티비티명 + 함수명 + 원하는 데이터를 한번에 보기위한 로그
    public void makeLog(String strData) {
        Log.d(TAG, className + "_" + strData);
    }

    //TODO : 소켓 관련 코드
    class ChatClient extends Thread {

        boolean threadAlive;
        String serverIp;
        String serverPort;
        String roomNum; //roomArr = 룸어레이

        BufferedReader bufferedReader;
        DataInputStream dataInputStream;
        DataOutputStream dataOutputStream;

        public ChatClient(String ip, String port, String roomNum) {
            threadAlive = true;

            this.serverIp = ip; //서버 IP
            this.serverPort = port; //서버 PORT
            this.roomNum = roomNum;  //방숫자 키 = roomArr
        }

        @Override
        public void run() {
            try {
                // 연결후 바로 ReceiveThread 시작
                socket = new Socket(serverIp, Integer.parseInt(serverPort));

                bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                //접속 버튼 클릭시 -> 서버로 방번호 고유키 전송 //@1번방 접속
                dataOutputStream.writeUTF(roomNum+"@#@#"+chatUser); // 58/59/@#@#user3
                makeLog(chatUser+"가 "+roomNum+"번방에 접속");

                //데이터 받기 쓰레드 실행
                receiveThread = new ReceiveThread(socket);
                receiveThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //메세지 받기 쓰레드
    class ReceiveThread extends Thread {

        Socket socket;
        DataInputStream dataInputStream;

        public ReceiveThread(Socket socket) {
            this.socket = socket;

            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        // 서버에서 메세지 받으면 Handler 로 해당 메세지 전달
        public void run() {
            while (true) {
                //서버로부터 메세지를 읽어온다
                String receiveMsg = null;
                try {
                    receiveMsg = dataInputStream.readUTF(); //TODO: 이부분에서 반복문이 계속 돈다 이것도 한번 봐야한다
                    makeLog("[서비스] 받은 메세지 : "+receiveMsg); //58@#@#user3@#@#456@#@#TEXT@#@#@#@#PM 5:54

                    if (receiveMsg != null) {

//                        if(receiveMsg.equals("exit") || receiveMsg.equals("wrong password")){
//                            makeLog("연결 끊김 - exit 나가기 !!");
//                            return;
//                        }

                        //받은 메세지를 액티비티로 넘겨준다
                        // 1111 = 도그워커 채팅 리스트 / 2222 = 채팅방 / 3333 = 반려인 채팅 리스트
                        //[1] 도그워커 채팅리스트 액티비티로 받은 데이터를 보냄
                        if(WalkerChatListActivity.msghandler != null){
                            makeLog("[서비스] 도그워커 리스트 핸들러 != null");
                            Message message1 = WalkerChatListActivity.msghandler.obtainMessage();
                            message1.what = 1111;
                            message1.obj = receiveMsg;
                            WalkerChatListActivity.msghandler.sendMessage(message1);
                        }else{
                            makeLog("[서비스] 도그워커 리스트 핸들러 == null");
                        }

                        //[2] 채팅 액티비티로 받은 데이터를 보냄
                        if(WalkerChattingActivity.msghandler != null){
                            makeLog("[서비스] 채팅방 핸들러 != null");
                            Message message = WalkerChattingActivity.msghandler.obtainMessage();
                            message.what = 2222;
                            message.obj = receiveMsg;
                            WalkerChattingActivity.msghandler.sendMessage(message);
                        }else{
                            makeLog("[서비스] 채팅방 핸들러 == null");
                        }
                        //[3] 반려인 채팅리스트 액티비티로 받은 데이터를 보냄
                        if(OwnerChatListActivity.msghandler != null){
                            makeLog("[서비스] 반려인 리스트 핸들러 != null");
                            Message message1 = OwnerChatListActivity.msghandler.obtainMessage();
                            message1.what = 3333;
                            message1.obj = receiveMsg;
                            OwnerChatListActivity.msghandler.sendMessage(message1);
                        }else{
                            makeLog("[서비스] 반려인 리스트 핸들러 == null");
                        }

                    }else {
                        makeLog("error 111");
                        break;
                    }

                } catch (IOException e) {
                    makeLog("error 222");
                    e.printStackTrace();
                    try {
//                        socket.close();
                        dataInputStream.close(); //dataInputStream 안닫아주면 계속 반복문 돈다!!
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
    //메세지 보내기 쓰레드
    public static class SendThread extends Thread {

        Socket socket;
        String roomNum;
        String sendUser;
        String sendMsg;
        String typeMsg; //메세지 타입
        DataOutputStream dataOutputStream;

        public SendThread(Socket socket, String roomNum, String sendUser, String sendMsg, String typeMsg) {
            this.socket = socket;
            this.roomNum = roomNum;
            this.sendUser = sendUser;
            this.sendMsg = sendMsg;
            this.typeMsg = typeMsg;

            try {
                //서버로 보낼 메세지
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
            }
        }

        public void run() {

//            while (true) { //반복문 도는 이유는 여기에 있돠!!!!
            //채팅 메세지를 서버로 보낸다
            try {
                //채팅 보내는 시간
                Date time = new Date();
                SimpleDateFormat time_format = new SimpleDateFormat("a h:mm");
                String send_time = time_format.format(time); //메세지 보내는 시간

//                        String msg = bufferedReader.readLine();

                // @채팅 메세지 서버로 출력
//                    dataOutputStream.writeUTF(roomNum+"@#@#"+sendMsg+ "@#@#" + send_time); //5@#@#hi@#@#오전1시
                dataOutputStream.writeUTF(roomNum+"@#@#"+sendUser+"@#@#"+sendMsg+"@#@#"+typeMsg+"@#@#"+send_time); //5@#@#user1@#@#hi@#@#오전1시
                dataOutputStream.flush();

//                makeLog("보낼 메세지 최종 : "+sendMsg);

            } catch (IOException e) {
                e.printStackTrace();
            }

//            } //반복문 도는 이유는 여기에 있돠!!!!
        }
    }


    //소켓 종료
    //채팅방 나가기 - 클라이언트 소켓 종료 (연결 끊기)
    public void stopClientSocket() {
        try {

            //퇴장 메세지 보내는 쓰레드 시작
            sendThread = new SendThread(socket, roomArr, chatUser, "exit", "TEXT"); //소켓, 채팅(보내는)유저, 채팅메세지
            sendThread.start();

            makeLog("[채팅방 나기기]");

            //Socket이 null이 아니고, 현재 닫혀있지 않으면
            if (socket != null && !socket.isClosed()) {

                sendThread.dataOutputStream.close();
                receiveThread.dataInputStream.close(); //dataInputStream 안닫아주면 계속 반복문 돈다!!
                socket.close();
                makeLog("[연결 끊음]");
            }
        } catch (IOException e) { }
    }

    @Override
    public IBinder onBind(Intent intent) {

        makeLog("[서비스] onBind()");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
