package com.example.dogwalker.owner.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.decorators.NoneDaysCancelDecorator;
import com.example.dogwalker.decorators.NoneDaysDecorator;
import com.example.dogwalker.decorators.NoneDaysDecoratorFragment;
import com.example.dogwalker.decorators.SaturdayDecorator;
import com.example.dogwalker.decorators.SundayDecorator;
import com.example.dogwalker.owner.dialog.DialogPaymentActivity;
import com.example.dogwalker.owner.dialog.DialogTimeActivity;
import com.example.dogwalker.retrofit2.response.NonServiceDateDTO;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.walker.WalkerScheduleActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentWalkerDetailSchedule extends FragmentBase implements View.OnClickListener{

    private static final int BOOKING_TIME_SELECTED = 5001;
    private static final int BOOKING_PAYMENT_SELECTED = 5002;

    String walkerName;  //bundle 을 통해 전달받은 도그워커 이름 데이터
    String walkDogName; //bundle 을 통해 전달받은 산책시킬 강아지 이름 데이터
    String defaultWalkTime; //bundle 을 통해 전달받은 선택한 기본 산책 시간 데이터
    int add30minTimeCount;  //bundle 을 통해 전달받은 선택한 추가 산책 시간 데이터

    String selectedDate; //선택한 예약 날짜
    int selectedTimeCalendarHour, selectedTimeCalendarMin, totalWalkTime;   //선택한 예약 시, 분, 예약한 총 서비스시간
    int totalPayPrice = 0;  //최종 결제할 금액

    int priceThirtyMinutes, priceSixtyMinutes, priceAddLargeSize, priceAddOneDog, priceAddHoliday;  //DB 에서 불러온 도그워커 정보(가격표)
    int walkableTypeS, walkableTypeM, walkableTypeL;   //DB 에서 불러온 도그워커 정보(산책가능유형)

    MaterialCalendarView calendarViewBooking;
    Button btnBooking;
    TextView tvBookingDate, tvBookingTime, tvBookingWalkTime;

    ArrayList<CalendarDay> nonServiceCalendarDates; //DB에서 받아온 서비스불가날짜 String List 를 CalendarDay List 에 담았다.    //클릭이벤트에서 사용하면됨

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_walker_detail_schedule, container, false);

        if(getArguments() != null){
            //bundle 을 통해 전달받은 데이터
            walkerName = getArguments().getString("walkerName");
            walkDogName = getArguments().getString("walkDogName");
            defaultWalkTime = getArguments().getString("defaultWalkTime");
            add30minTimeCount = getArguments().getInt("add30minTimeCount");
//            makeToast(walkerName);
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "//[0] walkerName : " + walkerName);
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "//[0] defaultWalkTime : " + defaultWalkTime);
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "//[0] add30minTimeCount : " + add30minTimeCount);

            priceThirtyMinutes = getArguments().getInt("priceThirtyMinutes");
            priceSixtyMinutes = getArguments().getInt("priceSixtyMinutes");
            priceAddLargeSize = getArguments().getInt("priceAddLargeSize");
            priceAddOneDog = getArguments().getInt("priceAddOneDog");
            priceAddHoliday = getArguments().getInt("priceAddHoliday");
            walkableTypeS = getArguments().getInt("walkableTypeS");
            walkableTypeM = getArguments().getInt("walkableTypeM");
            walkableTypeL = getArguments().getInt("walkableTypeL");
        }

        //string -> int 형변환
        int defaultWalkTimeInt = Integer.parseInt(defaultWalkTime);

        //최종 결제 금액 계산
        if(defaultWalkTimeInt == 30){
            //기본시간 30분이면
            totalPayPrice = priceThirtyMinutes;
        }else{
            //기본시간 60분이면
            int add30minTimePrice = add30minTimeCount*priceSixtyMinutes;
            totalPayPrice = priceThirtyMinutes*2 + add30minTimePrice;
        }

                //id 연결
        calendarViewBooking = (MaterialCalendarView) v.findViewById(R.id.calendarView_booking);
        btnBooking = (Button) v.findViewById(R.id.button_booking_ok);
        tvBookingDate = (TextView)v.findViewById(R.id.textView_booking_date);
        tvBookingTime = (TextView)v.findViewById(R.id.textView_booking_time);
        tvBookingWalkTime = (TextView)v.findViewById(R.id.textView_booking_walk_time);
        //클릭리스너 연결
        btnBooking.setOnClickListener(this);

        //캘린더 셋팅
        calendarViewInitSetting();
        //서비스 불가 날짜 DB 에서 데이터 불러와서 해당 날짜들에 addDecorator 백그라운드 회색 지정해주기
        loadNoneServiceDatesToDB();
        //클릭 리스너 이벤트
        onClickCalendarView();

