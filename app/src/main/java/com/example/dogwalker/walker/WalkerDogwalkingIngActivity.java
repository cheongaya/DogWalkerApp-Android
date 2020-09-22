package com.example.dogwalker.walker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.GpsTracker;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerDogwalkingIngBinding;
import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;

public class WalkerDogwalkingIngActivity extends BaseActivity {

    public static ActivityWalkerDogwalkingIngBinding binding;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;

    //Intent로 받은 산책 관련 데이터
    int booking_id;         //해당 산책 DB 컬럼 인덱스
    String owner_dog_name;  //산책 받는 강아지 이름
    int walk_total_time;    //산책 예약한 총 시간

    //스탑워치 관련 변수
    private long timeWhenStopped = 0;
    private boolean stopClicked;
    String timeWatch;

    //GPS 관련 변수
    ArrayList<LatLng> latLngArrayList;  //좌표 값을 담을 배열
    private GpsTracker gpsTracker;  //GpsTracker 클래스에서는 현재 위치를 가져와 주소로 변환하는 처리를 한다

    //배변횟수
    int pooCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_dogwalking_ing);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_dogwalking_ing);
        binding.setActivity(this);

        //Intent 에서 얻은 데이터
        Intent intent = getIntent();
        booking_id = intent.getIntExtra("booking_id", 0);
        owner_dog_name = intent.getStringExtra("owner_dog_name");
        walk_total_time = intent.getIntExtra("walk_total_time", 0);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[산책 데이터] booking_id : " + booking_id);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[산책 데이터] owner_dog_name : " + owner_dog_name);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[산책 데이터] walk_total_time : " + walk_total_time);
        //플로팅 버튼 열고 / 닫는 애니메이션 연결
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_btn_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_btn_close);

        binding.onClickBtnFloatVideo.startAnimation(fab_close);
        binding.onClickBtnFloatChat.startAnimation(fab_close);

        //배변횟수 화면에 표시
        binding.textViewWalkIngPooCount.setText(pooCount+"");

        //크로노미터(스탑워치) 셋팅
        chronometerInitSetting();

        //GpsTracker 객체 생성
        gpsTracker = new GpsTracker(WalkerDogwalkingIngActivity.this);

