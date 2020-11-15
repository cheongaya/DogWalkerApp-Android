package com.example.dogwalker.walker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerChattingBinding;
import com.example.dogwalker.retrofit2.response.ChattingDTO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class WalkerChattingActivity extends BaseActivity {

    ActivityWalkerChattingBinding binding;
    WalkerChattingAdapter walkerChattingAdapter;

    //소켓 관련 변수
    String localhost = "52.78.138.74";
    int chatting_port_num = 3333;  //채팅(소켓) 포트넘버

    Socket socket;  //소켓

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_chatting);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_chatting);
        binding.setActivity(this);

        //채팅 포트넘버 데이터
//        Intent intent = getIntent();
//        intent.getIntExtra("chatting_port_num", 0);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        //소켓 시작
        startClientSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //소켓 종료
        stopClientSocket();
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerViewWalkerChat.setLayoutManager(linearLayoutManager); //?? 주석??
        walkerChattingAdapter = new WalkerChattingAdapter(this);
        binding.recyclerViewWalkerChat.setAdapter(walkerChattingAdapter);
    }

    //버튼 - 채팅 보내기
    public void btnSendChatting(View view){

        String chatStr = binding.editTextChattingText.getText().toString();

        if(chatStr.length() != 0 || chatStr != null){
            //입력한 채팅 데이터를 서버 소켓으로 보내기
            sendDataSocket(chatStr);
        }
    }

    //클라이언트 소켓 시작 (서버 소켓과 연결)
    public void startClientSocket(){
        //작업 스레드를 생성한다. 왜냐면 connection과 receive()에서 블로킹이 일어나기 때문에
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(localhost, chatting_port_num));
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[연결 포트: " + chatting_port_num + "]");
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[연결 완료: " + socket.getRemoteSocketAddress() + "]");
                } catch (Exception e) {
                //예외가 발생할 시, catch 호출
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[연결 실패: " + socket.getRemoteSocketAddress() + "]");
                    if (!socket.isClosed()) {
                        stopClientSocket();
                    }
                    return;
                }

                //서버 소켓으로부터 데이터 받기 계속 대기중..
                receiveDataSocket();
            }
        };

        thread.start();
    }

    //클라이언트 소켓 종료 (연결 끊기)
    public void stopClientSocket() {
        try {
            //아까처럼 UI를 변경하기 위해
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[연결 끊음]");
            //Socket이 null이 아니고, 현재 닫혀있지 않으면
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) { }
    }

    //서버 소켓에서 데이터 받기 (스레드 안에서 계속 수신 대기중)
    public void receiveDataSocket() {
        //반복적으로 읽기 위해 무한 루프
        while (true) {
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[받기 대기중]");

            try {
                //받은 데이터의 길이가 100인 바이트 배열
                byte[] byteArr = new byte[100];
                InputStream inputStream = socket.getInputStream();

                int readByteCount = inputStream.read(byteArr);

                if (readByteCount == -1) {
                    throw new IOException();
                }

                String data = new String(byteArr, 0, readByteCount, "UTF-8");

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[받기 완료] : "+ data);

//                //리사이클러뷰 화면에 받은 텍스트 표시해주기
//                walkerChattingAdapter.addItem(new ChattingDTO("aa", data, "bb"));
//                walkerChattingAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[받기 - 서버 통신 안됨]");
                stopClientSocket();
                break;
            }
        }
    }

    //서버 소켓에 데이터 보내기
    public void sendDataSocket(String data) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    byte[] byteArr = data.getBytes("UTF-8");
                    //Socket으로부터 출력스트림을 얻는다.
                    OutputStream outputStream = socket.getOutputStream();
                    //바이트 배열을 매개 값으로 해서 write() 메소드를 호출한다
                    outputStream.write(byteArr);
                    outputStream.flush();
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[보내기 완료] : "+ data);

//                    //리사이클러뷰 화면에 보낸 텍스트 표시해주기
//                    walkerChattingAdapter.addItem(new ChattingDTO("aa", data, "bb"));
//                    walkerChattingAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[보내기 - 서버 통신 안됨]");
                    stopClientSocket();
                }

            }
        };
        thread.start();
    }

}