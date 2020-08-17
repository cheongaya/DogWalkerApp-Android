package com.example.dogwalker.walker;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.dogwalker.LocationWebViewActivity;
import com.example.dogwalker.decorators.Decorator;
import com.example.dogwalker.decorators.EventDecorator;
import com.example.dogwalker.R;
import com.example.dogwalker.decorators.NoneDaysDecorator;
import com.example.dogwalker.decorators.NonedayDecorator;
import com.example.dogwalker.decorators.OneDayDecorator;
import com.example.dogwalker.decorators.SaturdayDecorator;
import com.example.dogwalker.decorators.SundayDecorator;
import com.example.dogwalker.retrofit2.response.NonServiceDateDTO;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerScheduleActivity extends WalkerBottomNavigation implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    MaterialCalendarView materialCalendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    Button btnSelectedDate, btnNoneDate;

    ArrayList<CalendarDay> nonServiceCalendarDates; //DB에서 받아온 서비스불가날짜 String List 를 CalendarDay List 에 담았다.    //클릭이벤트에서 사용하면됨

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_schedule);

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        //캘린더 셋팅
        calendarViewInitSetting();

        ((RadioGroup) findViewById(R.id.radioGroup_selection_type)).setOnCheckedChangeListener(this);
        btnSelectedDate = (Button)findViewById(R.id.button_selectedDate);
        btnNoneDate = (Button)findViewById(R.id.button_noneDate);

        //클릭 리스너 연결
        btnSelectedDate.setOnClickListener(this);
        btnNoneDate.setOnClickListener(this);

        materialCalendarView.addDecorator(new NonedayDecorator());

        //클릭 리스너 이벤트
        onClickCalendarView();

        //서비스 불가 날짜 DB 에서 데이터 불러와서 해당 날짜들에 addDecorator 백그라운드 회색 지정해주기
        loadNoneServiceDatesToDB();

//        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
//            @Override
//            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//                int Year = date.getYear();
//                int Month = date.getMonth() + 1;
//                int Day = date.getDay();
//
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "CalendarDay(date) : " + date);
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "Year : " + Year);
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "Month : " + Month);
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "Day : " + Day);
//
//                String shot_Day = Year + "," + Month + "," + Day;
//                Log.i("shot_Day test", shot_Day + "");
////                materialCalendarView.clearSelection();
//                Toast.makeText(getApplicationContext(), shot_Day , Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button_selectedDate :
                //선택한 날짜 데이터값 받아오기
                getSelectedDates();
                break;

            case R.id.button_noneDate :
                //서비스 불가능한 날짜 셋팅하기
                saveServiceNoneDatesToDB();
                break;
        }
    }

    //서비스 불가능한 날짜 DB에 저장하기
    public void saveServiceNoneDatesToDB(){

        List<CalendarDay> calendarDays = materialCalendarView.getSelectedDates();

        makeToast(calendarDays.toString());
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "선택한 날짜 : " + calendarDays.toString());

        //single : [CalendarDay{2020-7-12}]
        //multiple : [CalendarDay{2020-7-18}, CalendarDay{2020-7-25}]
        //range : [CalendarDay{2020-7-18}, CalendarDay{2020-7-20}, CalendarDay{2020-7-19}]

        String result = "";

        for(int i=0; i < calendarDays.size(); i++){

            CalendarDay calendarDay = calendarDays.get(i);

            int year = calendarDay.getYear();
            int month = calendarDay.getMonth()+1;   //0부터 시작함으로 +1 해준다
            int day = calendarDay.getDay();
            String fullDate = year+"-"+month+"-"+day;

            result += (fullDate+" / ");

            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "선택한 날짜 date : " + fullDate);

            //서비스 불가 날짜 DB에 저장
            applicationClass.insertData3ColumnToDB("non_service_date", "walker_id", applicationClass.currentWalkerID,
                    "date", fullDate, "time", "all");

        }

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "선택한 날짜 result : " + result);

    }

    //달력에서 선택한 날짜 데이터값 받아오기
    public void getSelectedDates(){

        List<CalendarDay> calendarDays = materialCalendarView.getSelectedDates();

        makeToast(calendarDays.toString());
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "선택한 날짜 : " + calendarDays.toString());

        //single : [CalendarDay{2020-7-12}]
        //multiple : [CalendarDay{2020-7-18}, CalendarDay{2020-7-25}]
        //range : [CalendarDay{2020-7-18}, CalendarDay{2020-7-20}, CalendarDay{2020-7-19}]

        String result = "";

        for(int i=0; i < calendarDays.size(); i++){

            CalendarDay calendarDay = calendarDays.get(i);

            int year = calendarDay.getYear();
            int month = calendarDay.getMonth()+1;   //0부터 시작함으로 +1 해준다
            int day = calendarDay.getDay();
            String fullDate = year+"-"+month+"-"+day;

            result += (fullDate+" / ");

            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "선택한 날짜 date : " + fullDate);

        }

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "선택한 날짜 result : " + result);
    }

    //캘린터 기본 셋팅 메소드
    public void calendarViewInitSetting(){

        Calendar calendarMax = Calendar.getInstance();
        calendarMax.add(Calendar.DAY_OF_MONTH, +60);
//        int year = calendar.get(Calendar.DAY_OF_YEAR);
//        int month = calendar.get(Calendar.DAY_OF_MONTH) +1;
//        int day = 30;
//        calendar.set(year, month, day);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.today())
//                .setMinimumDate(CalendarDay.from(2020,0,1)) //2020,0,1 -> 1월 1일
//                .setMaximumDate(CalendarDay.from(2020,9,31))
                .setMaximumDate(calendarMax)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();


        //달력에 토/일요일 색깔 표시
        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        materialCalendarView.setCurrentDate(new Date(System.currentTimeMillis())); //Data형을 파라미터로 받아서 캘린더를 띄울때 현재 날짜를 기본으로 띄울 수 있다.
        materialCalendarView.setDateSelected(new Date(System.currentTimeMillis()), false); // 파라미터로 넣어준 날짜에 대한 선택여부를 결정한다.
//        binding.calendarView.setSelectedDate(new Date(System.currentTimeMillis()));
//        binding.calendarView.removeDecorators(); //decorators를 제거하여 초기화 해주고
//        binding.calendarView.setSelectionMode(3); //날짜를 선택하는 여러개의 옵션을 제공한다. 3의 경우는 다중 선택이 가능하다.
//        binding.calendarView.addDecorator(new Decorator()); //캘린더를 내 입맞대로 꾸밀 수 있는 Decorator클래스를 추가한다.
    }

    //캘린더 클릭 이벤트 메소드
    public void onClickCalendarView(){

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                //선택(클릭)한 날짜 데이터
                int Year = date.getYear();
                int Month = date.getMonth() + 1;  //1월 = 0 부터 시작함으로 +1 해준다
                int Day = date.getDay();
                CalendarDay selectedDay = CalendarDay.from(Year, Month, Day);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "클릭날짜 : " + selectedDay);

                //DB에 저장해놓은 date 리스트
                for(int i=0; i<nonServiceCalendarDates.size(); i++){

                    int dbYear = nonServiceCalendarDates.get(i).getYear();
                    int dbMonth = nonServiceCalendarDates.get(i).getMonth() + 1;
                    int dbDay = nonServiceCalendarDates.get(i).getDay();
                    CalendarDay dbNoneServiceDay = CalendarDay.from(dbYear, dbMonth, dbDay);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "디비날짜 : " + dbNoneServiceDay);

                    if(dbNoneServiceDay.equals(selectedDay)){
                        makeToast("선택 불가 날짜");
                        break;
                    }else{
//                        makeToast("선택 가능 날짜 "+date.toString());
                    }
                }