//        binding.textViewWalkDistance.setText((int) gpsTracker.distance+"");

    }


    /**
     * 버튼 클릭 이벤트 관련 코드
     */

    //닫기 버튼 클릭시 전 화면으로 돌아간다
    public void onClickClose(View view){
        finish();
    }

    //+ 버튼 클릭시 배변횟수가 증가한다
    public void onClickPlusPoo(View view){
        pooCount = pooCount+1;
        binding.textViewWalkIngPooCount.setText(pooCount+"");
    }

    //- 버튼 클릭시 배변횟수가 감소한다
    public void onClickMinusPoo(View view){
        pooCount = pooCount-1;
        binding.textViewWalkIngPooCount.setText(pooCount+"");
    }

    //카메라 버튼 클릭시 필터가 적용된 사진을 찍을 수 있다
    public void onClickBtnFloatCamera(View view){
    }

    //재생 버튼 클릭시 산책시간 스톱워치를 시작시킬 수 있다
    public void onClickBtnFloatStart(View view){
        //스탑워치 시작
        binding.chronometerStopWatch.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[start] timeWhenStopped : " + timeWhenStopped);
        binding.chronometerStopWatch.start();  //시간 갱신을 시작한다
        binding.onClickBtnFloatPause.setVisibility(View.VISIBLE);
        binding.onClickBtnFloatStart.setVisibility(View.INVISIBLE);
        stopClicked = false;

    }

    //일시정지 버튼 클릭시 산책시간 스톱워치를 일시정지시킬 수 있다
    public void onClickBtnFloatPause(View view){
        //스탑워치 정지
        if (!stopClicked) {
            timeWhenStopped = binding.chronometerStopWatch.getBase() - SystemClock.elapsedRealtime();
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[stop] timeWhenStopped : " + timeWhenStopped);
            binding.chronometerStopWatch.stop();    //시간 갱신을 중지한다
            binding.onClickBtnFloatStart.setVisibility(View.VISIBLE);
            binding.onClickBtnFloatPause.setVisibility(View.INVISIBLE);
            stopClicked = true;
        }
    }

    //완료 버튼 클릭시 산책을 완료 시킬 수 있다
    public void onClickBtnFloatDone(View view){
        //스탑워치 정지
        if (!stopClicked) {
            timeWhenStopped = binding.chronometerStopWatch.getBase() - SystemClock.elapsedRealtime();
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[stop] timeWhenStopped : " + timeWhenStopped);
            binding.chronometerStopWatch.stop();    //시간 갱신을 중지한다
            binding.onClickBtnFloatStart.setVisibility(View.VISIBLE);
            binding.onClickBtnFloatPause.setVisibility(View.INVISIBLE);
            stopClicked = true;
        }
        //스탑워치 기록 보여줌
//        makeToast(timeWatch);
        Intent dogwalkingDoneIntent = new Intent(WalkerDogwalkingIngActivity.this, WalkerDogwalkingDoneActivity.class);
        dogwalkingDoneIntent.putExtra("booking_id", booking_id);            //산책 idx
        dogwalkingDoneIntent.putExtra("owner_dog_name", owner_dog_name);    //산책 받는 강아지 이름
        dogwalkingDoneIntent.putExtra("walk_total_time", walk_total_time);  //산책 예약 총 시간
        dogwalkingDoneIntent.putExtra("walkingTime", timeWatch);            //산책 스탑워치 시간
        dogwalkingDoneIntent.putExtra("walkingDistance", binding.textViewWalkDistance.getText().toString());    //산책 이동거리
        dogwalkingDoneIntent.putExtra("walkingPooCount", binding.textViewWalkIngPooCount.getText().toString()); //산책 배변횟수
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "좌표 배열 크기 : " + gpsTracker.latLngArrayList.size());
        dogwalkingDoneIntent.putExtra("latLngArrayList", gpsTracker.latLngArrayList);
        startActivity(dogwalkingDoneIntent);
    }

    //리스트 버튼 클릭시 영상스트리밍 / 채팅 버튼이 위에 나온다
    public void onClickBtnFloatList(View view){
        anim();
    }

    //비디오 버튼 클릭시 산책하는 모습을 스트리밍을 할 수 있다
    public void onClickBtnFloatVideo(View view){
    }

    //채팅 버튼 클릭시 반려인과 채팅할 수 있다
    public void onClickBtnFloatChat(View view){
    }

    //플로팅 버튼 관련 에니메이션 메소드
    public void anim() {

        if (isFabOpen) {
            binding.onClickBtnFloatVideo.startAnimation(fab_close); //애니메이션 실행
            binding.onClickBtnFloatChat.startAnimation(fab_close);
            binding.onClickBtnFloatVideo.setClickable(false);   //버튼 활성화 / 비활성화
            binding.onClickBtnFloatChat.setClickable(false);
            isFabOpen = false;
        } else {
            binding.onClickBtnFloatVideo.startAnimation(fab_open);
            binding.onClickBtnFloatChat.startAnimation(fab_open);
            binding.onClickBtnFloatVideo.setClickable(true);
            binding.onClickBtnFloatChat.setClickable(true);
            isFabOpen = true;
        }
    }

    //크로노 미터 관련 초기 셋팅
    public void chronometerInitSetting(){
        //chronometer 가 바뀔 때 리스너가 작동한다
        binding.chronometerStopWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "time : " + time);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "SystemClock.elapsedRealtime() : " + SystemClock.elapsedRealtime());
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "chronometer.getBase() : " + chronometer.getBase());
                int h = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s = (int)(time - h*3600000- m*60000)/1000 ;
                timeWatch = (h < 10 ? "0"+h: h)+ ":" +(m < 10 ? "0"+m: m)+ ":" + (s < 10 ? "0"+s: s);
                chronometer.setText(timeWatch);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "timeWatch : " + timeWatch);
            }
        });

        //처음 시간 셋팅
        binding.chronometerStopWatch.setBase(SystemClock.elapsedRealtime());
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[set] SystemClock.elapsedRealtime() : " + SystemClock.elapsedRealtime());
        binding.chronometerStopWatch.setText("00:00:00");

        //스탑워치 시작
//        binding.chronometerStopWatch.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[start] timeWhenStopped : " + timeWhenStopped);
        binding.chronometerStopWatch.start();  //시간 갱신을 시작한다
        binding.onClickBtnFloatStart.setVisibility(View.INVISIBLE);
        stopClicked = false;

    }
}