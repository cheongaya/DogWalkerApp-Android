package com.example.dogwalker.walker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;

public class WalkerStopWatchActivity extends BaseActivity implements View.OnClickListener {

    private Chronometer chronometer;
    private long timeWhenStopped = 0;
    private boolean stopClicked;
    String timeWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walker_stop_watch);

        chronometer = (Chronometer) findViewById(R.id.chronometer_stop_watch);
        //chronometer 가 바뀔 때 리스너가 작동한다
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
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
        chronometer.setBase(SystemClock.elapsedRealtime());
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[set] SystemClock.elapsedRealtime() : " + SystemClock.elapsedRealtime());
        chronometer.setText("00:00:00");

        //버튼 id 연결
        Button startBtn = (Button) findViewById(R.id.start_button);
        Button pauseBtn = (Button) findViewById(R.id.pause_button);
        Button resetBtn = (Button) findViewById(R.id.reset_button);
        Button storeBtn = (Button) findViewById(R.id.store_button);

        //버튼 이벤트 연결
        startBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        storeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //버튼 이벤트 처리
        switch (v.getId()){
            case R.id.start_button:
                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[start] timeWhenStopped : " + timeWhenStopped);
                chronometer.start();  //시간 갱신을 시작한다
                stopClicked = false;
                break;
            case R.id.pause_button:
                if (!stopClicked) {
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[stop] timeWhenStopped : " + timeWhenStopped);
                    chronometer.stop();    //시간 갱신을 중지한다
                    stopClicked = true;
                    break;
                }
            case R.id.reset_button:
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.stop();
                timeWhenStopped = 0;
                break;
            case R.id.store_button:
                //다시 setBase 시켜준다
//                chronometer.setBase(SystemClock.elapsedRealtime());
//                chronometer.stop();
//                timeWhenStopped = 0;
                makeToast(timeWatch);
                break;
        }
    }

    //TODO: 앱이 종료되기 전 반드시 stop 을 해줘야 메모리 leak 이 발생하지 않는다
    //반드시 앱 종료전에 STOP 메소드를 호출해야 메모리릭이 발생하지 않는다.
//    public void onDestroy(){
//        super.onDestroy();
//        chronometer.stop();
//    }

}