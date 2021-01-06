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
import com.example.dogwalker.retrofit2.response.ChatDTO;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
//    public static ArrayList<ChatDTO> chatDTOArrayList = new ArrayList<>();

    public static List<ChatDTO> chatMessageList;

    public ChatAdapter(List<ChatDTO> msgList, Context context) {
        chatMessageList = msgList;
        this.context = context;
    }

    //클릭 리스너
    private ChatAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(ChatAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_chatting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatDTO chatDTO = chatMessageList.get(position);

        //내가 보낸 메세지면, 메세지 오른쪽 정렬
        if (chatDTO.isSendByMyself()) {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
        } else {
            //상대방이 보낸 메세지면, 메세지 왼쪽 정렬
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
        }

//        if (chatMessage.read != null) {
        String[] readMsg = chatDTO.getRead().split("/"); //walker2/user3
//            String user1 = readMsg[0]; //읽은 유저1
//            String user2 = readMsg[1]; //읽은 유저2
//        }

        switch (chatDTO.getType()) {
            case ChatDTO.TYPE_TEXT:
                if (chatDTO.isSendByMyself()) {
                    //내가 보낸 메세지면, 메세지 오른쪽 정렬
                    holder.rightTextMessage.setText(chatDTO.getContent());
                    holder.rightName.setText(chatDTO.getSender());
                    holder.rightImageMessage.setVisibility(View.GONE);
                    //읽음표시
                    if(readMsg.length == 1){
                        holder.rightReadOne.setVisibility(View.VISIBLE);
                    }else{
                        holder.rightReadOne.setVisibility(View.GONE);
                    }
                } else {
                    holder.leftTextMessage.setText(chatDTO.getContent());
                    holder.leftName.setText(chatDTO.getSender());
                    holder.leftImageMessage.setVisibility(View.GONE);
                    //읽음표시
                    if(readMsg.length == 1){
                        holder.leftReadOne.setVisibility(View.VISIBLE);
                    }else{
                        holder.leftReadOne.setVisibility(View.GONE);
                    }
                }
                break;
            case ChatDTO.TYPE_IMAGE:
                if (chatDTO.isSendByMyself()) {
                    holder.rightName.setText(chatDTO.getSender());
                    holder.rightTextMessage.setVisibility(View.GONE);
                    holder.rightLayout.setBackground(null);
                    Log.d("[채팅 어댑터] 이미지  : ", chatDTO.getContent());
                    //채팅 이미지 셋팅
                    Glide.with(context)
                            .load(chatDTO.getContent())
                            .override(300,300)
                            .apply(applicationClass.requestOptions.fitCenter().circleCrop())
                            .into(holder.rightImageMessage);
                    //읽음표시
                    if(readMsg.length == 1){
                        holder.rightReadOne.setVisibility(View.VISIBLE);
                    }else{
                        holder.rightReadOne.setVisibility(View.GONE);
                    }
                } else {
                    holder.leftName.setText(chatDTO.getSender());
                    holder.leftTextMessage.setVisibility(View.GONE);
                    holder.leftLayout.setBackground(null);
                    Log.d("[채팅 어댑터] 이미지  : ", chatDTO.getContent());
                    Glide.with(context)
                            .load(chatDTO.getContent())
                            .override(300,300)
                            .apply(applicationClass.requestOptions.fitCenter().circleCrop())
                            .into(holder.leftImageMessage);
                    //읽음표시
                    if(readMsg.length == 1){
                        holder.leftReadOne.setVisibility(View.VISIBLE);
                    }else{
                        holder.leftReadOne.setVisibility(View.GONE);
                    }
                }
                break;
            case ChatDTO.TYPE_ENTER:
                // TODO: 채팅방 입장
                break;
            case ChatDTO.TYPE_EXIT:
                // TODO: 채팅방 퇴장
                break;
            default:
                break;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftLayout;
        LinearLayout rightLayout;
        LinearLayout leftContentLayout;
        LinearLayout rightContentLayout;
        TextView leftTextMessage;
        TextView rightTextMessage;
        TextView leftName;
        TextView rightName;
        TextView leftReadOne; //읽음 표시
        TextView rightReadOne; //읽음 표시
        ImageView leftImageMessage;
        ImageView rightImageMessage;

        ViewHolder(@NonNull View view) {
            super(view);
            leftLayout = view.findViewById(R.id.left_layout);
            rightLayout = view.findViewById(R.id.right_layout);
            leftTextMessage = view.findViewById(R.id.left_text_message);
            rightTextMessage = view.findViewById(R.id.right_text_message);
            leftContentLayout = view.findViewById(R.id.left_content_layout);
            rightContentLayout = view.findViewById(R.id.right_content_layout);
            leftName = view.findViewById(R.id.left_name);
            rightName = view.findViewById(R.id.right_name);
            leftReadOne = view.findViewById(R.id.left_read_one); //읽음 표시
            rightReadOne = view.findViewById(R.id.right_read_one); //읽음 표시
            leftImageMessage = view.findViewById(R.id.left_image_message);
            rightImageMessage = view.findViewById(R.id.right_image_message);

            //onClick 선언
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(listenter !=null){
                            listenter.onItemClick(v,position);
                        }
                    }
                }
            });
        }
    }

//    public ArrayList<ChatDTO> getChattingDTOArrayList(){
//        return chatDTOArrayList;
//    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public void setChattingDTOArrayList(ArrayList<ChatDTO> chatDTOArrayList){
        this.chatMessageList = chatDTOArrayList;
    }

    public void addItem(ChatDTO item){
        chatMessageList.add(item);
        notifyDataSetChanged();
    }
}
