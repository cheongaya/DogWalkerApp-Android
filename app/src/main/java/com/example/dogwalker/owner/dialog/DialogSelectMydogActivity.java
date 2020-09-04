package com.example.dogwalker.owner.dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.data.DogDTO;
import com.example.dogwalker.databinding.ActivityDialogSelectMydogBinding;
import com.example.dogwalker.owner.DogListAdapter;
import com.example.dogwalker.owner.fragment.FragmentWalkDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogSelectMydogActivity extends BaseActivity{

    ActivityDialogSelectMydogBinding selectMydogBinding;

    DogListAdapter dogListAdapter;
    List<DogDTO> dogDTOList;
    String selectedDog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dialog_select_mydog);

        //데이터 바인딩 연결
        selectMydogBinding = DataBindingUtil.setContentView(this, R.layout.activity_dialog_select_mydog);
        selectMydogBinding.setActivity(this);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();
        //DB에서 강아지 데이터 불러오기
        loadMyDogDataToDB();

        //아이템 클릭 리스너
        dogListAdapter.setOnItemClickListener(new DogListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                String dogName = dogListAdapter.getDogDTOArrayList().get(position).getName();
                selectMydogBinding.textViewDialogSelectedMydog.setText(dogName);
                //클릭했을때 처리
//                makeToast("aaa");
                selectedDog = dogName;
            }
        });
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        selectMydogBinding.recyclerViewFragmentMydog.setLayoutManager(linearLayoutManager); //?? 주석??
        selectMydogBinding.recyclerViewFragmentMydog.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        dogListAdapter = new DogListAdapter(this);
        selectMydogBinding.recyclerViewFragmentMydog.setAdapter(dogListAdapter);
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
                    ArrayList<DogDTO> arrayList = new ArrayList<DogDTO>();
                    arrayList.addAll(dogDTOList);

                    dogListAdapter.setDogDTOArrayList(arrayList);
                    dogListAdapter.notifyDataSetChanged();

                }else{
                    //데이터 NULL 이면
                    makeToast("반려견 정보를 등록해주세요");
                }

            }

            @Override
            public void onFailure(Call<List<DogDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "강아지 데이터 조회 실패");

            }
        });
    }

    //다음 버튼 클릭시 이벤트
    public void onClickDogSelectedNext(View view){

        if(selectedDog != null){

            //프래그먼트 다이얼로그 띄우기
            Bundle bundle = new Bundle();
            bundle.putString("selectedDog", selectedDog);
            FragmentWalkDialog fragmentWalkDialog = new FragmentWalkDialog();
            fragmentWalkDialog.setArguments(bundle);
            fragmentWalkDialog.show(getSupportFragmentManager(), "fragmentWalkDialog");
            finish();

        }else{
            makeToast("산책시킬 반려견을 선택해주세요");
        }

    }
}