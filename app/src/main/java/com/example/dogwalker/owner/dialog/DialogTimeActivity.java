package com.example.dogwalker.owner.dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityDialogTimeBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class DialogTimeActivity extends BaseActivity implements View.OnClickListener{

    ActivityDialogTimeBinding timeDialogBinding;

    int Year, Month, Day; //선택한 날짜

    String defaultWalkTime; //선택한 기본 산책 시간
    int add30minTimeCount; //선택한 추가 산책 시간
    int checkingCount = 0;     //색칠해야하는 숫자

    private Button[] buttons = new Button[32];   //여러개의 버튼을 배열로 처리하기 위해 버튼에 대해 배열을 선언함
    private ArrayList<String> buttonArrayList;  //버튼을 클릭하면 각각 다르게 출력할 스트링을 넣어둘 리스트

    public static Calendar initCal; //선택한 시간 데이터를 가지고 있는 캘린더 객체

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dialog_time);

        timeDialogBinding = DataBindingUtil.setContentView(this, R.layout.activity_dialog_time);
        timeDialogBinding.setActivity(this);

        intent = getIntent();
        Year = intent.getIntExtra("selectedYear", 0);   //선택한 년
        Month = intent.getIntExtra("selectedMonth", 0); //선택한 월
        Day = intent.getIntExtra("selectedDay", 0);     //선택한 일
        defaultWalkTime = intent.getStringExtra("defaultWalkTime");  //선택한 기본 산책 시간
        add30minTimeCount = intent.getIntExtra("add30minTimeCount", 0); //선택한 추가 산책 시간

        initCal = Calendar.getInstance();    // 현재 시간을 받음.

        //선택한 기본 산책시간 색칠 카운팅 +
        if(defaultWalkTime.contentEquals("30")){
            checkingCount = 1;
        }else if(defaultWalkTime.contentEquals("60")){
            checkingCount = 2;
        }
        //선택한 추가 산책시간 색칠 카운팅 ++
        checkingCount += add30minTimeCount;

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "색칠갯수 checkingCount : " + checkingCount);

        //선택한 날짜 표시
        timeDialogBinding.textViewDialogDate.setText(Month+"월 "+Day+"일");
        //        setCancelable(false);   //다이얼로그 밖의 검은 영역 터치시에 화면 dismiss 되는 것을 막는 코드

        //TODO : 버튼 테스트중
        buttonArrayList = new ArrayList<String>();
        //버튼 배열에 time button 추가하기
        buttonListAddTimeButton();

        // 버튼들에 대한 클릭리스너 등록 및 각 버튼이 클릭되었을 때 출력될 메시지 생성(리스트)
        for(int i = 0 ; i < 32 ; i++) {
            // 버튼의 포지션(배열에서의 index)를 태그로 저장
            buttons[i].setTag(i);
            // 클릭 리스너 등록
            buttons[i].setOnClickListener(this);

            // 출력할 데이터 생성
            buttonArrayList.add("하이" + i + "입니다");
        }
    }

    //확인 버튼 클릭시
    public void onClickBookingTimeConfirm(View view){
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "클릭횟수 : " + 0);

        intent.putExtra("selectedDate", Year+"-"+ Month+"-"+Day);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "initCal.get(Calendar.HOUR_OF_DAY) : " + initCal.get(Calendar.HOUR_OF_DAY));
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "initCal.get(Calendar.MINUTE) : " + initCal.get(Calendar.MINUTE));
        intent.putExtra("selectedTimeCalendarHour", initCal.get(Calendar.HOUR_OF_DAY));
        intent.putExtra("selectedTimeCalendarMin", initCal.get(Calendar.MINUTE));
        intent.putExtra("selectedTimeCount", checkingCount);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {

        //TODO : 버튼 테스트 중
        //클릭된 뷰를 버튼으로 받아옴
        Button newButton = (Button) v;

        // 향상된 for문을 사용, 클릭된 버튼을 찾아냄
        for(Button tempButton : buttons) {
            // 클릭된 버튼을 찾았으면
            if(tempButton == newButton) {
                // 위에서 저장한 버튼의 포지션을 태그로 가져옴
                int position = (Integer)v.getTag();

//                v.setSelected(!v.isSelected());   //버튼 선택여부 반전시키기

                int limit = position + checkingCount;

                for(int i = position; i < 32; i++){

                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", " i: " + i);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", " limit: " + limit);

                    if(i == limit){
                        break;
                    }else{
                        buttons[i].setSelected(!buttons[i].isSelected());
                    }
                }

                //선택한 포지션값 에 따라 calendar 객체로 변환하기 -> intent 로 전 프래그먼트에 데이터 보내줄거다
                seletedTimeFormatDate(position);

//                makeToast(String.valueOf(v.isSelected()));

                // 태그로 가져온 포지션을 이용해 리스트에서 출력할 데이터를 꺼내서 토스트 메시지 출력
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", " 태그 포지션: " + buttonArrayList.get(position));
            }
        }

    }

    //선택한 포지션값에 따라 캘린더 객체로 변환하기
    public void seletedTimeFormatDate(int position){

//        int timeHOUR = Integer.parseInt(time.substring(0,2));
//        int timeMINUTE = Integer.parseInt(time.substring(2,4));

        initCal.set(Calendar.YEAR , Year);
        initCal.set(Calendar.MONTH , Month);
        initCal.set(Calendar.DAY_OF_MONTH , Day);

        initCal.set(Calendar.HOUR_OF_DAY , 07);
        initCal.set(Calendar.MINUTE , 00);
//        cal.set(Calendar.SECOND , 19);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "initCal : " + initCal.get(Calendar.HOUR_OF_DAY)+"시 "+initCal.get(Calendar.MINUTE)+"분");

        //선택한 날짜에 따른 계산하기
//        for (int i=0; i<=position; i++){
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "position : " + position);
        int addMin = position*30;
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "addMin : " + addMin);
        initCal.add(Calendar.MINUTE, addMin);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "addCal : " + initCal.get(Calendar.HOUR_OF_DAY)+"시 "+initCal.get(Calendar.MINUTE)+"분");

