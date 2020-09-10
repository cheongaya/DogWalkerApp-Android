package com.example.dogwalker.owner;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.NaverMapActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.data.WalkerlistDTO;
import com.example.dogwalker.databinding.ActivityNaverMapBindingImpl;
import com.example.dogwalker.databinding.ActivityOwnerWalkerlistBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerWalkerlistActivity extends BaseActivity {

    ActivityOwnerWalkerlistBinding binding;

    RecyclerView recyclerViewWalkerlist;
    OwnerWalkerlistAdapter walkerlistAdapter;
    List<WalkerlistDTO> walkerlistDTOList;

    TextView tvSearchWalkerlistNull;
    //Intent 객체에서 받아온 데이터
    String walkDogName;
    String defaultWalkTime;
    int add30minTimeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_walkerlist);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_owner_walkerlist);
        binding.setActivity(this);

        //검색된 도그워커가 없을때
        tvSearchWalkerlistNull = (TextView)findViewById(R.id.textView_search_walkerlist_null);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        //DB에서 도그워커 리스트 데이터 불러오기
        loadWalkerDataToDB();

        //FragmentWalkDialog 에서 받아온 산책시간 선택 데이터
        Intent intent = getIntent();
        walkDogName = intent.getStringExtra("seletedDog");
        defaultWalkTime = intent.getStringExtra("defaultWalkTime");
        add30minTimeCount = intent.getIntExtra("add30minTimeCount", 0);

        //아이템 클릭 리스너
        walkerlistAdapter.setOnItemClickListener(new OwnerWalkerlistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //클릭했을때 처리
//                makeToast("aaa");
                Intent intentWalkerDetail = new Intent(OwnerWalkerlistActivity.this, OwnerWalkerDetailActivity.class);
                String walkerName = walkerlistAdapter.getWalkerlistDTOArrayList().get(position).getName();
                intentWalkerDetail.putExtra("walkerName", walkerName);                  //도그워커 이름
                intentWalkerDetail.putExtra("walkDogName", walkDogName);                //산책시킬 강아지 이름
                intentWalkerDetail.putExtra("defaultWalkTime", defaultWalkTime);        //산책 기본 시간
                intentWalkerDetail.putExtra("add30minTimeCount", add30minTimeCount);    //산책 추가 시간
                startActivity(intentWalkerDetail);
//                finish();

            }
        });
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){
        recyclerViewWalkerlist =  (RecyclerView)findViewById(R.id.recyclerView_walkerlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewWalkerlist.setLayoutManager(linearLayoutManager); //?? 주석??

        walkerlistAdapter = new OwnerWalkerlistAdapter(this);
        recyclerViewWalkerlist.setAdapter(walkerlistAdapter);
    }

    //DB에서 도그워커 데이터 불러오기
    public void loadWalkerDataToDB(){
        Call<List<WalkerlistDTO>> call = retrofitApi.selectWalkerlistData("user_walker");
        call.enqueue(new Callback<List<WalkerlistDTO>>() {
            @Override
            public void onResponse(Call<List<WalkerlistDTO>> call, Response<List<WalkerlistDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "도그워커 데이터 조회 성공");
                walkerlistDTOList = response.body();    //List

                //도그워커 데이터 setText 해주기 (position 번째)
                if(walkerlistDTOList.size() > 0){

                    //검색된 도그워커 데이터가 있을때
                    recyclerViewWalkerlist.setVisibility(View.VISIBLE);
                    tvSearchWalkerlistNull.setVisibility(View.GONE);

                    ArrayList<WalkerlistDTO> walkerlistDTOArrayList = new ArrayList<>();
                    walkerlistDTOArrayList.addAll(walkerlistDTOList);

                    walkerlistAdapter.setWalkerlistDTOArrayList(walkerlistDTOArrayList);
                    walkerlistAdapter.notifyDataSetChanged();

                }else{
                    //검색된 도그워커 데이터가 없을때
                    recyclerViewWalkerlist.setVisibility(View.GONE);
                    tvSearchWalkerlistNull.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<WalkerlistDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "도그워커 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());

            }
        });

    }

    //지도 아이콘 클릭시 - 현재 위치 기반 주변 도그워커 보여주기
    public void onClickMapWalkerSearch(View view){

        Intent naverMapIntent = new Intent(OwnerWalkerlistActivity.this, NaverMapActivity.class);
        naverMapIntent.putExtra("walkDogName", walkDogName);                //산책시킬 강아지 이름
        naverMapIntent.putExtra("defaultWalkTime", defaultWalkTime);        //산책 기본 시간
        naverMapIntent.putExtra("add30minTimeCount", add30minTimeCount);    //산책 추가 시간
        startActivity(naverMapIntent);

    }

    //키워드 검색시 - 키워드로 검색한 도그워커 리스트 보여주기
    public void onClickKeywordWalkerSearch(View view){

    }

    //필터링 - 지역 선택
    public void onClickFilteringLocation(View view){

    }

    //필터링 - 날짜 선택
    public void onClickFilteringDate(View view){

    }

}