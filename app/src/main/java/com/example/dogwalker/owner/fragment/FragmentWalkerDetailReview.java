package com.example.dogwalker.owner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dogwalker.R;
import com.example.dogwalker.databinding.FragmentWalkerDetailReviewBinding;
import com.example.dogwalker.retrofit2.response.WalkerReviewDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentWalkerDetailReview extends FragmentBase {

    FragmentWalkerDetailReviewBinding binding;
    String walkerName, profileImgUrl;  //bundle 을 통해 전달받은 도그워커 이름 데이터 + 프로필 이미지 URL
    WalkerReviewAdapter walkerReviewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_walker_detail_review, container, false);
        binding.setFragment(this);
        View view = binding.getRoot();

        if(getArguments() != null){
            //bundle 을 통해 전달받은 도그워커 이름 데이터
            walkerName = getArguments().getString("walkerName");
            profileImgUrl = getArguments().getString("profileImgUrl");
//            makeToast(walkerName);
        }

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //DB 에서 리뷰 데이터 불러오기
        selectWalkerReviewDataToDB();
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerViewWalkerReview.setLayoutManager(linearLayoutManager); //?? 주석??

        walkerReviewAdapter = new WalkerReviewAdapter(getActivity());
        binding.recyclerViewWalkerReview.setAdapter(walkerReviewAdapter);
    }

    //DB 에서 리뷰 데이터 불러오기
    public void selectWalkerReviewDataToDB(){

        Call<List<WalkerReviewDTO>> call = retrofitApi.selectWalkerReviewData(walkerName);
        call.enqueue(new Callback<List<WalkerReviewDTO>>() {
            @Override
            public void onResponse(Call<List<WalkerReviewDTO>> call, Response<List<WalkerReviewDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "리뷰 데이터 조회 성공");
                List<WalkerReviewDTO> bookingServiceDTOList = response.body();    //List
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "리뷰 리스트 사이즈 : " + bookingServiceDTOList.size());
                binding.textViewWalkerReviewCount.setText(bookingServiceDTOList.size()+"");
                /**
                 * 리사이클러뷰 관련 코드
                 */
                ArrayList<WalkerReviewDTO> bookingServiceDTOArrayList = new ArrayList<>();
                bookingServiceDTOArrayList.addAll(bookingServiceDTOList);
                walkerReviewAdapter.setWalkerReviewDTOArrayList(bookingServiceDTOArrayList);
                walkerReviewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<WalkerReviewDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "리뷰 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "error : "+ t.toString());

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
