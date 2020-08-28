package com.example.dogwalker.owner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.dogwalker.R;
import com.example.dogwalker.databinding.FragmentWalkDialogBinding;
import com.example.dogwalker.owner.OwnerBookingActivity;
import com.example.dogwalker.owner.OwnerWalkerlistActivity;

public class FragmentWalkDialog extends DialogFragment {

    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    FragmentWalkDialogBinding walkDialogBinding;
    int Add30minTimeCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        walkDialogBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_walk_dialog, container, false);
        walkDialogBinding.setFragment(this);
        View view = walkDialogBinding.getRoot();

        //numberpicker 설정
        walkDialogBinding.numberPickerAdd30minWalkTime.setEnabled(false);   //처음에 false / false 넘버피커 사용 불가
        walkDialogBinding.numberPickerAdd30minWalkTime.setMaxValue(2); //최대 값 설정
        walkDialogBinding.numberPickerAdd30minWalkTime.setMinValue(0); //최소 값 설정
        walkDialogBinding.numberPickerAdd30minWalkTime.setValue(0); //현재 값 설정 (처음 보여지는 값)
        walkDialogBinding.numberPickerAdd30minWalkTime.setWrapSelectorWheel(false);
        walkDialogBinding.numberPickerAdd30minWalkTime.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); //데이터 선택시 editText 방지
        walkDialogBinding.numberPickerAdd30minWalkTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                makeToast(newVal+"");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "넘버피커 newVal : " + newVal);
                Add30minTimeCount = newVal;
            }
        });

        return view;
    }

    //기본 30분 산책 클릭시
    public void onClick30minWalkTime(View view){

        if(view.isSelected() == false && walkDialogBinding.buttonFragmentDialogWalk60min.isSelected() == true){
            walkDialogBinding.buttonFragmentDialogWalk60min.setSelected(false);
        }

        view.setSelected(!view.isSelected());   //버튼 선택여부 반전시키기
        walkDialogBinding.numberPickerAdd30minWalkTime.setEnabled(false);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "30분 산책 선택시 시간 추가 불가 : ");
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "30분 선택 : "+ view.isSelected() + " / 60분 선택 : "+ walkDialogBinding.buttonFragmentDialogWalk60min.isSelected());

    }

    //기본 60분 산책 클릭시
    public void onClick60minWalkTime(View view){

        if(view.isSelected() == false && walkDialogBinding.buttonFragmentDialogWalk30min.isSelected() == true){
            walkDialogBinding.buttonFragmentDialogWalk30min.setSelected(false);
        }

        view.setSelected(!view.isSelected());   //버튼 선택여부 반전시키기
        walkDialogBinding.numberPickerAdd30minWalkTime.setEnabled(true);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "30분 산책 선택시 시간 추가 가능 : ");
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "30분 선택 : "+ walkDialogBinding.buttonFragmentDialogWalk30min.isSelected() + " / 60분 선택 : "+ view.isSelected());
    }

    //다음 버튼 클릭시 도그워커 리스트 출력 화면으로 전환
    public void onClickWalkTimeSelectedNext(View view){

            //산책시간 선택 안했을 시
            if(walkDialogBinding.buttonFragmentDialogWalk30min.isSelected() == false && walkDialogBinding.buttonFragmentDialogWalk60min.isSelected() == false){
                makeToast("산책 시간을 선택해주세요");

            //산책시간 선택했을 시
            }else{
                Intent intentBookingPage = new Intent(getContext(), OwnerWalkerlistActivity.class);
                //데이터 전달
                if(walkDialogBinding.buttonFragmentDialogWalk30min.isSelected() == true){
                    //기본 선택시간 30분 선택시
                    intentBookingPage.putExtra("defaultWalkTime", "30");    //기본 선택시간
                    intentBookingPage.putExtra("add30minTimeCount", 0); //추가한 산책시간
                }else{
                    //기본 선택시간 60분 선택시
                    intentBookingPage.putExtra("defaultWalkTime", "60");    //기본 선택시간
                    intentBookingPage.putExtra("add30minTimeCount", Add30minTimeCount); //추가한 산책시간
                }

                startActivity(intentBookingPage);
                //다이얼로그 창 종료
                dismiss();
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
