package com.example.dogwalker.owner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.R;

import java.util.ArrayList;

public class DogListAdapter extends RecyclerView.Adapter<DogListAdapter.ItemViewHolder> {

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

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_mydog, parent, false);
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
//        private ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDogName = itemView.findViewById(R.id.textView_item_mydog_name);
//            imageView = itemView.findViewById(R.id.imageView);

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
        }
    }

    public ArrayList<DogDTO> getDogDTOArrayList(){
        return dogDTOArrayList;
    }

    public void setDogDTOArrayList(ArrayList<DogDTO> dogDTOArrayList){
        this.dogDTOArrayList = dogDTOArrayList;
    }
}
