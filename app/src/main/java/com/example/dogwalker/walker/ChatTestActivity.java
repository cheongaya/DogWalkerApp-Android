package com.example.dogwalker.walker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

public class ChatTestActivity extends BaseActivity {

    public static String serverIP = "192.168.1.17"; //서버 연결 IP
    public static String serverPort = "3333"; //서버 연결 Port

    public static String clientKey; //채팅자 고유키 (방번호@유저아이디)

    TextView showText;
    Button btnConnect;
    Button btnSendMsg;
    EditText editTextId, editTextMsg;

    SocketClient client;
    Socket socket;
    Handler msghandler;
    ReceiveThread receiveThread;
    SendThread sendThread;

    LinkedList<SocketClient> threadList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_test);

        threadList = new LinkedList<SocketClient>();

        editTextId = (EditText) findViewById(R.id.id_EditText);  //고유키 관련
        btnConnect = (Button) findViewById(R.id.connect_Button); //버튼 - 연결
        showText = (TextView) findViewById(R.id.showText_TextView); //메세지 보이는 부분
        editTextMsg = (EditText) findViewById(R.id.editText_massage); //메세지 입력하는 부분
        btnSendMsg = (Button) findViewById(R.id.Button_send); //버튼 - 메세지 전송

        // ReceiveThread 를 통해서 받은 메세지를 Handler로 MainThread에서 처리(외부 Thread에서는 UI 변경이 불가함으로..)
        msghandler = new Handler() {
            @Override
            public void handleMessage(Message hdmsg) {
                if (hdmsg.what == 1111) {
                    showText.append(hdmsg.obj.toString() + "\n");
                }
            }
        };

        //연결버튼 클릭 이벤트
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                clientKey = editTextId.getText().toString(); //입력한 고유키 데이터를 받아온다

                //Client 연결부
                client = new SocketClient(serverIP, serverPort, clientKey);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "threadList.size() : " + threadList.size());
                threadList.add(client);
                client.start();
            }
        });

        //메세지 전송 버튼 클릭 이벤트
        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //SendThread 시작
                if (editTextMsg.getText().toString() != null) {

                    String sendMsgStr = editTextMsg.getText().toString();

                    sendThread = new SendThread(socket, sendMsgStr);
                    sendThread.start();

                    //시작후 editTextMessage 입력창 초기화
                    editTextMsg.setText("");
                }
            }
        });
    }

    class SocketClient extends Thread {

        boolean threadAlive;
        String serverIp;
        String serverPort;
        String clientKey;

        //InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader br = null;

        private DataOutputStream output = null;

        public SocketClient(String ip, String port, String key) {
            threadAlive = true;

            this.serverIp = ip; //서버 IP
            this.serverPort = port; //서버 PORT
            this.clientKey = key;  //서버 클라이언트키
        }

        @Override
        public void run() {

            try {
                // 연결후 바로 ReceiveThread 시작
                socket = new Socket(serverIp, Integer.parseInt(serverPort));
                //inputStream = socket.getInputStream();
                output = new DataOutputStream(socket.getOutputStream());
                receiveThread = new ReceiveThread(socket);
                receiveThread.start();

                //접속 버튼 클릭시 -> 서버로 클라이언트 고유키 전송
                output.writeUTF(clientKey);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveThread extends Thread {

        Socket socket = null;
        DataInputStream input;

        public ReceiveThread(Socket socket) {
            this.socket = socket;
            try{
                //서버에서 받은 메세지
                input = new DataInputStream(socket.getInputStream());
            }catch(Exception e){
            }
        }
        // 메세지 수신후 Handler로 전달
        public void run() {
            try {
                while (input != null) {
                    //서버로부터 메세지를 읽어온다
                    String receiveMsgStr = input.readUTF();

                    if (receiveMsgStr != null) {
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "receiveMsgStr is not null..");
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "receiveMsgStr :" + receiveMsgStr);
                        Message message = msghandler.obtainMessage();
                        message.what = 1111;
                        message.obj = receiveMsgStr;
                        msghandler.sendMessage(message);
//                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()",
//                                "receiveMsgStr :" + message.obj.toString());

                    }
                }
            } catch (IOException e) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "receiveMsgStr is null..");
                e.printStackTrace();
            }
        }
    }

    class SendThread extends Thread {

        Socket socket = null;
        String sendMsgStr = null;
        DataOutputStream output;

        public SendThread(Socket socket, String sendMsgStr) {
            this.socket = socket;
            this.sendMsgStr = sendMsgStr;

            try {
                //서버로 보낼 메세지
                output = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
            }
        }

        public void run() {

            try {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "sendMsgStr :" + sendMsgStr);
                //채팅 메세지를 서버로 보낸다
                if (output != null) {
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "output is not null..");

                    if (sendMsgStr != null) {
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "sendMsgStr is not null.."); //요기까지 뜬다
                        //서버로 채팅 메세지 전송
                        output.writeUTF(clientKey + "  :  " +sendMsgStr); //5@walker1 : hi
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();

            }
        }
    }



}