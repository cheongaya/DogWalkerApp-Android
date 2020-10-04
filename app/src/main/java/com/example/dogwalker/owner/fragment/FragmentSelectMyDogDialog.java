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
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.DogDTO;
import com.example.dogwalker.databinding.FragmentSelectMydogDialogBinding;
import com.example.dogwalker.owner.SelectDogListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSelectMyDogDialog extends DialogFragment {

    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    FragmentSelectMydogDialogBinding selectMydogDialogBinding;

    SelectDogListAdapter selectDogListAdapter;
    List<DogDTO> dogDTOList;
    String selectedDog = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        selectMydogDialogBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_mydog_dialog, container, false);
        selectMydogDialogBinding.setFragment(this);
        View view = selectMydogDialogBinding.getRoot();

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();
        //DB에서 강아지 데이터 불러오기
        loadMyDogDataToDB();

        //아이템 클릭 리스너
        selectDogListAdapter.setOnItemClickListener(new SelectDogListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                String dogName = selectDogListAdapter.getDogDTOArrayList().get(position).getName();
                selectMydogDialogBinding.textViewDialogSelectedMydog.setText(dogName);
                //클릭했을때 처리
//                makeToast("aaa");
                selectedDog = dogName;
            }
        });

        return view;
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        selectMydogDialogBinding.recyclerViewFragmentMydog.setLayoutManager(linearLayoutManager); //?? 주석??
        selectMydogDialogBinding.recyclerViewFragmentMydog.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

//        recyclerView.setHasFixedSize(true); ??

        selectDogListAdapter = new SelectDogListAdapter(getContext());
        selectMydogDialogBinding.recyclerViewFragmentMydog.setAdapter(selectDogListAdapter);
    }

    //DB에서 강아지 데이터 불러오기
    public void loadMyDogDataToDB(){
        Call<List<DogDTO>> call = BaseActivity.retrofitApi.selectDogData(ApplicationClass.currentWalkerID);
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

                    selectDogListAdapter.setDogDTOArrayList(arrayList);
                    selectDogListAdapter.notifyDataSetChanged();

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

    //다음 버튼 클릭시 도그워커 리스트 출력 화면으로 전환
    public void onClickDogSelectedNext(View view){

        if(selectedDog != null){

            //프래그먼트 다이얼로그 띄우기
            Bundle bundle = new Bundle();
            bundle.putString("selectedDog", selectedDog);
            FragmentWalkDialog fragmentWalkDialog = new FragmentWalkDialog();
            fragmentWalkDialog.setArguments(bundle);
            fragmentWalkDialog.show(getFragmentManager(), "fragmentWalkDialog");
            //다이얼로그 창 종료
            dismiss();

        }else{
            makeToast("산책시킬 반려견을 선택해주세요");
        }

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