//        }

    }

    //버튼 배열에 시간 버튼 추가하기
    public void buttonListAddTimeButton(){

        buttons[0] = timeDialogBinding.buttonFragmentDialogTime700;
        buttons[1] = timeDialogBinding.buttonFragmentDialogTime730;
        buttons[2] = timeDialogBinding.buttonFragmentDialogTime800;
        buttons[3] = timeDialogBinding.buttonFragmentDialogTime830;
        buttons[4] = timeDialogBinding.buttonFragmentDialogTime900;
        buttons[5] = timeDialogBinding.buttonFragmentDialogTime930;
        buttons[6] = timeDialogBinding.buttonFragmentDialogTime1000;
        buttons[7] = timeDialogBinding.buttonFragmentDialogTime1030;
        buttons[8] = timeDialogBinding.buttonFragmentDialogTime1100;
        buttons[9] = timeDialogBinding.buttonFragmentDialogTime1130;
        buttons[10] = timeDialogBinding.buttonFragmentDialogTime1200;
        buttons[11] = timeDialogBinding.buttonFragmentDialogTime1230;
        buttons[12] = timeDialogBinding.buttonFragmentDialogTime1300;
        buttons[13] = timeDialogBinding.buttonFragmentDialogTime1330;
        buttons[14] = timeDialogBinding.buttonFragmentDialogTime1400;
        buttons[15] = timeDialogBinding.buttonFragmentDialogTime1430;
        buttons[16] = timeDialogBinding.buttonFragmentDialogTime1500;
        buttons[17] = timeDialogBinding.buttonFragmentDialogTime1530;
        buttons[18] = timeDialogBinding.buttonFragmentDialogTime1600;
        buttons[19] = timeDialogBinding.buttonFragmentDialogTime1630;
        buttons[20] = timeDialogBinding.buttonFragmentDialogTime1700;
        buttons[21] = timeDialogBinding.buttonFragmentDialogTime1730;
        buttons[22] = timeDialogBinding.buttonFragmentDialogTime1800;
        buttons[23] = timeDialogBinding.buttonFragmentDialogTime1830;
        buttons[24] = timeDialogBinding.buttonFragmentDialogTime1900;
        buttons[25] = timeDialogBinding.buttonFragmentDialogTime1930;
        buttons[26] = timeDialogBinding.buttonFragmentDialogTime2000;
        buttons[27] = timeDialogBinding.buttonFragmentDialogTime2030;
        buttons[28] = timeDialogBinding.buttonFragmentDialogTime2100;
        buttons[29] = timeDialogBinding.buttonFragmentDialogTime2130;
        buttons[30] = timeDialogBinding.buttonFragmentDialogTime2200;
        buttons[31] = timeDialogBinding.buttonFragmentDialogTime2230;

    }
}