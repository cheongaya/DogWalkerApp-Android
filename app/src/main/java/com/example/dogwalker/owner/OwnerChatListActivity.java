package com.example.dogwalker.owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.ChatListDTO;
import com.example.dogwalker.walker.WalkerChatListActivity;
import com.example.dogwalker.walker.WalkerChatListAdapter;
import com.example.dogwalker.walker.WalkerChattingActivity;

public class OwnerChatListActivity extends OwnerBottomNavigation {

    WalkerChatListAdapter walkerChatListAdapter;

    RecyclerView recyclerViewOwnerChatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_chat);

        recyclerViewOwnerChatList = (RecyclerView)findViewById(R.id.recyclerView_owner_chat_list);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        walkerChatListAdapter.addItem(new ChatListDTO(null, "채팅 이름 5001", "채팅 텍스트", "채팅 날짜"));

        //리사이클러뷰 아이템 클릭시
        walkerChatListAdapter.setOnItemClickListener(new WalkerChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(OwnerChatListActivity.this, WalkerChattingActivity.class);
                intent.putExtra("chatting_port_num", 5001);
                startActivity(intent);
            }
        });
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewOwnerChatList.setLayoutManager(linearLayoutManager); //?? 주석??
        walkerChatListAdapter = new WalkerChatListAdapter(this);
        recyclerViewOwnerChatList.setAdapter(walkerChatListAdapter);
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_owner_chat_list;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavOwner02;
    }
}