//            //임의의 날짜 지정
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(2020,8,23);
//            CalendarDay calendarDay = CalendarDay.from(calendar);
//            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "지정날짜 : " + calendarDay);
//            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "받은날짜 : " + selectedDay);

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        //체크 초기화
        clearSelectionMenuClick();
        //체크 모드 변경
        switch (checkedId){
            case R.id.radioButton_single:
                materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
                break;
            case R.id.radioButton_multiple:
                materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
                break;
            case R.id.radioButton_range:
                materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
                break;
            case R.id.radioButton_none:
                materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
                break;
        }

    }

    //선택한 라디오버튼 체크를 clear 시킨다
    public void clearSelectionMenuClick(){
        materialCalendarView.clearSelection();
    }

    //서비스 불가 날짜 DB 에서 데이터 불러와서 해당 날짜들에 addDecorator 백그라운드 회색 지정해주기
    public void loadNoneServiceDatesToDB(){

        ArrayList<String> nonDatesList = new ArrayList<>();

        //DB에서 서비스 불가 날짜 데이터 불러오기
        Call<List<NonServiceDateDTO>> call = retrofitApi.selectWalkerNonServiceDates(applicationClass.currentWalkerID);
        call.enqueue(new Callback<List<NonServiceDateDTO>>() {
            @Override
            public void onResponse(Call<List<NonServiceDateDTO>> call, Response<List<NonServiceDateDTO>> response) {
                List<NonServiceDateDTO> nonServiceDateDTOList = response.body();

                for(int i=0; i<nonServiceDateDTOList.size(); i++){
                    String nonServiceDate = nonServiceDateDTOList.get(i).getDate();
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "DB에서 받아온 nonService 날짜 : " + nonServiceDate);
                    nonDatesList.add(nonServiceDate);
                }
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "반복문 종료");

                //방법[1] DB 에서 조회해 온 결과 date 들을 담은 배열 처리 (List -> Array 변환)
//                String[] resultArray = new String[nonDatesList.size()];
//                int size=0;
//                for(String temp : nonDatesList){
//                    resultArray[size++] = temp;
//                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "배열 결과 temp : "+temp);
//                }

                //방법[2] DB 에서 조회해 온 결과 date 들을 담은 배열 처리 (List -> Array 변환)
                String[] resultArray = nonDatesList.toArray(new String[nonDatesList.size()]);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "배열 결과 array : "+resultArray.toString());

                //위의 결과 배열에 해당하는 날짜들에 background 회색 처리
                new ApiSimulator(resultArray).executeOnExecutor(Executors.newSingleThreadExecutor());

            }

            @Override
            public void onFailure(Call<List<NonServiceDateDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "통신 실패");
            }
        });

        //예제
//        String[] resultArray = {"2020-08-18","2020-09-18","2020-09-20","2020-09-24"};
//        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "배열 결과 resultArray : "+resultArray.toString());
//        new ApiSimulator(resultArray).executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            nonServiceCalendarDates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                nonServiceCalendarDates.add(day);
                calendar.set(year,month-1,dayy);
            }

            return nonServiceCalendarDates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new NoneDaysDecorator(Color.RED, calendarDays,WalkerScheduleActivity.this));
        }
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_walker_schedule;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavWorker03;
    }
}