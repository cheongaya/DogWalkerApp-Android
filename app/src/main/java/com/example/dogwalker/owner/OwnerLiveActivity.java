package com.example.dogwalker.owner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dogwalker.R;
import com.example.dogwalker.VlcSeePlayerActivity;
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerLiveActivity extends OwnerBottomNavigation implements View.OnClickListener{

    private LinearLayout linearLiveOn, linearLiveOff;
    private Button btnSeeLive;
    String liveStreamUrl;   //스트리밍 URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_live);

        //ID 연결
        linearLiveOn = (LinearLayout) findViewById(R.id.linearLayout_live_on);
        linearLiveOff = (LinearLayout) findViewById(R.id.linearLayout_live_off);
        btnSeeLive = (Button) findViewById(R.id.button_see_live);

        //클릭 리스너 연결
        btnSeeLive.setOnClickListener(this);

        //산책 라이브 상태 데이터 조회
        selectDogWalkLiveData();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_see_live:
                //산책 방송 보는 채널로 화면 전환
                if(liveStreamUrl.equals(null)){
                    Toast.makeText(getApplicationContext(), "현재 라이브 중인 방송이 없습니다", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(OwnerLiveActivity.this, VlcSeePlayerActivity.class);
                    intent.putExtra("liveStreamUrl", liveStreamUrl);
                    startActivity(intent);
                }

                break;
            default:
                break;
        }
    }

    //산책 라이브 상태 데이터 조회
    public void selectDogWalkLiveData(){

        Call<ResultDTO> call = retrofitApi.selectDogWalkLiveData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[라이브] 데이터 조회 성공");
                ResultDTO resultDTO = response.body();

                liveStreamUrl = resultDTO.getResponceResult(); //스트리밍 URL 또는 null 값이 반환된다
                //라이브 off 일떄
                if(liveStreamUrl == null){
                    linearLiveOn.setVisibility(View.GONE);
                    linearLiveOff.setVisibility(View.VISIBLE);
                //라이브 on 일떄
                }else{
                    linearLiveOn.setVisibility(View.VISIBLE);
                    linearLiveOff.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[라이브] 데이터 조회 실패  : " + t.toString() );
            }
        });

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_owner_live;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavOwner03;
    }

}