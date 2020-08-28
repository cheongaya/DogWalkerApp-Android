package com.example.dogwalker.owner.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.retrofit2.RetrofitApi;
import com.example.dogwalker.retrofit2.RetrofitUtil;

public class FragmentBase extends Fragment {

    public static ApplicationClass applicationClass;
    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    public static RetrofitApi retrofitApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ApplicationClass 객체 생성
        applicationClass = (ApplicationClass) getActivity().getApplicationContext();
        //객체와 인터페이스 연결
        retrofitApi = new RetrofitUtil().getRetrofitApi();
    }

    //로그 : 액티비티명 + 함수명 + 원하는 데이터를 한번에 보기위한 로그
    public void makeLog(String methodData, String strData) {
        Log.d(TAG, className + "_" + methodData + "_" + strData);
    }

    //토스트메세지 : 귀찮음을 없애기 위해 토스트를 띄우는 함수를 만듦
    public void makeToast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, className + "_onAttach()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, className + "_onActivityCreated()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, className + "_onDestroyView()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, className + "_onDetach()");
    }
}
