package com.example.dogwalker.owner.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.FragmentWalkerDetailProfileBinding;

public class FragmentWalkerDetailProfile extends FragmentBase {

    FragmentWalkerDetailProfileBinding binding;

    String walkerName;  //bundle 을 통해 전달받은 도그워커 이름 데이터
    String introduce, location, profileImgUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_walker_detail_profile, container, false);
        binding.setFragment(this);
        View view = binding.getRoot();

        if(getArguments() != null){
            //bundle 을 통해 전달받은 도그워커 이름 데이터
            walkerName = getArguments().getString("walkerName");
            introduce = getArguments().getString("introduce");
            location = getArguments().getString("location");
            profileImgUrl = getArguments().getString("profileImgUrl");
//            makeToast(walkerName);
        }

        //화면에 데이터 표시
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "자기소개 : " + introduce);
        binding.textViewWalkerProfileIntroduce.setText(introduce);  //자기소개
        binding.textViewWalkerProfileLocation.setText(location);    //위치정보
        //도그워커 프로필 이미지
        Glide.with(getActivity())
                .load(profileImgUrl)
                .override(300,300)
                .apply(applicationClass.requestOptions.fitCenter())
                .into(binding.imageViewWalkerProfileImg);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
