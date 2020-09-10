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
import com.example.dogwalker.data.DogDTO;

import java.util.ArrayList;

public class SelectDogListAdapter extends RecyclerView.Adapter<SelectDogListAdapter.ItemViewHolder> {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;

    //adapter에 들어갈 list
    private ArrayList<DogDTO> dogDTOArrayList = new ArrayList<>();
    //클릭 리스너
    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public SelectDogListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_mydog_select, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(dogDTOArrayList.get(position));

    }

    @Override
    public int getItemCount() {
        return dogDTOArrayList.size();
    }

    //subView를 setting 해준다
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDogName;
        private ImageView imvDogProfileImg;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDogName = itemView.findViewById(R.id.textView_item_mydog_name);
            imvDogProfileImg = itemView.findViewById(R.id.imageView_item_mydog_img);

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

        public void onBind(DogDTO dogDTO) {
            tvDogName.setText(dogDTO.getName());
//            imageView.setImageResource(data.getResId());
            //반려견 프로필 이미지 셋팅
            Glide.with(context)
                    .load(dogDTO.getProfile_img())
                    .override(300,300)
                    .apply(applicationClass.requestOptions.fitCenter().circleCrop())
                    .into(imvDogProfileImg);
        }
    }

    public ArrayList<DogDTO> getDogDTOArrayList(){
        return dogDTOArrayList;
    }

    public void setDogDTOArrayList(ArrayList<DogDTO> dogDTOArrayList){
        this.dogDTOArrayList = dogDTOArrayList;
    }
}
