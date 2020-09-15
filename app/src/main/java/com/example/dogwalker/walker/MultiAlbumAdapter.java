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

import com.bumptech.glide.Glide;
import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.GoogleMapActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;

import java.util.ArrayList;

public class MultiAlbumAdapter extends RecyclerView.Adapter<MultiAlbumAdapter.ItemViewHolder>  {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
    public static ArrayList<String> imageUrlArraylist = new ArrayList<>();

    //클릭 리스너
    private BookingServiceAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(BookingServiceAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public MultiAlbumAdapter(Context context) {
        this.context = context;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_file_upload, parent, false);
        return new MultiAlbumAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(imageUrlArraylist.get(position));
    }

    @Override
    public int getItemCount() {
        return imageUrlArraylist.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imvMultiAlbum;
        private Button btnImageDelete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imvMultiAlbum = itemView.findViewById(R.id.imageView_item_file_upload_img);
            btnImageDelete = itemView.findViewById(R.id.imageButton_item_file_upload_delete);

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

            //버튼 클릭시 이벤트
            btnImageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, WalkerStopWatchActivity.class);
//                    context.startActivity(intent);
                }
            });
        }

        public void onBind(String s) {

            //반려견 프로필 이미지 셋팅
            Glide.with(context)
                    .load(s)
                    .override(300,300)
                    .apply(applicationClass.requestOptions.fitCenter().circleCrop())
                    .into(imvMultiAlbum);
        }
    }

    public ArrayList<String> getImageUrlArraylist(){
        return imageUrlArraylist;
    }

    public void setImageUrlArraylist(ArrayList<String> imageUrlArraylist){
        this.imageUrlArraylist = imageUrlArraylist;
    }
}
