package com.example.dogwalker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dogwalker.owner.OwnerLiveActivity;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VlcBroadPlayerActivity extends BaseActivity implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    String walker_id;
    int booking_id;

    private View decorView;
    private int	uiOption;

    public RtmpCamera1 rtmpCamera1;
    private ImageButton btnLiveStart, btnLiveStop, btnSwitchCamera, btnClose;

//    private File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/rtmp-rtsp-stream-client-java");

//    String RTMP_SERVER_URL = "rtmp://15.164.216.186:1935/myapp/rootc";
    String RTMP_SERVER_URL = "rtmp://15.164.216.186:1935/myapp/";

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_broad_cast);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility( uiOption );

        //Intent 로 받은 도그워커 아이디
        Intent intent = getIntent();
        walker_id = intent.getStringExtra("walker_id");
        booking_id = intent.getIntExtra("booking_id", 0);
        makeLog("도그워커 ID : "+walker_id);
        makeLog("산책예약 ID : "+booking_id);

        //ID 연결
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        btnLiveStart = (ImageButton) findViewById(R.id.button_live_start); //스트리밍 방송 시작
        btnLiveStop = (ImageButton) findViewById(R.id.button_live_stop);    //스트리밍 방송 종료
        btnSwitchCamera = (ImageButton) findViewById(R.id.button_switch_camera);    //카메라 화면 전환
        btnClose = (ImageButton) findViewById(R.id.button_close);   //스트리밍 화면 종료 (뒤로가기)

        //스트리밍 방송 중지 버튼 숨김 처리
        btnLiveStop.setVisibility(View.GONE);

        //RtmpCamera1 초기셋팅
        rtmpCamera1 = new RtmpCamera1(surfaceView, this);
        rtmpCamera1.setReTries(10);
        rtmpCamera1.setPreviewOrientation(90);
        surfaceView.getHolder().addCallback(this);
        handler = new Handler();

        //클릭 리스너 연결
        btnLiveStart.setOnClickListener(this);
        btnLiveStop.setOnClickListener(this);
        btnSwitchCamera.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        makeLog("surfaceCreated()");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        makeLog("surfaceChanged()");
        rtmpCamera1.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        makeLog("surfaceDestroyed()");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && rtmpCamera1.isRecording()) {
            rtmpCamera1.stopRecord();
        }
        //스트리밍 중일때
        if (rtmpCamera1.isStreaming()) {
            //스트리밍 중지
            rtmpCamera1.stopStream();
            btnLiveStart.setVisibility(View.VISIBLE);
            btnLiveStop.setVisibility(View.GONE);
            Toast.makeText(this, "라이브 방송을 종료합니다", Toast.LENGTH_SHORT).show();
            //DB로 해당 예약 ID에 스트리밍 URL값 off 전송
            updateBookingLiveStatus(booking_id, "off");
        }
        rtmpCamera1.stopPreview();
    }

    //스트리밍 방송 시작 함수
    public void rtmpStart(){
        //스트리밍 방송 시작
        if (!rtmpCamera1.isStreaming()) {
            makeLog("방송 시작()");
            if (rtmpCamera1.isRecording() || rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
                btnLiveStart.setVisibility(View.GONE);
                btnLiveStop.setVisibility(View.VISIBLE);
                rtmpCamera1.startStream(RTMP_SERVER_URL+walker_id); //미디어 서버 URL 로 스트리밍 방송 송출 시작

                //DB로 해당 예약 ID에 스트리밍 URL값 전송
                updateBookingLiveStatus(booking_id, RTMP_SERVER_URL+walker_id);

                //스트리밍 시작 알림 보내기
                MyFirebaseSendNotification.sendNotification(MyFirebaseSendNotification.user3Key, "산책 방송 라이브중", "산책중인 모습을 봐주세요");

            } else {
                Toast.makeText(this, "라이브 방송이 불가합니다", Toast.LENGTH_SHORT).show();
            }
        } else {
            //스트리밍 방송 종료
            makeLog("방송 종료()");
            btnLiveStart.setVisibility(View.VISIBLE);
            btnLiveStop.setVisibility(View.GONE);
            rtmpCamera1.stopStream();
            //DB로 해당 예약 ID에 스트리밍 URL값 off 전송
            updateBookingLiveStatus(booking_id, "off");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_live_start:
                //스트리밍 방송 시작
                handler.postDelayed(new Runnable()  {
                    public void run() {
                        rtmpStart();
                    }
                }, 1500);

                break;
            case R.id.button_live_stop:
                //스트리밍 방송 종료
                makeLog("방송 종료()");
                btnLiveStart.setVisibility(View.VISIBLE);
                btnLiveStop.setVisibility(View.GONE);
                rtmpCamera1.stopStream();
                Toast.makeText(this, "라이브 방송을 종료합니다", Toast.LENGTH_SHORT).show();

                break;
            case R.id.button_switch_camera:
                //카메라 전환
                try {
                    rtmpCamera1.switchCamera();
                } catch (CameraOpenException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_close:
                //스트리밍 화면 종료 (뒤로가기)
                makeLog("뒤로가기()");
                rtmpCamera1.stopStream();
                //DB로 해당 예약 ID에 스트리밍 URL값 off 전송
                updateBookingLiveStatus(booking_id, "off");
                finish();
                break;
            default:
                break;
        }
    }

    //해당 산책 예약 -> 스트리밍 방송 상태 업데이트 (off -> URL or URL -> off)
    public void updateBookingLiveStatus(int booking_id, String live_url){

        Call<ResultDTO> call = retrofitApi.updateBookingLiveStatus(booking_id, live_url);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                makeLog("스트리밍 방송 상태 조회 - 성공");
                ResultDTO resultDTO = response.body();
                makeLog("스트리밍 방송 상태 조회 - 성공값 : "+resultDTO.getResponceResult());
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog("스트리밍 방송 상태 조회 - 실패");
            }
        });

    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                makeLog("onConnectionSuccessRtmp() - Connection success");
                Toast.makeText(VlcBroadPlayerActivity.this, "라이브 방송을 시작합니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(@NonNull String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //스트리밍 연결 실패시
                if (rtmpCamera1.reTry(5000, reason)) {
                    makeLog("onConnectionFailedRtmp() - Retry");
                } else {
                    makeLog("onConnectionFailedRtmp() - Connection failed / "+reason);
                    rtmpCamera1.stopStream();
                    btnLiveStart.setVisibility(View.VISIBLE);
                    btnLiveStop.setVisibility(View.GONE);
                    //DB로 해당 예약 ID에 스트리밍 URL값 off 전송
                    updateBookingLiveStatus(booking_id, "off");
                }
            }
        });
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {
        makeLog("onNewBitrateRtmp()");

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                makeLog("onDisconnectRtmp() - Disconnected");
//                Toast.makeText(VlcBroadPlayerActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                makeLog("onAuthErrorRtmp() - Auth error");
//                Toast.makeText(VlcBroadPlayerActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                makeLog("onAuthSuccessRtmp() - Auth success");
//                Toast.makeText(VlcBroadPlayerActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //로그 : 액티비티명 + 함수명 + 원하는 데이터를 한번에 보기위한 로그
    public void makeLog(String strData) {
        Log.d(TAG, className + "_[비디오] " + strData);
    }
}