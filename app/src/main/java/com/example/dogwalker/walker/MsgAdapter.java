package com.example.dogwalker.walker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.R;

import java.util.ArrayList;

public class MsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MsgDTO> items;

    public MsgAdapter(ArrayList<MsgDTO> items) {
        this.items = items;
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


        if (item.getViewType()==1) {
            myChatMessageViewHolder my_holder = (myChatMessageViewHolder) viewHolder;
//            my_holder.my_message.setText(item.getMessage());
//            my_holder.my_time.setText(item.getTime());
            my_holder.my_message.setText(item.getMsg());

        } else if (item.getViewType()==2){
            othersChatMessageViewHolder others_holder = (othersChatMessageViewHolder) viewHolder;
//            others_holder.others_message.setText(item.getMessage());
//            others_holder.others_time.setText(item.getTime());
            others_holder.others_message.setText(item.getMsg());

        } else{
            serverChatMessageViewHolder server_holder = (serverChatMessageViewHolder) viewHolder;
//            server_holder.tv_server.setText(item.getMessage());
            server_holder.tv_server.setText(item.getMsg());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class myChatMessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout my_layout;
        TextView my_time, my_message;

        public myChatMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            my_layout = itemView.findViewById(R.id.my_layout);
            my_time = itemView.findViewById(R.id.my_time);
            my_message = itemView.findViewById(R.id.my_message);
        }
    }

    public class othersChatMessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout others_layout;
        TextView others_time, others_message;

        public othersChatMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            others_layout = itemView.findViewById(R.id.others_layout);
            others_time = itemView.findViewById(R.id.others_time);
            others_message = itemView.findViewById(R.id.others_message);
        }
    }


    public class serverChatMessageViewHolder extends RecyclerView.ViewHolder {

        TextView tv_server;

        public serverChatMessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            tv_server = itemView.findViewById(R.id.tv_server);

        }
    }
}
