package com.example.dogwalker.owner;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.data.WalkerDTO;
import com.example.dogwalker.data.WalkerlistDTO;
import com.example.dogwalker.databinding.ActivityOwnerWalkerDetailBinding;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailPrice;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailProfile;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailReview;
import com.example.dogwalker.owner.fragment.FragmentWalkerDetailSchedule;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerWalkerDetailActivity extends BaseActivity implements View.OnClickListener {
    
    ActivityOwnerWalkerDetailBinding binding;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Bundle bundle;

    FragmentWalkerDetailProfile fragmentWalkerDetailProfile;
    FragmentWalkerDetailSchedule fragmentWalkerDetailSchedule;
    FragmentWalkerDetailPrice fragmentWalkerDetailPrice;
    FragmentWalkerDetailReview fragmentWalkerDetailReview;

    Intent intent;
    String walkerName, walkDogName, defaultWalkTime;
    String name, introduce, profileImgUrl, location;
    int priceThirtyMinutes, priceSixtyMinutes, priceAddLargeSize, priceAddOneDog, priceAddHoliday;
    int walkableTypeS, walkableTypeM, walkableTypeL;
    int add30minTimeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_walker_detail);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_owner_walker_detail);
        binding.setActivity(this);

        //intent 로 받아온 데이터
        intent = getIntent();
        walkerName = intent.getStringExtra("walkerName");   //도그워커 이름 데이터
        walkDogName = intent.getStringExtra("walkDogName"); //산책시킬 강아지 이름 데이터
        defaultWalkTime = intent.getStringExtra("defaultWalkTime"); //기본 산책시간 데이터
        add30minTimeCount = intent.getIntExtra("add30minTimeCount", 0); //추가한 산책시간 데이터

        fragmentManager = getSupportFragmentManager();

        //해당 도그워커의 정보 DB에서 불러오기
        selectWalkerInfoToDB();
        
        //북마크 유무 DB에서 불러오기
        selectBookmarkToDB(applicationClass.currentWalkerID, walkerName);

        //프래그먼트 객체 생성
        fragmentWalkerDetailProfile = new FragmentWalkerDetailProfile();
        fragmentWalkerDetailSchedule = new FragmentWalkerDetailSchedule();
        fragmentWalkerDetailPrice = new FragmentWalkerDetailPrice();
        fragmentWalkerDetailReview = new FragmentWalkerDetailReview();

        //클릭리스너 연결
        binding.buttonFragmentProfile.setOnClickListener(this);
        binding.buttonFragmentSchedule.setOnClickListener(this);
        binding.buttonFragmentReview.setOnClickListener(this);
        binding.buttonFragmentPrice.setOnClickListener(this);
    }

    //북마크 생성
    public void btnBookmarkOn(View view){
        //해당 유저가 도그워커를 북마크 한다 -> DB에 북마크 정보 저장
        insertBookmarkToDB(applicationClass.currentWalkerID, walkerName);
        //서버에 해당 도그워크를 북마크한 데이터가 있으면 색칠된 별표 이미지를 노출시킨다
        binding.imageButtonBookmarkOn.setVisibility(View.VISIBLE);
        binding.imageButtonBookmarkOff.setVisibility(View.GONE);
    }

    //북마크 제거
    public void btnBookmarkOff(View view){
        //해당 유저가 도그워커 북마크를 해제한다 -> DB에 북마크 정보 삭제
        deleteBookmarkToDB(applicationClass.currentWalkerID, walkerName);
        //서버에 해당 도그워크를 북마크한 데이터가 없으면 빈 별표 이미지를 노출시킨다
        binding.imageButtonBookmarkOn.setVisibility(View.GONE);
        binding.imageButtonBookmarkOff.setVisibility(View.VISIBLE);
    }
    
    //해당 유저가 도그워커를 북마크 했는지 유무 정보를 DB에서 불러오기
    public void selectBookmarkToDB(String bmk_user_id, String bmk_owner_id){
        
        Call<ResultDTO> call = retrofitApi.selectOwnerBookmark(bmk_user_id, bmk_owner_id);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                //서버로부터 받은 북마크 조회 결과 (반환값 : 0, 1)
                String bookmarkSelectResultDataFromServer = resultDTO.getResponceResult();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "북마크 정보 조회 성공 : " + resultDTO.getResponceResult());

                //북마크 이미지 노출
                if(bookmarkSelectResultDataFromServer.contentEquals("0")){
                    //서버에 해당 도그워크를 북마크한 데이터가 없으면 빈 별표 이미지를 노출시킨다
                    binding.imageButtonBookmarkOn.setVisibility(View.GONE);
                    binding.imageButtonBookmarkOff.setVisibility(View.VISIBLE);
                }else if(bookmarkSelectResultDataFromServer.contentEquals("1")){
                    //서버에 해당 도그워크를 북마크한 데이터가 있으면 색칠된 별표 이미지를 노출시킨다
                    binding.imageButtonBookmarkOn.setVisibility(View.VISIBLE);
                    binding.imageButtonBookmarkOff.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "북마크 정보 조회 실패");
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "에러 내용 : " + t.toString());
            }
        });
    }

    //해당 유저가 도그워커를 북마크 한다 -> DB에 북마크 정보 저장
    public void insertBookmarkToDB(String bmk_user_id, String bmk_owner_id){

        Call<ResultDTO> call = retrofitApi.insertOwnerBookmark(bmk_user_id, bmk_owner_id);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "북마크 정보 저장 성공 : " + resultDTO.getResponceResult());
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "북마크 정보 저장 실패");
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "에러 내용 : " + t.toString());
            }
        });
    }

    //해당 유저가 도그워커 북마크를 해제 -> DB에 북마크 정보 삭제
    public void deleteBookmarkToDB(String bmk_user_id, String bmk_owner_id){

        Call<ResultDTO> call = retrofitApi.deleteOwnerBookmark(bmk_user_id, bmk_owner_id);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "북마크 정보 삭제 성공 : " + resultDTO.getResponceResult());
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "북마크 정보 삭제 실패");
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "에러 내용 : " + t.toString());
            }
        });
    }

    //해당 도그워커의 정보 DB에서 불러오기
    public void selectWalkerInfoToDB(){

        Call<List<WalkerDTO>> call = retrofitApi.selectWalkerProfileData(walkerName);   //도그워커 아이디가 매개변수
        call.enqueue(new Callback<List<WalkerDTO>>() {
            @Override
            public void onResponse(Call<List<WalkerDTO>> call, Response<List<WalkerDTO>> response) {

                List<WalkerDTO> walkerlistDTOList = response.body();
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

                binding.textViewWalkerDetailWalkerName.setText(name);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.linearLayout_fragment_container_walker_detail, fragmentWalkerDetailProfile);
//        fragmentTransaction.addToBackStack(null);
                fragmentTransaction.disallowAddToBackStack();
                fragmentTransaction.commit();
                fragmentWalkerDetailProfile.setArguments(bundle);
            }

            @Override
            public void onFailure(Call<List<WalkerDTO>> call, Throwable t) {

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