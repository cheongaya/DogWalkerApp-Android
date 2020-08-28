package com.example.dogwalker.owner.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dogwalker.R;

public class FragmentWalkerDetailProfile extends Fragment {

    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    String walkerName;  //bundle 을 통해 전달받은 도그워커 이름 데이터

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        if(getArguments() != null){
            //bundle 을 통해 전달받은 도그워커 이름 데이터
            walkerName = getArguments().getString("walkerName");
            makeToast(walkerName);
        }

        return inflater.inflate(R.layout.fragment_walker_detail_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //로그 : 액티비티명 + 함수명 + 원하는 데이터를 한번에 보기위한 로그
    public void makeLog(String methodData, String strData) {
        Log.d(TAG, className + "_" + methodData + "_" + strData);
    }

    //토스트메세지 : 귀찮음을 없애기 위해 토스트를 띄우는 함수를 만듦
    public void makeToast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }
}
