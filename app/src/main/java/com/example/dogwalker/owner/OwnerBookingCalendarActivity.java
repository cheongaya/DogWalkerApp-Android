package com.example.dogwalker.owner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityOwnerBookingCalendarBinding;
import com.example.dogwalker.decorators.BookingDaysDecorator;
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerBookingCalendarActivity extends BaseActivity {

    ActivityOwnerBookingCalendarBinding binding;

    BookingServiceOwnerAdapter bookingServiceOwnerAdapter;
    ArrayList<CalendarDay> bookingServiceCalendarDates; //DB에서 받아온 예약 날짜 String List 를 CalendarDay List 에 담는다


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_booking_calendar);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_owner_booking_calendar);
        binding.setActivity(this);

        //캘린더 셋팅
        calendarViewInitSetting();

        //캘린더 예약 내역 DB 에서 데이터 불러와서 해당 날짜들에 addDecorator 점 표시 지정해주기
        loadBookingServiceDatesToDB();

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        //캘린더 클릭 리스너 이벤트
        onClickCalendarView();
    }

    //캘린터 기본 셋팅 메소드
    public void calendarViewInitSetting(){

        Calendar calendarMax = Calendar.getInstance();
        calendarMax.add(Calendar.YEAR, +1);

        binding.calendarViewOwnerBooking.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2000, 0, 1))
                .setMaximumDate(calendarMax)    //1년 뒤
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        binding.calendarViewOwnerBooking.setCurrentDate(new Date(System.currentTimeMillis())); //Data형을 파라미터로 받아서 캘린더를 띄울때 현재 날짜를 기본으로 띄울 수 있다.
        binding.calendarViewOwnerBooking.setDateSelected(new Date(System.currentTimeMillis()), false); // 파라미터로 넣어준 날짜에 대한 선택여부를 결정한다.
//        binding.calendarView.setSelectedDate(new Date(System.currentTimeMillis()));
//        binding.calendarView.removeDecorators(); //decorators를 제거하여 초기화 해주고
    }
    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerViewBookinglistOwner.setLayoutManager(linearLayoutManager); //?? 주석??
        bookingServiceOwnerAdapter = new BookingServiceOwnerAdapter(this);
        binding.recyclerViewBookinglistOwner.setAdapter(bookingServiceOwnerAdapter);
    }

    //예약 내역 DB 에서 데이터 불러와서 해당 날짜들에 addDecorator 점 표시 지정해주기
    public void loadBookingServiceDatesToDB(){

        ArrayList<String> bookingDatesList = new ArrayList<>();

        Call<List<BookingServiceDTO>> call = retrofitApi.selectOwnerIngBookingServiceData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<List<BookingServiceDTO>>() {
            @Override
            public void onResponse(Call<List<BookingServiceDTO>> call, Response<List<BookingServiceDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "예약리스트 데이터 조회 성공");
                List<BookingServiceDTO> bookingServiceDTOList = response.body();    //List
                /**
                 * 달력에 점 표시하기 위한 코드
                 */
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "리스트 사이즈 : " + bookingServiceDTOList.size());

                for(int i=0; i<bookingServiceDTOList.size(); i++){
                    String bookingServiceDate = bookingServiceDTOList.get(i).getWalk_date();
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "DB에서 받아온 예약 날짜 : " + bookingServiceDate);
                    bookingDatesList.add(bookingServiceDate);
                }
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "반복문 종료");

                //DB 에서 조회해 온 결과 date 들을 담은 배열 처리 (List -> Array 변환)
                String[] resultArray = bookingDatesList.toArray(new String[bookingDatesList.size()]);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "배열 결과 array : "+resultArray.toString());

                //위의 결과 배열에 해당하는 날짜들에 background 회색 처리
                new OwnerBookingCalendarActivity.ApiSimulator(resultArray).executeOnExecutor(Executors.newSingleThreadExecutor());

                /**
                 * 리사이클러뷰 관련 코드
                 */
                ArrayList<BookingServiceDTO> bookingServiceDTOArrayList = new ArrayList<>();
                bookingServiceDTOArrayList.addAll(bookingServiceDTOList);
                bookingServiceOwnerAdapter.setBookingServiceDTOArrayList(bookingServiceDTOArrayList);
                bookingServiceOwnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<BookingServiceDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "예약리스트 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());

            }
        });

    }
    //캘린더 클릭 이벤트 메소드
    public void onClickCalendarView(){

        binding.calendarViewOwnerBooking.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                //선택(클릭)한 날짜 데이터
                int Year = date.getYear();
                int Month = date.getMonth() + 1;  //1월 = 0 부터 시작함으로 +1 해준다
                int Day = date.getDay();
                CalendarDay selectedDay = CalendarDay.from(Year, Month, Day);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "클릭날짜 : " + selectedDay);

                //DB에 저장해놓은 date 리스트
                for(int i=0; i<bookingServiceCalendarDates.size(); i++){

                    int dbYear = bookingServiceCalendarDates.get(i).getYear();
                    int dbMonth = bookingServiceCalendarDates.get(i).getMonth() + 1;
                    int dbDay = bookingServiceCalendarDates.get(i).getDay();
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[1]디비 dbYear : " + dbYear);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[1]디비 dbMonth : " + dbMonth);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[1]디비 dbDay : " + dbDay);
                    CalendarDay dbNoneServiceDay = CalendarDay.from(dbYear, dbMonth, dbDay);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "디비날짜 : " + dbNoneServiceDay);

                }

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
            bookingServiceCalendarDates = new ArrayList<>();
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[2]디비 bookingServiceCalendarDates : " + bookingServiceCalendarDates.toString());

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환

            for(int i = 0 ; i < Time_Result.length ; i ++){

//                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[2]디비 year : " + year);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[2]디비 month : " + month);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[2]디비 dayy : " + dayy);

                CalendarDay day = CalendarDay.from(year, month-1, dayy);

                bookingServiceCalendarDates.add(day);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[2]디비 bookingServiceCalendarDates : " + bookingServiceCalendarDates.get(i).toString());
//                calendar.set(year,month,dayy);
            }

            return bookingServiceCalendarDates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            binding.calendarViewOwnerBooking.addDecorator(new BookingDaysDecorator(Color.RED, calendarDays,OwnerBookingCalendarActivity.this));
        }
    }


}