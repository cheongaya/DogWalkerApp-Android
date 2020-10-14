package com.example.dogwalker.walker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.ChatListDTO;
import com.example.dogwalker.retrofit2.response.ChattingDTO;

import java.util.ArrayList;

public class WalkerChattingAdapter extends RecyclerView.Adapter<WalkerChattingAdapter.ItemViewHolder> {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
    public static ArrayList<ChattingDTO> chattingDTOArrayList = new ArrayList<>();

    //클릭 리스너
    private WalkerChattingAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(WalkerChattingAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public WalkerChattingAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_chatting, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(chattingDTOArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return chattingDTOArrayList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvChattingName;
        private TextView tvChattingText;
        private TextView tvChattingDate;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvChattingName = itemView.findViewById(R.id.textView_item_chatting_name);
            tvChattingText = itemView.findViewById(R.id.textView_item_chatlist_text);
            tvChattingDate = itemView.findViewById(R.id.textView_item_chatting_date);

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

        public void onBind(ChattingDTO chattingDTO) {

            tvChattingName.setText(chattingDTO.getChatName());
            tvChattingText.setText(chattingDTO.getChatText());
            tvChattingDate.setText(chattingDTO.getChatDate());

        }
    }

    public ArrayList<ChattingDTO> getChattingDTOArrayList(){
        return chattingDTOArrayList;
    }

    public void setChattingDTOArrayList(ArrayList<ChattingDTO> chattingDTOArrayList){
        this.chattingDTOArrayList = chattingDTOArrayList;
    }

    public void addItem(ChattingDTO item){
        chattingDTOArrayList.add(item);
    }
}
