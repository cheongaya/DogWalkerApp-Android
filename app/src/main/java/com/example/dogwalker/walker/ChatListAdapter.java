package com.example.dogwalker.walker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.ChatListDTO;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ItemViewHolder> {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
    public static ArrayList<ChatListDTO> chatListDTOArrayList = new ArrayList<>();

    //클릭 리스너
    private ChatListAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(ChatListAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public ChatListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_chatlist, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(chatListDTOArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatListDTOArrayList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvChatRoomNum;
        private TextView tvChatUsers;
        private TextView tvChatDate;
        private TextView tvChatReadNum;
//        private Button btnChatStart;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

//            imvChatListImg = itemView.findViewById(R.id.imageView_item_chatlist_img);
//            tvChatListName = itemView.findViewById(R.id.textView_item_chatlist_name);
//            tvChatListText = itemView.findViewById(R.id.textView_item_chatlist_text);
//            tvChatListDate = itemView.findViewById(R.id.textView_item_chatlist_date);

            tvChatRoomNum = itemView.findViewById(R.id.textView_room_num);
            tvChatUsers = itemView.findViewById(R.id.textView_chat_users);
            tvChatDate = itemView.findViewById(R.id.textview_chat_date);
//            btnChatStart = itemView.findViewById(R.id.button_chat_start);
            tvChatReadNum = itemView.findViewById(R.id.textVew_chat_list_read);

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

//            //채팅시작 버튼 클릭시 이벤트
//            btnChatStart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//
//                    Intent intent = new Intent(context, WalkerChattingActivity.class);
//                    intent.putExtra("roomNum", chatListDTOArrayList.get(position).getRoomNum());    //채팅방번호
//                    intent.putExtra("chatUser", chatListDTOArrayList.get(position).getChatUser());  //채팅유저 (본인)
//                    intent.putExtra("chatPartner", chatListDTOArrayList.get(position).getChatPartner());  //채팅상대방
//                    context.startActivity(intent);
//
//                }
//            });
        }

        public void onBind(ChatListDTO chatListDTO) {

//            tvChatListName.setText(chatListDTO.getChatName());
//            tvChatListText.setText(chatListDTO.getChatText());
//            tvChatListDate.setText(chatListDTO.getChatDate());

            tvChatRoomNum.setText(chatListDTO.getRoomNum()+"번방 : "+chatListDTO.getChatUser()+ "/" + chatListDTO.getChatPartner());
            tvChatUsers.setText(chatListDTO.getChatLastMsg());
            tvChatDate.setText(chatListDTO.getChatDate());
            tvChatReadNum.setText(chatListDTO.getChatReadNum()+"");

        }
    }

    public ArrayList<ChatListDTO> getChatListDTOArrayList(){
        return chatListDTOArrayList;
    }

    public void setChatListDTOArrayList(ArrayList<ChatListDTO> chatListDTOArrayList){
        this.chatListDTOArrayList = chatListDTOArrayList;
    }

    public void addItem(ChatListDTO item){
        chatListDTOArrayList.add(item);
        notifyDataSetChanged();
    }
}
