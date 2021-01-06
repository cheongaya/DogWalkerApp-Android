package com.example.dogwalker.walker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.ChatService;
import com.example.dogwalker.MyFirebaseSendNotification;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.ChatDTO;
import com.example.dogwalker.retrofit2.response.ChatListDTO;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerChatListActivity extends WalkerBottomNavigation {

    public static ChatListAdapter chatListAdapter;
    RecyclerView recyclerViewWalkerChatList;
    List<ChatListDTO> chatListDTOList;

//    Intent serviceIntent;
    public static Handler msghandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_chat);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

//        //TODO : 채팅 알림 테스트중
//        MyFirebaseSendNotification.sendNotification(MyFirebaseSendNotification.user3Key, "나다", "hi");

        //리사이클러뷰 아이템 클릭시
        chatListAdapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(WalkerChatListActivity.this, WalkerChattingActivity.class);
                intent.putExtra("roomNum", chatListAdapter.chatListDTOArrayList.get(position).getRoomNum());    //채팅방번호
                intent.putExtra("chatUser", chatListAdapter.chatListDTOArrayList.get(position).getChatUser());  //채팅유저 (본인)
                intent.putExtra("chatPartner", chatListAdapter.chatListDTOArrayList.get(position).getChatPartner());  //채팅상대방
                startActivity(intent);
            }
        });

        //메세지 받는 핸들러 생성
        setMsghandler();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //채팅방 리스트 데이터 조회
        selectChatListToDB(applicationClass.currentWalkerID);
    }

    //메세지 핸들러
    public void setMsghandler(){
        // @ 채팅 메세지 화면에 보여주는 코드
        // ReceiveThread 를 통해서 받은 메세지를 Handler로 MainThread에서 처리(UI 변경을 하기위해)
        msghandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 2222) {
//                    tvChatShowMsg.append(message.obj.toString() + "\n");
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[도그워커 리스트] 서버로부터 받은 메세지 : "+message.obj);
                    //받은 메세지 : 방번호, 유저아이디, 메세지타입, 읽은유저, 메세지, 메세지시간
                    String[] receivedMsg = message.obj.toString().split("@#@#"); //58@#@#user1@#@#hi@#@#오전1시
                    String roomNum = receivedMsg[0];    //방번호
                    String senderID = receivedMsg[1];   //메세지 보낸 유저
                    String MsgType = receivedMsg[2];    //메세지 타입 ex) TEXT, IMAGE

                    //메세지 타입 ENTER 일때
                    if(MsgType.equals("ENTER") && senderID.equals(ApplicationClass.currentWalkerID)){
                        //내가 채팅방 입장했을 때
                        //해당 채팅방에서 읽음 숫자를 업데이트 시켜줌 //반환값 = 읽지않은 숫자 int
                        int roomN = Integer.valueOf(roomNum);
                        selectChatReadNumToDB(roomN, ApplicationClass.currentWalkerID); //채팅방 리스트에서 안읽은 메세지 숫자가 0이 되야함
                    }else if(MsgType.equals("ENTER") && !senderID.equals(ApplicationClass.currentWalkerID)){
                        //상대방이 채팅방 입장했을 때
                        //TODO : 이때도 메세지 리스트 안읽은 숫자 갱신이 필요한데.. 어떻게 구현해야할지 더 생각해보자
                        makeToast(senderID+" 채팅방 입장");
                    }

                    //메세지 타입 TEXT 또는 IMAGE 일때
                    if(MsgType.equals("TEXT") || MsgType.equals("IMAGE") ){

                        String ReadID = receivedMsg[3];     //메세지 읽은 유저 ex) walker2/user3/
                        String Msg = receivedMsg[4];        //메세지 내용
                        String sendTime = receivedMsg[5];   //메세지 보낸 시간

                        //해당 채팅방에서 마지막 메세지 + 시간을 실시간 업데이트 시켜줌
                        for(int i=0; chatListAdapter.chatListDTOArrayList.size() > i ; i++){
                            String roomN = chatListAdapter.getChatListDTOArrayList().get(i).getRoomNum();
                            if(roomN.equals(roomNum)){
                                chatListAdapter.getChatListDTOArrayList().get(i).setChatLastMsg(Msg);   //마지막 보낸 메세지 갱신
                                chatListAdapter.getChatListDTOArrayList().get(i).setChatDate(sendTime); //마지막 메세지 보낸 시간 갱신
                                //안읽은 메세지 처리
                                String[] readNotID = ReadID.split("/"); // ex) walker2/user3
                                //경우 1. 내가 채팅메세지 보내고 있고, 상대방은 안보고있을 때 -> walker2/
                                //경우 2. 상대방이 채팅메세지 보내고 있고, 내가 안보고있을 때 -> user3/
                                //경우 3. 둘다 채팅메세지 보고있을 때 -> walker2/user3/
                                if(readNotID.length == 2){
                                    //둘다 보고있었을 때
                                    chatListAdapter.getChatListDTOArrayList().get(i).setChatReadNum(0);
                                }else{
                                    //나 혹은 상대방이 안보고 있었을 때
                                    if(readNotID[0].equals(ApplicationClass.currentWalkerID)){
                                        makeToast("나는 현재 채팅방 안에 있습니다");
                                    }else {
                                        //상대방이 채팅메세지 안보고 있을 경우
                                        makeToast("나는 현재 채팅방 밖에 있습니다");
//                                        makeToast("채팅 상대방이 메세지를 보내고 있습니다");
                                        //내가 채팅메세지 안보고 있을 경우 (안읽은 메세지 숫자 +1 씩 해줌)
                                        int nowReadNotNum = chatListAdapter.getChatListDTOArrayList().get(i).getChatReadNum();
                                        int addReadNotNum = ++nowReadNotNum;
                                        chatListAdapter.getChatListDTOArrayList().get(i).setChatReadNum(addReadNotNum);
                                        chatListAdapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        }
                    }
                }
            }
        };
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){
        recyclerViewWalkerChatList = (RecyclerView)findViewById(R.id.recyclerView_walker_chat_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewWalkerChatList.setLayoutManager(linearLayoutManager); //?? 주석??
        chatListAdapter = new ChatListAdapter(this);
        recyclerViewWalkerChatList.setAdapter(chatListAdapter);
    }

    //채팅방 리스트 데이터 조회
    public void selectChatListToDB(String chatUser){

        Call<List<ChatListDTO>> call = retrofitApi.selectChatRoomList(chatUser);
        call.enqueue(new Callback<List<ChatListDTO>>() {
            @Override
            public void onResponse(Call<List<ChatListDTO>> call, Response<List<ChatListDTO>> response) {

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "채팅방 리스트 데이터 조회 = 성공");
                chatListDTOList = response.body();    //List

                //도그워커 데이터 setText 해주기 (position 번째)
                if(chatListDTOList.size() > 0){

                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "채팅방 리스트 데이터 조회 != null");

                    ArrayList<ChatListDTO> chatListDTOArrayList = new ArrayList<>();
                    chatListDTOArrayList.addAll(chatListDTOList);
                    chatListAdapter.setChatListDTOArrayList(chatListDTOArrayList);
                    chatListAdapter.notifyDataSetChanged();

//                    //TODO : 채팅 서비스 테스트중 s
//                    String roomArr = "";
//                    for(int i=0; chatListDTOList.size() > i ; i++){
//                        makeLog(new Object() {
//                        }.getClass().getEnclosingMethod().getName() + "()", "[리스트] roomArr : " + chatListDTOList.get(i).getRoomNum());
//                        roomArr += chatListDTOList.get(i).getRoomNum()+"/";
//                    }
//
//                    //서비스를 시작시킨다 -> 소켓을 연결하고 조회한 방 번호들을 모두 보낸다
//                    serviceIntent = new Intent(WalkerChatListActivity.this, ChatService.class);
//                    serviceIntent.putExtra("command", "socket");
//                    serviceIntent.putExtra("chatUser", chatUser);
//                    serviceIntent.putExtra("roomArr", roomArr);
//                    startService(serviceIntent); //서비스를 실행하는 함수
//                    //TODO : 채팅 서비스 테스트중 e

                }else{
                    //검색된 도그워커 데이터가 없을때
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "채팅방 리스트 데이터 조회 == null");

                }

            }

            @Override
            public void onFailure(Call<List<ChatListDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "채팅방 리스트 데이터 조회 = 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "t.toString() : " + t.toString());

            }
        });

    }

    //채팅방 읽지않은 메세지 숫자 조회
    public void selectChatReadNumToDB(int roomNum, String chatUser){

        makeLog(new Object() {
        }.getClass().getEnclosingMethod().getName() + "()", "[안읽음 DB] 채팅방 읽지 않은 메세지 숫자 조회 : "+ roomNum +"번방 "+ chatUser);

        Call<ResultDTO> call = retrofitApi.selectChatRoomListReadNum(roomNum, chatUser);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "[안읽음 DB] 채팅방 읽지 않은 메세지 숫자 조회 = 성공");
                ResultDTO resultDTO = response.body();    //List
                String readNotNum = resultDTO.getResponceResult();
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "[안읽음 DB] 채팅방 읽지 않은 메세지 숫자 : "+readNotNum);

                int roomN3 = Integer.valueOf(readNotNum);
                String roomN4 = String.valueOf(roomNum);

                //해당 채팅방에서 안읽은 메세지 숫자를 실시간 업데이트 시켜줌
                for(int i=0; chatListAdapter.chatListDTOArrayList.size() > i ; i++){
                    String roomN2 = chatListAdapter.getChatListDTOArrayList().get(i).getRoomNum();
                    if(roomN2.equals(roomN4)){
                        chatListAdapter.getChatListDTOArrayList().get(i).setChatReadNum(roomN3);
                        chatListAdapter.notifyDataSetChanged();
                        makeLog(new Object() {
                        }.getClass().getEnclosingMethod().getName() + "()", "[안읽음 DB] 안읽음 숫자 업데이트 = 성공");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "[안읽음 DB] 채팅방 읽지 않은 메세지 숫자 조회 = 실패\n" + t.toString());

            }
        });

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //서비스 종료
//        stopService(serviceIntent); //서비스를 중단시키는 함수
//    }


    @Override
    int getContentViewId() {
        return R.layout.activity_walker_chat_list;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavWorker02;
    }
}