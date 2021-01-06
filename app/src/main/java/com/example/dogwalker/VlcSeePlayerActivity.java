package com.example.dogwalker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.pedro.encoder.input.video.CameraOpenException;

import org.videolan.libvlc.MediaPlayer;

import java.util.Arrays;

public class VlcSeePlayerActivity extends BaseActivity implements VlcListener, View.OnClickListener{

    private View decorView;
    private int	uiOption;

    private VlcVideoLibrary vlcVideoLibrary;
    private ImageButton bStartStop, btnClose;

//    String LIVE_STREAM_URL = "rtmp://15.164.216.186:1935/myapp/rootc";
    String LIVE_STREAM_URL; //rtmp://15.164.216.186:1935/myapp/walker2

    private String[] options = new String[]{":fullscreen"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_see_cast);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // surfaceview 기본 셋팅
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility( uiOption );

        //Intent 로 받은 스트리밍 URL
        Intent intent = getIntent();
        LIVE_STREAM_URL = intent.getStringExtra("liveStreamUrl");
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[시청자] 스트리밍 URL : "+LIVE_STREAM_URL);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
//        bStartStop = (Button) findViewById(R.id.b_start_stop);
        btnClose = (ImageButton) findViewById(R.id.button_close);
//        bStartStop.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        vlcVideoLibrary = new VlcVideoLibrary(this, this, surfaceView);
        vlcVideoLibrary.setOptions(Arrays.asList(options));

        //라이브 방송 시청 시작
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()  {
            public void run() {
                seeStart();
            }
        }, 1000);
    }

    //  플레이어 시작 함수
    public void seeStart(){
        if (!vlcVideoLibrary.isPlaying()) {
            vlcVideoLibrary.play(LIVE_STREAM_URL);
        } else {
            vlcVideoLibrary.stop();
        }
    }


    @Override
    public void onComplete() {
        Toast.makeText(this, "방송 시청을 시작합니다", Toast.LENGTH_SHORT).show();
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[시청자] : onComplete() - playing....");
    }

    @Override
    public void onError() {
        vlcVideoLibrary.stop(); //스트리밍 방송 시청 종료
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[시청자] : onError()");
        Toast.makeText(this, "방송 시청을 종료합니다", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBuffering(MediaPlayer.Event event) {
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[시청자] : onBuffering()");
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.button_close:
                // (뒤로가기)
                vlcVideoLibrary.stop(); //스트리밍 방송 시청 종료
                finish();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[시청자] : 뒤로가기()");
                break;
            default:
                break;
        }
    }

//    if (!vlcVideoLibrary.isPlaying()) {
//        vlcVideoLibrary.play(LIVE_STREAM_URL); //해당 미디어 서버 URL로 방송 시작
//    } else {
//        vlcVideoLibrary.stop();
//    }
}