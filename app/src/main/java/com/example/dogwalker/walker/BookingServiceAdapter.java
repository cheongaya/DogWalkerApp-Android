package com.example.dogwalker.walker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.GoogleMapActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;

import java.util.ArrayList;

public class BookingServiceAdapter extends RecyclerView.Adapter<BookingServiceAdapter.ItemViewHolder> {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
    public static ArrayList<BookingServiceDTO> bookingServiceDTOArrayList = new ArrayList<>();
    //클릭 리스너
    private BookingServiceAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(BookingServiceAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public BookingServiceAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_bookinglist_walker, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(bookingServiceDTOArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return bookingServiceDTOArrayList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBookingOwnerName;
        private TextView tvBookingOwnerDogName;
        private TextView tvBookingWalkTotalTime;
        private TextView tvBookingWalkTime;
        private Button btnWalkStart;
        private Button btnWalkEnd;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookingOwnerName = itemView.findViewById(R.id.textView_item_booking_owner_name);
            tvBookingOwnerDogName = itemView.findViewById(R.id.textView_item_booking_owner_dog_name);
            tvBookingWalkTotalTime = itemView.findViewById(R.id.textView_item_booking_walk_total_time);
            tvBookingWalkTime = itemView.findViewById(R.id.textView_item_booking_walk_time);
            btnWalkStart = itemView.findViewById(R.id.button_item_walk_start);
            btnWalkEnd = itemView.findViewById(R.id.button_item_walk_end);

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
            btnWalkStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
//                    Intent intent = new Intent(context, WalkerStopWatchActivity.class);
//                    context.startActivity(intent);
                    Intent intent = new Intent(context, WalkerDogwalkingIngActivity.class);
                    intent.putExtra("booking_id", bookingServiceDTOArrayList.get(position).getIdx());
                    intent.putExtra("owner_dog_name", bookingServiceDTOArrayList.get(position).getOwner_dog_name());
                    intent.putExtra("walk_total_time", bookingServiceDTOArrayList.get(position).getWalk_total_time());
                    context.startActivity(intent);
                }
            });

            btnWalkEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "완료된 산책입니다", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void onBind(BookingServiceDTO bookingServiceDTO) {

            tvBookingOwnerName.setText(bookingServiceDTO.getOwner_id());
            tvBookingOwnerDogName.setText(bookingServiceDTO.getOwner_dog_name());
            tvBookingWalkTotalTime.setText(bookingServiceDTO.getWalk_total_time()+"분");
            tvBookingWalkTime.setText(bookingServiceDTO.getWalk_date()+" "+bookingServiceDTO.getWalk_time());
            //DB에서 불러온 산책 상태 데이터가 "before" 이면 [산책시간] 버튼 노출
            if(bookingServiceDTO.getWalking_status().contentEquals("before")){
                btnWalkStart.setVisibility(View.VISIBLE);
                btnWalkEnd.setVisibility(View.GONE);
            //DB에서 불러온 산책 상태 데이터가 "after" 이면 [산책완료] 버튼 노출
            }else if(bookingServiceDTO.getWalking_status().contentEquals("after")){
                btnWalkStart.setVisibility(View.GONE);
                btnWalkEnd.setVisibility(View.VISIBLE);
            }
        }
    }

    public ArrayList<BookingServiceDTO> getBookingServiceDTOArrayList(){
        return bookingServiceDTOArrayList;
    }

    public void setBookingServiceDTOArrayList(ArrayList<BookingServiceDTO> bookingServiceDTOArrayList){
        this.bookingServiceDTOArrayList = bookingServiceDTOArrayList;
    }
}
