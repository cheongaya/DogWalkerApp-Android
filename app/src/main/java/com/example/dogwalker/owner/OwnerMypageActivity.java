package com.example.dogwalker.owner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerMypageActivity extends OwnerBottomNavigation implements View.OnClickListener {

    private static final int OWNER_ADD_DOG = 2001;

    ImageView imgOwnerProgile, imgDogDataNull;
    TextView tvOwnerName, tvMyDogName, tvMyDogSex, tvMyDogAge, tvMyDogType, tvMyDogWeight, tvMyDogKine, tvMyDogNumber;
    RecyclerView recyclerViewMyDog;
    FloatingActionButton btnFloatAddMyDog;
    LinearLayout linearDogDataNull;
    ConstraintLayout constraintDogDataIs;
    ImageButton btnMyDogDataDelete;

    DogListAdapter dogListAdapter;
    List<DogDTO> dogDTOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_mypage);

        imgOwnerProgile = (ImageView)findViewById(R.id.imageView_profileImg);
        imgDogDataNull = (ImageView)findViewById(R.id.imageView_mydog_data_null);
        linearDogDataNull = (LinearLayout)findViewById(R.id.linearLayout_myDog_data_null);
        constraintDogDataIs = (ConstraintLayout)findViewById(R.id.constraintLayout_myDog_info);
        tvOwnerName = (TextView)findViewById(R.id.textView_name);
        tvMyDogName = (TextView)findViewById(R.id.textView_mydog_name);
        tvMyDogSex = (TextView)findViewById(R.id.textView_mydog_sex);
        tvMyDogAge = (TextView)findViewById(R.id.textView_mydog_age);
        tvMyDogType = (TextView)findViewById(R.id.textView_mydog_type);
        tvMyDogWeight = (TextView)findViewById(R.id.textView_mydog_weight);
        tvMyDogKine = (TextView)findViewById(R.id.textView_mydog_kind);
        tvMyDogNumber = (TextView)findViewById(R.id.textView_mydog_number);
        btnFloatAddMyDog = (FloatingActionButton)findViewById(R.id.button_floating_add_mydog);
        btnMyDogDataDelete = (ImageButton)findViewById(R.id.imageView_mydog_delete);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        //DB에서 강아지 데이터 불러오기
        loadMyDogDataToDB();

        //클릭 리스너 이벤트
        btnFloatAddMyDog.setOnClickListener(this);
        btnMyDogDataDelete.setOnClickListener(this);

        //DB에 저장된 데이터 조회해서 setText() 해주기
//        applicationClass.loadData1ColumnToDB("user_owner", "name", "id", applicationClass.currentWalkerID, tvOwnerName);

        //아이템 클릭 리스너
        dogListAdapter.setOnItemClickListener(new DogListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //클릭했을때 처리
//                makeToast("aaa");
                myDogDataSetText(position);
            }
        });
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){
        recyclerViewMyDog =  (RecyclerView)findViewById(R.id.recyclerView_myDog);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMyDog.setLayoutManager(linearLayoutManager); //?? 주석??
        recyclerViewMyDog.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        dogListAdapter = new DogListAdapter();
        recyclerViewMyDog.setAdapter(dogListAdapter);
    }

    //DB에서 강아지 데이터 불러오기
    public void loadMyDogDataToDB(){
        Call<List<DogDTO>> call = retrofitApi.selectDogData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<List<DogDTO>>() {
            @Override
            public void onResponse(Call<List<DogDTO>> call, Response<List<DogDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "강아지 데이터 조회 성공");
                dogDTOList = response.body();

                //강아지 데이터 setText 해주기 (position 번째)
                if(dogDTOList.size() > 0){

                    //데이터 NULL 아니면
                    imgDogDataNull.setVisibility(View.GONE); //안보이게
                    linearDogDataNull.setVisibility(View.GONE); //안보이게
                    recyclerViewMyDog.setVisibility(View.VISIBLE); //보이게
                    constraintDogDataIs.setVisibility(View.VISIBLE); //보이게

                    ArrayList<DogDTO> arrayList = new ArrayList<DogDTO>();
                    arrayList.addAll(dogDTOList);

                    dogListAdapter.setDogDTOArrayList(arrayList);
                    dogListAdapter.notifyDataSetChanged();

                    myDogDataSetText(0);

                }else{
                    //데이터 NULL 이면
                    imgDogDataNull.setVisibility(View.VISIBLE); //보이게
                    linearDogDataNull.setVisibility(View.VISIBLE); //보이게
                    recyclerViewMyDog.setVisibility(View.GONE); //안보이게
                    constraintDogDataIs.setVisibility(View.GONE); //안보이게
                }

            }

            @Override
            public void onFailure(Call<List<DogDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "강아지 데이터 조회 실패");

            }
        });
    }

    //강아지 데이터 setText 해주기 (position 번째)
    public void myDogDataSetText(int position){
        //첫번째 강아지 data 정보 setText 해주기
        tvMyDogName.setText(dogDTOList.get(position).getName());
        if(dogDTOList.get(position).getSex().contentEquals("male")){
            tvMyDogSex.setText("남");
        }else{
            tvMyDogSex.setText("여");
        }
        //TODO: 개월수 날짜계산 해야함
        tvMyDogAge.setText(dogDTOList.get(position).getBirthday_month()+"월생");
        if(dogDTOList.get(position).getSize().contentEquals("S")){
            tvMyDogType.setText("소형");
        }else if(dogDTOList.get(position).getSize().contentEquals("M")){
            tvMyDogType.setText("중형");
        }else{
            tvMyDogType.setText("대형");
        }
        tvMyDogKine.setText(dogDTOList.get(position).getKind());
        tvMyDogWeight.setText(dogDTOList.get(position).getWeight()+" kg");
        tvMyDogNumber.setText(dogDTOList.get(position).getNumber());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.button_floating_add_mydog :
                //강아지 추가하기
                Intent intentAddDog = new Intent(this, OwnerAddDogActivity.class);
                startActivityForResult(intentAddDog, OWNER_ADD_DOG);
                break;

            case R.id.imageView_mydog_delete:
                //강아지 삭제 여부 묻는 다이얼로그 창 띄우기
                deleteDogDataDialog();
                break;

            default:
                break;
        }
    }

    //펫정보 삭제 다이얼로그
    public void deleteDogDataDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("펫 삭제")
                .setMessage("펫 정보를 삭제하시겠습니까?");

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DB 에서 강아지 데이터 삭제
                deleteMyDogDataToDB();
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //DB 에서 강아지 데이터 삭제하기
    public void deleteMyDogDataToDB(){

        String dogName = tvMyDogName.getText().toString();
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "삭제할 강아지 이름 : " + dogName);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "삭제할 로그인유저 이름 : " + applicationClass.currentWalkerID);

        Call<ResultDTO> call = retrofitApi.deleteDogData(applicationClass.currentWalkerID, dogName);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();

                if(resultDataStr.contentEquals("ok")){
                    //다시 디비에서 강아지데이터 리로드 시키기
                    loadMyDogDataToDB();
                    makeToast("펫 삭제 완료");
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case OWNER_ADD_DOG :
                //DB 에서 강아지 데이터 다시 로드하기
                loadMyDogDataToDB();
                break;

            default:
                break;
        }
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_owner_mypage;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavOwner04;
    }


}