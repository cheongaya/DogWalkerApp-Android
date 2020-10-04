package com.example.dogwalker.owner.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.WalkerReviewDTO;

import java.util.ArrayList;

import static java.sql.Types.NULL;

public class WalkerReviewAdapter extends RecyclerView.Adapter<WalkerReviewAdapter.ItemViewHolder> {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
    public static ArrayList<WalkerReviewDTO> walkerReviewDTOArrayList = new ArrayList<>();

    //클릭 리스너
    private WalkerReviewAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(WalkerReviewAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public WalkerReviewAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_review, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(walkerReviewDTOArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return walkerReviewDTOArrayList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        //댓글 관련
        private ImageView imvReviewOwnerImg;
        private TextView tvReviewOwnerName;
        private TextView tvReviewCreatedDate;
        private TextView tvReviewMemo;
        //답글 관련
        private LinearLayout linearReplyCnt;    //답글영역
        private TextView tvReplyWalkerName, tvReplyCreatedDate, tvReplyMemo;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            //댓글 관련
            imvReviewOwnerImg = itemView.findViewById(R.id.imageView_item_walker_review_profile_img);
            tvReviewOwnerName = itemView.findViewById(R.id.textView_item_walker_review_owner_name);
            tvReviewCreatedDate = itemView.findViewById(R.id.textView_item_walker_review_write_date);
            tvReviewMemo = itemView.findViewById(R.id.textView_item_walker_review);
            //답글 관련
            linearReplyCnt = itemView.findViewById(R.id.linearLayout_reply);
            tvReplyWalkerName = itemView.findViewById(R.id.textView_item_reply_walker_name);
            tvReplyCreatedDate = itemView.findViewById(R.id.textView_item_reply_write_date);
            tvReplyMemo = itemView.findViewById(R.id.textView_item_reply_text);

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

        public void onBind(WalkerReviewDTO walkerReviewDTO) {

            //리뷰 관련
//            imvReviewOwnerImg.setImageURI(bookingServiceDTO.getOwner_id());
            tvReviewOwnerName.setText(walkerReviewDTO.getReview_owner_id());
            tvReviewCreatedDate.setText(walkerReviewDTO.getReview_created_date());
            tvReviewMemo.setText(walkerReviewDTO.getReview_memo());
            //답변 관련
            if(walkerReviewDTO.getReply_idx() == NULL){
                linearReplyCnt.setVisibility(View.GONE);
            }else{
                linearReplyCnt.setVisibility(View.VISIBLE);
                tvReplyWalkerName.setText(walkerReviewDTO.getReply_walker_id());
                tvReplyCreatedDate.setText(walkerReviewDTO.getReply_created_date());
                tvReplyMemo.setText(walkerReviewDTO.getReply_memo());
            }

        }
    }

    public ArrayList<WalkerReviewDTO> getWalkerReviewDTOArrayList(){
        return walkerReviewDTOArrayList;
    }

    public void setWalkerReviewDTOArrayList(ArrayList<WalkerReviewDTO> walkerReviewDTOArrayList){
        this.walkerReviewDTOArrayList = walkerReviewDTOArrayList;
    }
}