//        return inflater.inflate(R.layout.fragment_walker_detail_schedule, container, false);
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //캘린터 기본 셋팅 메소드
    public void calendarViewInitSetting(){

        Calendar calendarMax = Calendar.getInstance();
        calendarMax.add(Calendar.DAY_OF_MONTH, +60);
//        int year = calendar.get(Calendar.DAY_OF_YEAR);
//        int month = calendar.get(Calendar.DAY_OF_MONTH) +1;
//        int day = 30;
//        calendar.set(year, month, day);

        calendarViewBooking.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.today())
//                .setMinimumDate(CalendarDay.from(2020,0,1)) //2020,0,1 -> 1월 1일
//                .setMaximumDate(CalendarDay.from(2020,9,31))
                .setMaximumDate(calendarMax)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendarViewBooking.setCurrentDate(new Date(System.currentTimeMillis())); //Data형을 파라미터로 받아서 캘린더를 띄울때 현재 날짜를 기본으로 띄울 수 있다.
        calendarViewBooking.setDateSelected(new Date(System.currentTimeMillis()), false); // 파라미터로 넣어준 날짜에 대한 선택여부를 결정한다.
//        binding.calendarView.setSelectedDate(new Date(System.currentTimeMillis()));
//        binding.calendarView.removeDecorators(); //decorators를 제거하여 초기화 해주고
//        binding.calendarView.setSelectionMode(3); //날짜를 선택하는 여러개의 옵션을 제공한다. 3의 경우는 다중 선택이 가능하다.
//        binding.calendarView.addDecorator(new Decorator()); //캘린더를 내 입맞대로 꾸밀 수 있는 Decorator클래스를 추가한다.
    }

    //서비스 불가 날짜 DB 에서 데이터 불러와서 해당 날짜들에 addDecorator 백그라운드 회색 지정해주기
    public void loadNoneServiceDatesToDB(){

        ArrayList<String> nonDatesList = new ArrayList<>();

        //DB에서 서비스 불가 날짜 데이터 불러오기
        Call<List<NonServiceDateDTO>> call = BaseActivity.retrofitApi.selectWalkerNonServiceDates(walkerName);
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

                //DB 에서 조회해 온 결과 date 들을 담은 배열 처리 (List -> Array 변환)
                String[] resultArray = nonDatesList.toArray(new String[nonDatesList.size()]);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "배열 결과 array : "+resultArray.toString());

                //위의 결과 배열에 해당하는 날짜들에 background 회색 처리
                new FragmentWalkerDetailSchedule.ApiSimulator(resultArray).executeOnExecutor(Executors.newSingleThreadExecutor());

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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_booking_ok:
//                //확인버튼 누르면 예약 정보가 DB에 저장된다
//                saveBookingServiceDataToDB();

                //예약하기 버튼 누르면 결제 액티비티 화면으로 이동한다
                Intent paymentDialogIntent = new Intent(getContext(), DialogPaymentActivity.class);
                paymentDialogIntent.putExtra("walker_id", walkerName);             //예약할 도그워커 이름
                paymentDialogIntent.putExtra("owner_dog_name", walkDogName);       //예약할 강아지 이름
                paymentDialogIntent.putExtra("walk_total_time", totalWalkTime);    //예약할 총시간
                paymentDialogIntent.putExtra("walk_date", selectedDate);           //예약할 날짜
                paymentDialogIntent.putExtra("walk_total_price", totalPayPrice);    //총 결제 금액

                if(selectedTimeCalendarMin == 0){
                    String selectedTimeCalendarMinStr = "00";
                    paymentDialogIntent.putExtra("walk_time", selectedTimeCalendarHour+":"+selectedTimeCalendarMinStr);        //예약할 시간
                }else{
                    paymentDialogIntent.putExtra("walk_time", selectedTimeCalendarHour+":"+selectedTimeCalendarMin);           //예약할 시간
                }

                startActivityForResult(paymentDialogIntent, BOOKING_PAYMENT_SELECTED);
                break;
        }

    }

    //캘린더 클릭 이벤트 메소드
    public void onClickCalendarView(){

        calendarViewBooking.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                Boolean dialogTimeOn = false;

                //선택(클릭)한 날짜 데이터
                int Year = date.getYear();
                int Month = date.getMonth() + 1;  //1월 = 0 부터 시작함으로 +1 해준다
                int Day = date.getDay();
                CalendarDay selectedDay = CalendarDay.from(Year, Month, Day);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "클릭날짜 : " + selectedDay);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "nonServiceCalendarDates.size() : " + nonServiceCalendarDates.size());

                //DB에 저장해놓은 date 리스트
                for(int i=0; i<nonServiceCalendarDates.size(); i++){

                    int dbYear = nonServiceCalendarDates.get(i).getYear();
                    int dbMonth = nonServiceCalendarDates.get(i).getMonth() + 1;
                    int dbDay = nonServiceCalendarDates.get(i).getDay();
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[1]디비 dbYear : " + dbYear);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[1]디비 dbMonth : " + dbMonth);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[1]디비 dbDay : " + dbDay);
                    CalendarDay dbNoneServiceDay = CalendarDay.from(dbYear, dbMonth, dbDay);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "디비날짜 : " + dbNoneServiceDay);

                    if(dbNoneServiceDay.equals(selectedDay)){
                        //선택 불가 날짜
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "000 : " + 000);
                        makeToast("예약할 수 없는 날짜입니다");
                        dialogTimeOn = false;
                        break;
                    }else{
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "111 : " + 111);
                        //선택 가능 날짜
