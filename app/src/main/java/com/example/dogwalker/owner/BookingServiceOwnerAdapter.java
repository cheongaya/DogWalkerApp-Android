package com.example.dogwalker.owner;

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
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;
import com.example.dogwalker.walker.WalkerStopWatchActivity;

import java.util.ArrayList;

public class BookingServiceOwnerAdapter extends RecyclerView.Adapter<BookingServiceOwnerAdapter.ItemViewHolder> {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
    public static ArrayList<BookingServiceDTO> bookingServiceDTOArrayList = new ArrayList<>();

    //클릭 리스너
    private BookingServiceOwnerAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(BookingServiceOwnerAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public BookingServiceOwnerAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_bookinglist_owner, parent, false);
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

        private TextView tvBookingWalkerName;
        private TextView tvBookingOwnerDogName;
        private TextView tvBookingWalkTotalTime;
        private TextView tvBookingWalkTime;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookingWalkerName = itemView.findViewById(R.id.textView_item_booking_walker_name);
            tvBookingOwnerDogName = itemView.findViewById(R.id.textView_item_booking_owner_dog_name);
            tvBookingWalkTotalTime = itemView.findViewById(R.id.textView_item_booking_walk_total_time);
            tvBookingWalkTime = itemView.findViewById(R.id.textView_item_booking_walk_time);

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

        public void onBind(BookingServiceDTO bookingServiceDTO) {

            tvBookingWalkTime.setText(bookingServiceDTO.getOwner_id());
            tvBookingOwnerDogName.setText(bookingServiceDTO.getOwner_dog_name());
            tvBookingWalkTotalTime.setText(bookingServiceDTO.getWalk_total_time()+"분");
            tvBookingWalkTime.setText(bookingServiceDTO.getWalk_date()+" "+bookingServiceDTO.getWalk_time());
        }
    }

    public ArrayList<BookingServiceDTO> getBookingServiceDTOArrayList(){
        return bookingServiceDTOArrayList;
    }

    public void setBookingServiceDTOArrayList(ArrayList<BookingServiceDTO> bookingServiceDTOArrayList){
        this.bookingServiceDTOArrayList = bookingServiceDTOArrayList;
    }
}
