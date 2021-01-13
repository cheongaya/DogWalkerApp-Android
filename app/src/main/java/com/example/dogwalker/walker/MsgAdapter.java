package com.example.dogwalker.walker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;

import java.util.ArrayList;

public class MsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ApplicationClass applicationClass;
    Context context;

    private ArrayList<MsgDTO> items;

    public static String chatPartnerProfile;

    public MsgAdapter(ArrayList<MsgDTO> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);

        if (items.get(position).getViewType()==1){
            return 1; //나
        }else if (items.get(position).getViewType()==2) {
            return 2; //상대방
        }else{
            return 3; //시스템
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;

        View view;

        applicationClass = (ApplicationClass)context.getApplicationContext();

        switch (viewType){
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_mymsg, parent, false);
                return new myChatMessageViewHolder(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_othermsg, parent, false);
                return new othersChatMessageViewHolder(view);
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_servermsg, parent, false);
                return new serverChatMessageViewHolder(view);
        }
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_mymsg, parent, false);
        return new myChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MsgDTO item = items.get(position);

        Log.d("onBindViewHolder", String.valueOf(position));

        String[] readNumArr = item.getReader().split("/"); //walker2/user3
        String readNum = ""; //읽음표시
        //읽음표시
        if(readNumArr.length == 1){
            readNum = "1";
        }else{
            readNum = "";
        }

        //내가 보낸 메세지일때
        if (item.getViewType()==1) {
            myChatMessageViewHolder my_holder = (myChatMessageViewHolder) viewHolder;
            my_holder.my_time.setText(item.getMsgTime());
            my_holder.my_read.setText(readNum);
            if(item.msgType.equals("TEXT")){
                //텍스트 메세지
                my_holder.my_message.setVisibility(View.VISIBLE);
                my_holder.my_image.setVisibility(View.GONE);
                my_holder.my_message.setText(item.getMsg());
            }else if(item.msgType.equals("IMAGE")){
                //이미지 메세지
                my_holder.my_image.setVisibility(View.VISIBLE);
                my_holder.my_message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(item.msg)
                        .override(150,150)
                        .apply(applicationClass.requestOptions.fitCenter().centerInside().centerCrop())
                        .into(my_holder.my_image);
            }

        //상대방이 보낸 메세지일때
        } else if (item.getViewType()==2){
            othersChatMessageViewHolder others_holder = (othersChatMessageViewHolder) viewHolder;
            others_holder.others_id.setText(item.getSender());
            Glide.with(context)
                    .load(chatPartnerProfile)
                    .override(50,50)
                    .apply(applicationClass.requestOptions.fitCenter().circleCrop())
                    .into(others_holder.others_profile);
            others_holder.others_time.setText(item.getMsgTime());
            others_holder.others_read.setText(readNum);
            if(item.msgType.equals("TEXT")){
                //텍스트 메세지
                others_holder.others_message.setVisibility(View.VISIBLE);
                others_holder.others_image.setVisibility(View.GONE);
                others_holder.others_message.setText(item.getMsg());
            }else if(item.msgType.equals("IMAGE")){
                //이미지 메세지
                others_holder.others_image.setVisibility(View.VISIBLE);
                others_holder.others_message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(item.msg)
                        .override(150,150)
                        .apply(applicationClass.requestOptions.fitCenter().centerInside().centerCrop())
                        .into(others_holder.others_image);
            }
        //서버가 보낸 메세지일때
        } else{
            serverChatMessageViewHolder server_holder = (serverChatMessageViewHolder) viewHolder;
//            server_holder.tv_server.setText(item.getMessage());
//            server_holder.tv_server.setText(item.getSender() + item.getMsg());
            //본인 입장일때
            if(item.getSender().equals(ApplicationClass.currentWalkerID)){
//                server_holder.tv_server.setText("나 "+" 입장했습니다");
                server_holder.tv_server.setVisibility(View.GONE);
            }else{
            //상대방 입장일때
//                server_holder.tv_server.setText(item.getSender()+" 입장했습니다");
                server_holder.tv_server.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //내 채팅
    public class myChatMessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout my_layout;
        TextView my_read, my_time, my_message;
        ImageView my_image;

        public myChatMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            my_layout = itemView.findViewById(R.id.my_layout);
            my_read = itemView.findViewById(R.id.my_read);
            my_time = itemView.findViewById(R.id.my_time);
            my_message = itemView.findViewById(R.id.my_message);
            my_image = itemView.findViewById(R.id.my_image);
        }
    }
    //상대방 채팅
    public class othersChatMessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout others_layout;
        TextView others_read, others_time, others_message, others_id;
        ImageView others_image, others_profile;

        public othersChatMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            others_layout = itemView.findViewById(R.id.others_layout);
            others_read = itemView.findViewById(R.id.others_read);
            others_time = itemView.findViewById(R.id.others_time);
            others_message = itemView.findViewById(R.id.others_message);
            others_id = itemView.findViewById(R.id.others_id);
            others_image = itemView.findViewById(R.id.others_image);
            others_profile = itemView.findViewById(R.id.others_profile);
        }
    }
    //서버 채팅
    public class serverChatMessageViewHolder extends RecyclerView.ViewHolder {

        TextView tv_server;

        public serverChatMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            tv_server = itemView.findViewById(R.id.tv_server);
        }
    }
}