//                        makeToast("선택 가능 날짜 "+date.toString());
                        //시간 선택 다이얼로그 띄우기
//                        Bundle bundle = new Bundle();   //bundle = 무언가를 담을 보따리 or 주머니
//                        bundle.putInt("selectedYear", Year);    //선택한 년
//                        bundle.putInt("selectedMonth", Month);  //선택한 월
//                        bundle.putInt("selectedDay", Day);      //선택한 일
//                        bundle.putString("defaultWalkTime", defaultWalkTime);   //선택한 기본 산책 시간
//                        bundle.putInt("add30minTimeCount", add30minTimeCount);  //선택한 추가 산책 시간
//                        FragmentTimeDialog fragmentTimeDialog = new FragmentTimeDialog();
//                        fragmentTimeDialog.setArguments(bundle);
//                        fragmentTimeDialog.setTargetFragment(FragmentWalkerDetailSchedule.this, 5001);
//                        fragmentTimeDialog.show(getActivity().getSupportFragmentManager(), "fragmentTimeDialog");

                        dialogTimeOn = true;
                    }
                }

                //예약 불가 날짜가 없을 경우
                if(nonServiceCalendarDates.size() == 0){
                    dialogTimeOn = true;
                }

                //시간 다이얼로그 띄어줌
                if(dialogTimeOn == true){

                    Intent timeDialogIntent = new Intent(getContext(), DialogTimeActivity.class);
                    timeDialogIntent.putExtra("selectedYear", Year);    //선택한 년
                    timeDialogIntent.putExtra("selectedMonth", Month);  //선택한 월
                    timeDialogIntent.putExtra("selectedDay", Day);      //선택한 일
                    timeDialogIntent.putExtra("defaultWalkTime", defaultWalkTime);   //선택한 기본 산책 시간
                    timeDialogIntent.putExtra("add30minTimeCount", add30minTimeCount);  //선택한 추가 산책 시간
                    startActivityForResult(timeDialogIntent, BOOKING_TIME_SELECTED);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BOOKING_TIME_SELECTED){
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "호출횟수 : " + 1);

            selectedDate = data.getStringExtra("selectedDate");
            selectedTimeCalendarHour = data.getIntExtra("selectedTimeCalendarHour", 0);
            selectedTimeCalendarMin = data.getIntExtra("selectedTimeCalendarMin", 0);
            int selectedTimeCount = data.getIntExtra("selectedTimeCount", 0);

            tvBookingDate.setText(selectedDate);
            tvBookingTime.setText(selectedTimeCalendarHour+"시 "+selectedTimeCalendarMin+"분");
            totalWalkTime = selectedTimeCount*30;
            tvBookingWalkTime.setText("산책시간 "+totalWalkTime+"분 예약");
        }
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result) {
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
            makeLog(new Object() {
            }.getClass().getEnclosingMethod().getName() + "()", "[2]디비 nonServiceCalendarDates : " + nonServiceCalendarDates.toString());

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환

            for (int i = 0; i < Time_Result.length; i++) {
//                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "[2]디비 year : " + year);
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "[2]디비 month : " + month);
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "[2]디비 dayy : " + dayy);

                CalendarDay day = CalendarDay.from(year, month - 1, dayy);

                nonServiceCalendarDates.add(day);
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "[2]디비 nonServiceCalendarDates : " + nonServiceCalendarDates.get(i).toString());
//                calendar.set(year,month,dayy);
            }

            return nonServiceCalendarDates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return; //true or fasle 리턴시킴
            }

            calendarViewBooking.addDecorator(new NoneDaysDecoratorFragment(Color.RED, calendarDays, FragmentWalkerDetailSchedule.this));
        }
    }


}
