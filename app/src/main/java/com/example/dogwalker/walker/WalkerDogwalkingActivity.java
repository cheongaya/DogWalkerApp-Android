package com.example.dogwalker.walker;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dogwalker.GoogleMapActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.owner.BookingServiceOwnerAdapter;
import com.example.dogwalker.owner.OwnerBookingCalendarActivity;
import com.example.dogwalker.owner.fragment.FragmentSelectMyDogDialog;
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerDogwalkingActivity extends WalkerBottomNavigation implements View.OnClickListener {

    RecyclerView recyclerViewIngBookingList, recyclerViewEndBookingList;
    BookingServiceAdapter ingBookingServiceAdapter, endBookingServiceAdapter;
    List<BookingServiceDTO> ingBookingServiceDTOList, endBookingServiceDTOList;

    LinearLayout linearIngBookingCnt, linearEndBookingCnt;
    TextView tvIngBookingListNull, tvEndBookingListNull;
    Button btnIngBookingList, btnEndBookingList;
    ImageButton btnMapTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_dogwalking);

        //예약 리스트가 없을때의 텍스트 표시
        tvIngBookingListNull = (TextView)findViewById(R.id.textView_ing_booking_list_null);
        tvEndBookingListNull = (TextView)findViewById(R.id.textView_end_booking_list_null);
        //진행중 / 지난 예약 버튼
        btnIngBookingList = (Button)findViewById(R.id.button_booking_ing_list);
        btnEndBookingList = (Button)findViewById(R.id.button_booking_end_list);
        //진행중 / 지난 예약 컨텐츠 영역
        linearIngBookingCnt = (LinearLayout)findViewById(R.id.linearLayout_ing_booking_list_content);
        linearEndBookingCnt = (LinearLayout)findViewById(R.id.linearLayout_end_booking_list_content);

        //TODO:구글맵 테스트 코드
        btnMapTest = (ImageButton)findViewById(R.id.imageButton_walker_booking_calendar);

        //리사이클러뷰 (진행중 예약) 초기화 셋팅
        IngRecyclerViewInitSetting();
        //리사이클러뷰 (지난 예약) 초기화 셋팅
        EndRecyclerViewInitSetting();

        //클릭 리스너 연결
        btnIngBookingList.setOnClickListener(this);
        btnEndBookingList.setOnClickListener(this);
        //TODO:구글맵 테스트 코드
        btnMapTest.setOnClickListener(this);

//        //진행중 예약 아이템 클릭시
//        ingBookingServiceAdapter.setOnItemClickListener(new BookingServiceAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
////                Intent intent = new Intent(WalkerDogwalkingActivity.this, WalkerStopWatchActivity.class);
////                startActivity(intent);
//            }
//        });


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
        recyclerViewIngBookingList = (RecyclerView)findViewById(R.id.recyclerView_ing_bookinglist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewIngBookingList.setLayoutManager(linearLayoutManager); //?? 주석??
        ingBookingServiceAdapter = new BookingServiceAdapter(this);
        recyclerViewIngBookingList.setAdapter(ingBookingServiceAdapter);
    }

    //지난 예약 -리사이클러뷰 초기화 셋팅
    public void EndRecyclerViewInitSetting(){
        recyclerViewEndBookingList = (RecyclerView)findViewById(R.id.recyclerView_end_bookinglist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewEndBookingList.setLayoutManager(linearLayoutManager); //?? 주석??
        endBookingServiceAdapter = new BookingServiceAdapter(this);
        recyclerViewEndBookingList.setAdapter(endBookingServiceAdapter);
    }

    //진행중 예약 - DB에서 진행중 예약 리스트 데이터 불러오기
    public void loadIngBookingServiceDataToDB(){

        Call<List<BookingServiceDTO>> call = retrofitApi.selectWalkerIngBookingServiceData(applicationClass.currentWalkerID);
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

                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "진행중 예약리스트 사이즈 ingBookingServiceDTOList : " + ingBookingServiceDTOList.size() );
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "진행중 예약리스트 사이즈 ingBookingServiceDTOArrayList : " + ingBookingServiceDTOArrayList.size() );

                    //TODO: 확인용
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

        Call<List<BookingServiceDTO>> call = retrofitApi.selectWalkerEndBookingServiceData(applicationClass.currentWalkerID);
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

                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "지난 예약리스트 사이즈 endBookingServiceDTOList : " + endBookingServiceDTOList.size() );
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "지난 예약리스트 사이즈 endBookingServiceDTOArrayList : " + endBookingServiceDTOArrayList.size() );

                    //TODO: 확인용
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

            //버튼 - 예약한 산책 클릭시
            case R.id.button_booking_ing_list:
                //버튼 색상 바꾸기 ContextCompat.getColor(getContext(), R.color.colorMain
                btnIngBookingList.setTextColor(getColor(R.color.colorSub));
                btnEndBookingList.setTextColor(getColor(R.color.colorBlack));
                //컨텐츠 노출/비노출
                linearIngBookingCnt.setVisibility(View.VISIBLE);
                linearEndBookingCnt.setVisibility(View.GONE);
                break;
            //버튼 - 지난 예약 클릭시
            case R.id.button_booking_end_list:
                //버튼 색상 바꾸기
                btnIngBookingList.setTextColor(getColor(R.color.colorBlack));
                btnEndBookingList.setTextColor(getColor(R.color.colorSub));
                //컨텐츠 노출/비노출
                linearIngBookingCnt.setVisibility(View.GONE);
                linearEndBookingCnt.setVisibility(View.VISIBLE);

                //DB에서 지난 예약 리스트 데이터 불러오기
                loadEndBookingServiceDataToDB();
                break;

            //TODO:구글맵 테스트 코드
            case R.id.imageButton_walker_booking_calendar:
                Intent intent = new Intent(WalkerDogwalkingActivity.this, GoogleMapActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
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