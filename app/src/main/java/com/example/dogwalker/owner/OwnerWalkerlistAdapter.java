package com.example.dogwalker.owner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;
import com.example.dogwalker.data.WalkerlistDTO;

import java.util.ArrayList;

public class OwnerWalkerlistAdapter extends RecyclerView.Adapter<OwnerWalkerlistAdapter.ItemViewHolder> {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
    public static ArrayList<WalkerlistDTO> walkerlistDTOArrayList = new ArrayList<>();
    //클릭 리스너
    private OwnerWalkerlistAdapter.OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OwnerWalkerlistAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public OwnerWalkerlistAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OwnerWalkerlistAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_walker, parent, false);
        return new OwnerWalkerlistAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnerWalkerlistAdapter.ItemViewHolder holder, int position) {
        holder.onBind(walkerlistDTOArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return walkerlistDTOArrayList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvWalkerName;
        private TextView tvWalkerLocation;
        private TextView tvWalkerIntroduce;
        private TextView tvWalkerReviewCount;
        private TextView tvWalkerThirtyMinPrice;
        private ImageView imvWalkerBookMarkImgON;
        private ImageView imvWalkerBookMarkImgOFF;
        private ImageView imvWalkerProfileImg;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWalkerName = itemView.findViewById(R.id.textView_item_walker_name);
            tvWalkerLocation = itemView.findViewById(R.id.textView_item_walker_location);
            tvWalkerIntroduce = itemView.findViewById(R.id.textView_item_walker_introduce);
            tvWalkerReviewCount = itemView.findViewById(R.id.textView_item_walker_review);
            tvWalkerThirtyMinPrice = itemView.findViewById(R.id.textView_item_walker_price_thirty_minutes);
            imvWalkerBookMarkImgON = itemView.findViewById(R.id.imageVeiw_item_walker_bookmark_on);
            imvWalkerBookMarkImgOFF = itemView.findViewById(R.id.imageVeiw_item_walker_bookmark_off);
            imvWalkerProfileImg = itemView.findViewById(R.id.imageView_item_walker_img);

            //onClick 선언
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(mListener !=null){
                            mListener.onItemClick(v,position);
                        }
                    }
                }
            });
        }

        public void onBind(WalkerlistDTO walkerlistDTO) {

            tvWalkerName.setText(walkerlistDTO.getName());
            tvWalkerLocation.setText(walkerlistDTO.getLocation());
            tvWalkerIntroduce.setText(walkerlistDTO.getIntroduce());
            tvWalkerReviewCount.setText("리뷰 "+Integer.valueOf(walkerlistDTO.getReview_score())+"개");  //string -> int 형 변환
            tvWalkerThirtyMinPrice.setText(Integer.valueOf(walkerlistDTO.getPrice_thirty_minutes())+"원 (30분 기준)"); //string -> int 형 변환

//            imageView.setImageResource(data.getResId());
            //도그워커 프로필 이미지 셋팅
            Glide.with(context)
                    .load(walkerlistDTO.getProfile_img())
                    .override(300,300)
                    .apply(applicationClass.requestOptions.fitCenter().circleCrop())
                    .into(imvWalkerProfileImg);

            //즐겨찾기 이미지 셋팅
            if(walkerlistDTO.getBmk_user_id() == null && walkerlistDTO.getBmk_walker_id() == null){
                imvWalkerBookMarkImgOFF.setVisibility(View.VISIBLE);
                imvWalkerBookMarkImgON.setVisibility(View.GONE);
            }else if(walkerlistDTO.getId().contentEquals(walkerlistDTO.getBmk_walker_id())){
                imvWalkerBookMarkImgOFF.setVisibility(View.GONE);
                imvWalkerBookMarkImgON.setVisibility(View.VISIBLE);
            }
        }
    }

    public ArrayList<WalkerlistDTO> getWalkerlistDTOArrayList(){
        return walkerlistDTOArrayList;
    }

    public void setWalkerlistDTOArrayList(ArrayList<WalkerlistDTO> walkerlistDTOArrayList){
        this.walkerlistDTOArrayList = walkerlistDTOArrayList;
    }

}
