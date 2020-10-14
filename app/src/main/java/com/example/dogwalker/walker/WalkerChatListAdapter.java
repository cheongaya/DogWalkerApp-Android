package com.example.dogwalker.walker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;
import com.example.dogwalker.retrofit2.response.ChatListDTO;

import java.util.ArrayList;

public class WalkerChatListAdapter extends RecyclerView.Adapter<WalkerChatListAdapter.ItemViewHolder> {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
    public static ArrayList<ChatListDTO> chatListDTOArrayList = new ArrayList<>();

    //클릭 리스너
    private WalkerChatListAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(WalkerChatListAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public WalkerChatListAdapter(Context context) {
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

        private ImageView imvChatListImg;
        private TextView tvChatListName;
        private TextView tvChatListText;
        private TextView tvChatListDate;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imvChatListImg = itemView.findViewById(R.id.imageView_item_chatlist_img);
            tvChatListName = itemView.findViewById(R.id.textView_item_chatlist_name);
            tvChatListText = itemView.findViewById(R.id.textView_item_chatlist_text);
            tvChatListDate = itemView.findViewById(R.id.textView_item_chatlist_date);

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

        public void onBind(ChatListDTO chatListDTO) {

            tvChatListName.setText(chatListDTO.getChatName());
            tvChatListText.setText(chatListDTO.getChatText());
            tvChatListDate.setText(chatListDTO.getChatDate());

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
    }
}
