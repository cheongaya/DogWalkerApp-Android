package com.example.dogwalker.owner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dogwalker.R;

public class FragmentWalkerDetailReview extends FragmentBase {

    String walkerName;  //bundle 을 통해 전달받은 도그워커 이름 데이터

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        if(getArguments() != null){
            //bundle 을 통해 전달받은 도그워커 이름 데이터
            walkerName = getArguments().getString("walkerName");
//            makeToast(walkerName);
        }

        return inflater.inflate(R.layout.fragment_walker_detail_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
