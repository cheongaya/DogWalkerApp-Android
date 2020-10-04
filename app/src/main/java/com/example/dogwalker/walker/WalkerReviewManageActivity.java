package com.example.dogwalker.walker;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerReviewManageBinding;
import com.example.dogwalker.retrofit2.response.WalkerReviewManageDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerReviewManageActivity extends BaseActivity {

    ActivityWalkerReviewManageBinding binding;
    WalkerReviewManageAdapter walkerReviewManageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_review_list);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_review_manage);
        binding.setActivity(this);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //DB 에서 리뷰 데이터 불러오기
        selectWalkerReplyDataToDB();
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerViewReviewList.setLayoutManager(linearLayoutManager); //?? 주석??

        walkerReviewManageAdapter = new WalkerReviewManageAdapter(this);
        binding.recyclerViewReviewList.setAdapter(walkerReviewManageAdapter);
    }

    //DB 에서 리뷰 데이터 불러오기
    public void selectWalkerReplyDataToDB(){

        Call<List<WalkerReviewManageDTO>> call = retrofitApi.selectWalkerReviewManageData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<List<WalkerReviewManageDTO>>() {
            @Override
            public void onResponse(Call<List<WalkerReviewManageDTO>> call, Response<List<WalkerReviewManageDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "리뷰 데이터 조회 성공");
                List<WalkerReviewManageDTO> walkerReviewManageDTOList = response.body();    //List
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "리뷰 리스트 사이즈 : " + walkerReviewManageDTOList.size());
                binding.textViewWalkerReviewCount.setText(walkerReviewManageDTOList.size()+"");
                /**
                 * 리사이클러뷰 관련 코드
                 */
                ArrayList<WalkerReviewManageDTO> walkerReviewManageDTOArrayList = new ArrayList<>();
                walkerReviewManageDTOArrayList.addAll(walkerReviewManageDTOList);
                walkerReviewManageAdapter.setWalkerReviewManageDTOArrayList(walkerReviewManageDTOArrayList);
                walkerReviewManageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<WalkerReviewManageDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "리뷰 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "error : "+ t.toString());

            }
        });
    }
}