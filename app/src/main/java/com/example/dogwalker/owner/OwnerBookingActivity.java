package com.example.dogwalker.owner;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.R;
import com.example.dogwalker.decorators.BookingDaysDecorator;
import com.example.dogwalker.decorators.NoneDaysDecorator;
import com.example.dogwalker.decorators.SaturdayDecorator;
import com.example.dogwalker.decorators.SundayDecorator;
import com.example.dogwalker.owner.fragment.FragmentSelectMyDogDialog;
import com.example.dogwalker.owner.fragment.FragmentTimeDialog;
import com.example.dogwalker.owner.fragment.FragmentWalkDialog;
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;
import com.example.dogwalker.retrofit2.response.NonServiceDateDTO;
import com.example.dogwalker.walker.BookingServiceAdapter;
import com.example.dogwalker.walker.WalkerScheduleActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerBookingActivity extends OwnerBottomNavigation implements View.OnClickListener{

    RecyclerView recyclerViewIngBookingList, recyclerViewEndBookingList;
    BookingServiceOwnerAdapter ingBookingServiceAdapter, endBookingServiceAdapter;
    List<BookingServiceDTO> ingBookingServiceDTOList, endBookingServiceDTOList;

    LinearLayout linearIngBookingCnt, linearEndBookingCnt;
    TextView tvIngBookingListNull, tvEndBookingListNull;
    Button btnIngBookingList, btnEndBookingList, btnBookingPage;
    ImageButton btnCalendarMonthView; //월간달력보기 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_booking);

        btnBookingPage = (Button)findViewById(R.id.button_booking_page);
        btnCalendarMonthView = (ImageButton)findViewById(R.id.imageButton_calendar_month_view);

        //예약 리스트가 없을때의 텍스트 표시
        tvIngBookingListNull = (TextView)findViewById(R.id.textView_ing_booking_list_null_owner);
        tvEndBookingListNull = (TextView)findViewById(R.id.textView_end_booking_list_null_owner);
        //진행중 / 지난 예약 버튼
        btnIngBookingList = (Button)findViewById(R.id.button_booking_ing_list_owner);
        btnEndBookingList = (Button)findViewById(R.id.button_booking_end_list_owner);
        //진행중 / 지난 예약 컨텐츠 영역
        linearIngBookingCnt = (LinearLayout)findViewById(R.id.linearLayout_ing_booking_list_content_owner);
        linearEndBookingCnt = (LinearLayout)findViewById(R.id.linearLayout_end_booking_list_content_owner);

        //리사이클러뷰 (진행중 예약) 초기화 셋팅
        IngRecyclerViewInitSetting();
        //리사이클러뷰 (지난 예약) 초기화 셋팅
        EndRecyclerViewInitSetting();

        //클릭 리스너 연결
        btnBookingPage.setOnClickListener(this);
        btnCalendarMonthView.setOnClickListener(this);
        btnIngBookingList.setOnClickListener(this);
        btnEndBookingList.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //DB에서 진행중 예약 리스트 데이터 불러오기
        loadIngBookingServiceDataToDB();
//        //DB에서 지난 예약 리스트 데이터 불러오기
//        loadEndBookingServiceDataToDB();
    }

    //진행중 예약 - 리사이클러뷰 초기화 셋팅
    public void IngRecyclerViewInitSetting(){
        recyclerViewIngBookingList = (RecyclerView)findViewById(R.id.recyclerView_ing_bookinglist_owner);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewIngBookingList.setLayoutManager(linearLayoutManager); //?? 주석??
        ingBookingServiceAdapter = new BookingServiceOwnerAdapter(this);
        recyclerViewIngBookingList.setAdapter(ingBookingServiceAdapter);
    }

    //지난 예약 -리사이클러뷰 초기화 셋팅
    public void EndRecyclerViewInitSetting(){
        recyclerViewEndBookingList = (RecyclerView)findViewById(R.id.recyclerView_end_bookinglist_owner);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewEndBookingList.setLayoutManager(linearLayoutManager); //?? 주석??
        endBookingServiceAdapter = new BookingServiceOwnerAdapter(this);
        recyclerViewEndBookingList.setAdapter(endBookingServiceAdapter);
    }

    //진행중 예약 - DB에서 진행중 예약 리스트 데이터 불러오기
