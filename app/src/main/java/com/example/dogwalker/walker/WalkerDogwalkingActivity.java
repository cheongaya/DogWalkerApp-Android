package com.example.dogwalker.walker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.dogwalker.R;
import com.example.dogwalker.data.WalkerlistDTO;
import com.example.dogwalker.owner.OwnerWalkerlistAdapter;
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerDogwalkingActivity extends WalkerBottomNavigation {

    RecyclerView recyclerViewBookingList;
    BookingServiceAdapter bookingServiceAdapter;
    List<BookingServiceDTO> bookingServiceDTOList;

    TextView tvBookingListNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_dogwalking);

        //예약 리스트가 없을때
        tvBookingListNull = (TextView)findViewById(R.id.textView_booking_list_null);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        //DB에서 예약 리스트 데이터 불러오기
        loadBookingServiceDataToDB();

        //아이템 클릭시
        bookingServiceAdapter.setOnItemClickListener(new BookingServiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                Intent intent = new Intent(WalkerDogwalkingActivity.this, WalkerStopWatchActivity.class);
//                startActivity(intent);
            }
        });
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){
        recyclerViewBookingList = (RecyclerView)findViewById(R.id.recyclerView_bookinglist);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewBookingList.setLayoutManager(linearLayoutManager); //?? 주석??

        bookingServiceAdapter = new BookingServiceAdapter(this);
        recyclerViewBookingList.setAdapter(bookingServiceAdapter);
    }

    //DB에서 예약 리스트 데이터 불러오기
    public void loadBookingServiceDataToDB(){

        Call<List<BookingServiceDTO>> call = retrofitApi.selectBookingServiceData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<List<BookingServiceDTO>>() {
            @Override
            public void onResponse(Call<List<BookingServiceDTO>> call, Response<List<BookingServiceDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "예약리스트 데이터 조회 성공");
                bookingServiceDTOList = response.body();    //List

                //예약리스트 데이터 setText 해주기 (position 번째)
                if(bookingServiceDTOList.size() > 0){

                    //검색된 도그워커 데이터가 있을때
                    recyclerViewBookingList.setVisibility(View.VISIBLE);
                    tvBookingListNull.setVisibility(View.GONE);

                    ArrayList<BookingServiceDTO> bookingServiceDTOArrayList = new ArrayList<>();
                    bookingServiceDTOArrayList.addAll(bookingServiceDTOList);

                    bookingServiceAdapter.setBookingServiceDTOArrayList(bookingServiceDTOArrayList);
                    bookingServiceAdapter.notifyDataSetChanged();

                }else{
                    //검색된 도그워커 데이터가 없을때
                    recyclerViewBookingList.setVisibility(View.GONE);
                    tvBookingListNull.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<BookingServiceDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "예약리스트 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());

            }
        });

    }



    @Override
    int getContentViewId() {
        return R.layout.activity_walker_dogwalking;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavWorker01;
    }
}