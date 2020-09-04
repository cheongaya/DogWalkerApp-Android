package com.example.dogwalker.owner;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailPrice;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailProfile;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailReview;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailSchedule;

public class OwnerWalkerDetailActivity extends BaseActivity implements View.OnClickListener {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Bundle bundle;

    FragmentWalkerDetailProfile fragmentWalkerDetailProfile;
    FragmentWalkerDetailSchedule fragmentWalkerDetailSchedule;
    FragmentWalkerDetailPrice fragmentWalkerDetailPrice;
    FragmentWalkerDetailReview fragmentWalkerDetailReview;

    Button btnFragmentProfile, btnFragmentReview, btnFragmentPrice, btnFragmentSchedule;
    TextView tvWalkerName;

    Intent intent;
    String walkerName, walkDogName, defaultWalkTime;
    int add30minTimeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_walker_detail);

        intent = getIntent();

        //intent 로 받아온 데이터
        walkerName = intent.getStringExtra("walkerName");   //도그워커 이름 데이터
        walkDogName = intent.getStringExtra("walkDogName"); //산책시킬 강아지 이름 데이터
        defaultWalkTime = intent.getStringExtra("defaultWalkTime"); //기본 산책시간 데이터
        add30minTimeCount = intent.getIntExtra("add30minTimeCount", 0); //추가한 산책시간 데이터

        fragmentManager = getSupportFragmentManager();
        //원하는 데이터를 Bundle 을 통해 전달함
        bundle = new Bundle();
        bundle.putString("walkerName", walkerName);
        bundle.putString("walkDogName", walkDogName);
        bundle.putString("defaultWalkTime", defaultWalkTime);
        bundle.putInt("add30minTimeCount", add30minTimeCount);

        btnFragmentProfile = (Button)findViewById(R.id.button_fragment_profile);
        btnFragmentSchedule = (Button)findViewById(R.id.button_fragment_schedule);
        btnFragmentReview = (Button)findViewById(R.id.button_fragment_review);
        btnFragmentPrice = (Button)findViewById(R.id.button_fragment_price);
        tvWalkerName = (TextView)findViewById(R.id.textView_walker_detail_walkerName);

        tvWalkerName.setText(walkerName);

        fragmentWalkerDetailProfile = new FragmentWalkerDetailProfile();
        fragmentWalkerDetailSchedule = new FragmentWalkerDetailSchedule();
        fragmentWalkerDetailPrice = new FragmentWalkerDetailPrice();
        fragmentWalkerDetailReview = new FragmentWalkerDetailReview();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.linearLayout_fragment_container_walker_detail, fragmentWalkerDetailProfile);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();

        btnFragmentProfile.setOnClickListener(this);
        btnFragmentSchedule.setOnClickListener(this);
        btnFragmentReview.setOnClickListener(this);
        btnFragmentPrice.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        fragmentTransaction = fragmentManager.beginTransaction();

        int id = v.getId();
        switch (id){
            //프로필 화면
            case R.id.button_fragment_profile:
                fragmentTransaction.replace(R.id.linearLayout_fragment_container_walker_detail, fragmentWalkerDetailProfile);
                fragmentTransaction.disallowAddToBackStack();
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //도그워커이름+선택한기본산책시간+추가산책시간 데이터를 bundle 을 통해 프래그먼트로 전달함
                fragmentWalkerDetailProfile.setArguments(bundle);
                break;
            //일정표 화면
            case R.id.button_fragment_schedule:
                fragmentTransaction.replace(R.id.linearLayout_fragment_container_walker_detail, fragmentWalkerDetailSchedule);
                fragmentTransaction.disallowAddToBackStack();
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //도그워커이름+선택한기본산책시간+추가산책시간 데이터를 bundle 을 통해 프래그먼트로 전달함
                fragmentWalkerDetailSchedule.setArguments(bundle);
                break;
            //가격표 화면
            case R.id.button_fragment_price:
                fragmentTransaction.replace(R.id.linearLayout_fragment_container_walker_detail, fragmentWalkerDetailPrice);
                fragmentTransaction.disallowAddToBackStack();
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //도그워커이름+선택한기본산책시간+추가산책시간 데이터를 bundle 을 통해 프래그먼트로 전달함
                fragmentWalkerDetailSchedule.setArguments(bundle);
                break;
            //리뷰 화면
            case R.id.button_fragment_review:
                fragmentTransaction.replace(R.id.linearLayout_fragment_container_walker_detail, fragmentWalkerDetailReview);
                fragmentTransaction.disallowAddToBackStack();
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //도그워커이름+선택한기본산책시간+추가산책시간 데이터를 bundle 을 통해 프래그먼트로 전달함
                fragmentWalkerDetailSchedule.setArguments(bundle);
                break;
            default:
                break;
        }
    }
}