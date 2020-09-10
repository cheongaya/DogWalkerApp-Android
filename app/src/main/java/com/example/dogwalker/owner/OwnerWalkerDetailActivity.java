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
import com.example.dogwalker.data.WalkerlistDTO;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailPrice;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailProfile;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailReview;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailSchedule;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    String name, introduce, profileImgUrl, location;
    int priceThirtyMinutes, priceSixtyMinutes, priceAddLargeSize, priceAddOneDog, priceAddHoliday;
    int walkableTypeS, walkableTypeM, walkableTypeL;
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

        //해당 도그워커의 정보 DB에서 불러오기
        selectWalkerInfoToDB();

        //id 연결
        btnFragmentProfile = (Button)findViewById(R.id.button_fragment_profile);
        btnFragmentSchedule = (Button)findViewById(R.id.button_fragment_schedule);
        btnFragmentReview = (Button)findViewById(R.id.button_fragment_review);
        btnFragmentPrice = (Button)findViewById(R.id.button_fragment_price);
        tvWalkerName = (TextView)findViewById(R.id.textView_walker_detail_walkerName);

        fragmentWalkerDetailProfile = new FragmentWalkerDetailProfile();
        fragmentWalkerDetailSchedule = new FragmentWalkerDetailSchedule();
        fragmentWalkerDetailPrice = new FragmentWalkerDetailPrice();
        fragmentWalkerDetailReview = new FragmentWalkerDetailReview();

        btnFragmentProfile.setOnClickListener(this);
        btnFragmentSchedule.setOnClickListener(this);
        btnFragmentReview.setOnClickListener(this);
        btnFragmentPrice.setOnClickListener(this);
    }

    //해당 도그워커의 정보 DB에서 불러오기
    public void selectWalkerInfoToDB(){

        Call<List<WalkerlistDTO>> call = retrofitApi.selectWalkerProfileData(walkerName);   //도그워커 아이디가 매개변수
        call.enqueue(new Callback<List<WalkerlistDTO>>() {
            @Override
            public void onResponse(Call<List<WalkerlistDTO>> call, Response<List<WalkerlistDTO>> response) {

                List<WalkerlistDTO> walkerlistDTOList = response.body();
                //프로필 정보
                name = walkerlistDTOList.get(0).getName();
                introduce = walkerlistDTOList.get(0).getIntroduce();
                location = walkerlistDTOList.get(0).getLocation();
                profileImgUrl = walkerlistDTOList.get(0).getProfile_img();
                //가격 정보
                priceThirtyMinutes = walkerlistDTOList.get(0).getPrice_thirty_minutes();
                priceSixtyMinutes = walkerlistDTOList.get(0).getPrice_sixty_minutes();
                priceAddLargeSize = walkerlistDTOList.get(0).getAddprice_large_size();
                priceAddOneDog = walkerlistDTOList.get(0).getAddprice_one_dog();
                priceAddHoliday = walkerlistDTOList.get(0).getAddprice_holiday();
                //산책가능 유형 정보
                walkableTypeS = walkerlistDTOList.get(0).getWalkable_type_s();
                walkableTypeM = walkerlistDTOList.get(0).getWalkable_type_m();
                walkableTypeL = walkerlistDTOList.get(0).getWalkable_type_l();

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "반응 기본산채각격 : " + priceThirtyMinutes);

                //번들에 전달할 데이터를 넣는다
                putDataIntoBundle();

                tvWalkerName.setText(name);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.linearLayout_fragment_container_walker_detail, fragmentWalkerDetailProfile);
//        fragmentTransaction.addToBackStack(null);
                fragmentTransaction.disallowAddToBackStack();
                fragmentTransaction.commit();
                fragmentWalkerDetailProfile.setArguments(bundle);
            }

            @Override
            public void onFailure(Call<List<WalkerlistDTO>> call, Throwable t) {

            }
        });

    }

    //번들에 전달할 데이터를 담는다
    public void putDataIntoBundle(){
        //원하는 데이터를 Bundle 을 통해 전달함
        bundle = new Bundle();
        bundle.putString("walkerName", walkerName);             //선택한 도그워커 아이디
        bundle.putString("walkDogName", walkDogName);           //선택한 반려견 이름
        bundle.putString("defaultWalkTime", defaultWalkTime);   //선택한 기본 산책시간
        bundle.putInt("add30minTimeCount", add30minTimeCount);  //선택한 30분 추가 횟수
        //여기서부터 bundle 에 담을 정보는 선택한 도그워커의 프로필 정보이다
        bundle.putString("introduce", introduce);
        bundle.putString("location", location);
        bundle.putString("profileImgUrl", profileImgUrl);
        bundle.putInt("priceThirtyMinutes", priceThirtyMinutes);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "번들 기본산채각격 : " + priceThirtyMinutes);
        bundle.putInt("priceSixtyMinutes", priceSixtyMinutes);
        bundle.putInt("priceAddLargeSize", priceAddLargeSize);
        bundle.putInt("priceAddOneDog", priceAddOneDog);
        bundle.putInt("priceAddHoliday", priceAddHoliday);
        bundle.putInt("walkableTypeS", walkableTypeS);
        bundle.putInt("walkableTypeM", walkableTypeM);
        bundle.putInt("walkableTypeL", walkableTypeL);
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
                fragmentWalkerDetailPrice.setArguments(bundle);
                break;
            //리뷰 화면
            case R.id.button_fragment_review:
                fragmentTransaction.replace(R.id.linearLayout_fragment_container_walker_detail, fragmentWalkerDetailReview);
                fragmentTransaction.disallowAddToBackStack();
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //도그워커이름+선택한기본산책시간+추가산책시간 데이터를 bundle 을 통해 프래그먼트로 전달함
                fragmentWalkerDetailReview.setArguments(bundle);
                break;
            default:
                break;
        }
    }
}