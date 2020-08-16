package com.example.dogwalker.walker;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.dogwalker.decorators.EventDecorator;
import com.example.dogwalker.R;
import com.example.dogwalker.decorators.OneDayDecorator;
import com.example.dogwalker.decorators.SaturdayDecorator;
import com.example.dogwalker.decorators.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class WalkerScheduleActivity extends WalkerBottomNavigation {

    MaterialCalendarView materialCalendarView;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_schedule);

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        //캘린더 셋팅
        calendarViewSetting();

        //달력에 토/일요일 색깔 표시
        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        String[] result = {"2020,03,18","2020,04,18","2020,05,18","2020,06,18"};

        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "CalendarDay(date) : " + date);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "Year : " + Year);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "Month : " + Month);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "Day : " + Day);

                String shot_Day = Year + "," + Month + "," + Day;

                Log.i("shot_Day test", shot_Day + "");

//                materialCalendarView.setDateSelected(CalendarDay.from(2020,8,19), false);
                materialCalendarView.clearSelection();

                Toast.makeText(getApplicationContext(), shot_Day , Toast.LENGTH_SHORT).show();
            }
        });


    }

    //캘린터 기본 셋팅 메소드
    public void calendarViewSetting(){
//        binding.calendarView.state().edit()
//                .setFirstDayOfWeek(Calendar.SUNDAY)
//                .setMinimumDate(CalendarDay.from(2020,1,1))
//                .setMinimumDate(CalendarDay.from(2023,12,31))
//                .setCalendarDisplayMode(CalendarMode.MONTHS)
//                .commit();

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

            }
        });
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
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year,month-1,dayy);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }


            materialCalendarView.addDecorator(new EventDecorator(Color.GREEN, calendarDays,WalkerScheduleActivity.this));
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