//    public void loadIngBookingServiceDataToDB(){
//
//        Call<List<BookingServiceDTO>> call = retrofitApi.selectOwnerBookingServiceData(applicationClass.currentWalkerID);
//        call.enqueue(new Callback<List<BookingServiceDTO>>() {
//            @Override
//            public void onResponse(Call<List<BookingServiceDTO>> call, Response<List<BookingServiceDTO>> response) {
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "예약리스트 데이터 조회 성공");
//                List<BookingServiceDTO> bookingServiceDTOList = response.body();    //List
//
//                ArrayList<BookingServiceDTO> bookingServiceDTOArrayList = new ArrayList<>();
//                bookingServiceDTOArrayList.addAll(bookingServiceDTOList);
//                bookingServiceOwnerAdapter.setBookingServiceDTOArrayList(bookingServiceDTOArrayList);
//                bookingServiceOwnerAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(Call<List<BookingServiceDTO>> call, Throwable t) {
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "예약리스트 데이터 조회 실패");
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
//
//            }
//        });
//
//    }

    //진행중 예약 - DB에서 진행중 예약 리스트 데이터 불러오기
    public void loadIngBookingServiceDataToDB(){

        Call<List<BookingServiceDTO>> call = retrofitApi.selectOwnerIngBookingServiceData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<List<BookingServiceDTO>>() {
            @Override
            public void onResponse(Call<List<BookingServiceDTO>> call, Response<List<BookingServiceDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "진행중 예약리스트 데이터 조회 성공");
                ingBookingServiceDTOList = response.body();    //List

                //예약리스트 데이터 setText 해주기 (position 번째)
                if(ingBookingServiceDTOList.size() > 0){

                    //검색된 도그워커 데이터가 있을때
                    recyclerViewIngBookingList.setVisibility(View.VISIBLE);
                    tvIngBookingListNull.setVisibility(View.GONE);

                    ArrayList<BookingServiceDTO> ingBookingServiceDTOArrayList = new ArrayList<>();
                    ingBookingServiceDTOArrayList.addAll(ingBookingServiceDTOList);

                    //TODO: 확인용
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "진행중 예약리스트 사이즈 ingBookingServiceDTOList : " + ingBookingServiceDTOList.size() );
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "진행중 예약리스트 사이즈 ingBookingServiceDTOArrayList : " + ingBookingServiceDTOArrayList.size() );

                    for(int i=0; i<ingBookingServiceDTOList.size(); i++){
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "진행중 예약리스트 ingBookingServiceDTOList : " + ingBookingServiceDTOList.get(i).getOwner_id());
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "진행중 예약리스트 ingBookingServiceDTOArrayList : " + ingBookingServiceDTOArrayList.get(i).getOwner_id());
                    }

                    ingBookingServiceAdapter.setBookingServiceDTOArrayList(ingBookingServiceDTOArrayList);
                    ingBookingServiceAdapter.notifyDataSetChanged();

                }else{
                    //검색된 도그워커 데이터가 없을때
                    recyclerViewIngBookingList.setVisibility(View.GONE);
                    tvIngBookingListNull.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<BookingServiceDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "진행중 예약리스트 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());

            }
        });
    }

    //지난 예약 - DB에서 지난 예약 리스트 데이터 불러오기
    public void loadEndBookingServiceDataToDB(){

        Call<List<BookingServiceDTO>> call = retrofitApi.selectOwnerEndBookingServiceData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<List<BookingServiceDTO>>() {
            @Override
            public void onResponse(Call<List<BookingServiceDTO>> call, Response<List<BookingServiceDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "지난 예약리스트 데이터 조회 성공");
                endBookingServiceDTOList = response.body();    //List

                //예약리스트 데이터 setText 해주기 (position 번째)
                if(endBookingServiceDTOList.size() > 0){

                    //검색된 도그워커 데이터가 있을때
                    recyclerViewEndBookingList.setVisibility(View.VISIBLE);
                    tvEndBookingListNull.setVisibility(View.GONE);

                    ArrayList<BookingServiceDTO> endBookingServiceDTOArrayList = new ArrayList<>();
                    endBookingServiceDTOArrayList.addAll(endBookingServiceDTOList);

                    //TODO: 확인용
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "지난 예약리스트 사이즈 endBookingServiceDTOList : " + endBookingServiceDTOList.size() );
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "지난 예약리스트 사이즈 endBookingServiceDTOArrayList : " + endBookingServiceDTOArrayList.size() );

                    for(int i=0; i<endBookingServiceDTOList.size(); i++){
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "지난 예약리스트 endBookingServiceDTOList : " + endBookingServiceDTOList.get(i).getOwner_id());
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "지난 예약리스트 endBookingServiceDTOArrayList : " + endBookingServiceDTOArrayList.get(i).getOwner_id());
                    }

                    endBookingServiceAdapter.setBookingServiceDTOArrayList(endBookingServiceDTOArrayList);
                    endBookingServiceAdapter.notifyDataSetChanged();

                }else{
                    //검색된 도그워커 데이터가 없을때
                    recyclerViewEndBookingList.setVisibility(View.GONE);
                    tvEndBookingListNull.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<BookingServiceDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "지난 예약리스트 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //버튼 - 예약하기 클릭시 -> 강아지 선택 프래그먼트 다이얼로그 띄우기
            case R.id.button_booking_page :
                Bundle bundle = new Bundle();
                FragmentSelectMyDogDialog selectMyDogDialog = new FragmentSelectMyDogDialog();
                selectMyDogDialog.setArguments(bundle);
                selectMyDogDialog.show(getSupportFragmentManager(), "fragmentSelectedMyDog");
                break;
            //버튼 - 달력 클릭시
            case R.id.imageButton_calendar_month_view :
                Intent intent = new Intent(this, OwnerBookingCalendarActivity.class);
                startActivity(intent);
                break;
            //버튼 - 산책 예약 클릭시
            case R.id.button_booking_ing_list_owner:
                //버튼 색상 바꾸기 ContextCompat.getColor(getContext(), R.color.colorMain
                btnIngBookingList.setTextColor(getColor(R.color.colorSub));
                btnEndBookingList.setTextColor(getColor(R.color.colorBlack));
                //컨텐츠 노출/비노출
                linearEndBookingCnt.setVisibility(View.GONE);
                linearIngBookingCnt.setVisibility(View.VISIBLE);
                break;
            //버튼 - 지난 예약 클릭시
            case R.id.button_booking_end_list_owner:
                //버튼 색상 바꾸기
                btnIngBookingList.setTextColor(getColor(R.color.colorBlack));
                btnEndBookingList.setTextColor(getColor(R.color.colorSub));
                //컨텐츠 노출/비노출
                linearIngBookingCnt.setVisibility(View.GONE);
                linearEndBookingCnt.setVisibility(View.VISIBLE);

                //DB에서 지난 예약 리스트 데이터 불러오기
                loadEndBookingServiceDataToDB();
                break;
        }
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_owner_booking;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavOwner01;
    }

}