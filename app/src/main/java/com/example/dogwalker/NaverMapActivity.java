package com.example.dogwalker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;

import com.example.dogwalker.databinding.ActivityNaverMapBinding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.util.FusedLocationSource;

public class NaverMapActivity extends BaseActivity implements OnMapReadyCallback, LocationListener {

    ActivityNaverMapBinding binding;

    FusedLocationSource locationSource;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;

    //스탑워치 관련 변수
    private long timeWhenStopped = 0;
    private boolean stopClicked;
    String timeWatch;

    //지도 관련 변수
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private NaverMap map;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; //10m
    private static final long MIN_TIME_BW_UPDATES = 1000;  //1분 (1000 * 60 * 1)

    //이동 거리 계산 관련 변수
    Location lastKnownLocation;
    float distance = 0;

    @Nullable
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_naver_map);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_naver_map);
        binding.setActivity(this);

        //플로팅 버튼 열고 / 닫는 애니메이션 연결
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_btn_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_btn_close);

        binding.onClickBtnFloatVideo.startAnimation(fab_close);
        binding.onClickBtnFloatChat.startAnimation(fab_close);

        //크로노미터(스탑워치) 셋팅
        chronometerInitSetting();

        //NaverMap 객체 얻어오기
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
//        mapFragment.getMapAsync(naverMap -> map = naverMap);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //TODO : 위치트랙커 테스트중
        locationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "naverMap onMapReady() : " + naverMap);

        //UiSettings 는 UI 와 관련된 설정을 담당하는 클래스이다
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        //TODO : 위치트랙커 테스트중
        naverMap.setLocationSource(locationSource);

        //위치 추적 모드 follow 지정 -> 위치 추적 활성화 / 현위치 오버레이와 카메라의 좌표가 사용자의 위치를 따라 움직인다
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
//                makeToast("야 : "+String.valueOf(location.getLatitude()));
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "야 : " + String.valueOf(location.getLatitude()));

                LatLng coord = new LatLng(location);

                LocationOverlay locationOverlay = map.getLocationOverlay();
                locationOverlay.setVisible(true);
                locationOverlay.setPosition(coord);
                locationOverlay.setBearing(location.getBearing());

                if(lastKnownLocation == null) {
                    lastKnownLocation = location;
                    Log.d("DeveloperLog","Distance: null");
                }
                else {
                    distance = lastKnownLocation.distanceTo(location);    //meter
                    Log.d("DeveloperLog","Distance:"+distance);
                    makeToast(distance+"");
//            lastKnownLocation = location;
                    //화면에 표시
                    double distanceKm = distance / 1000;
                    Log.d("DeveloperLog","distanceKm:"+String.format("%.2f", distanceKm));  //소숫점 2자리까지 표현
                    binding.textViewWalkDistance.setText(String.format("%.2f", distanceKm));
                    map.moveCamera(CameraUpdate.scrollTo(coord));
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

        //앱 백그라운드 시에도 위치 정보 받아온다
        Log.d("DeveloperLog", "onLocationChanged() :"+location.getLongitude() + " / " +location.getLatitude());

        if (map == null || location == null) {
            return;
        }

        LatLng coord = new LatLng(location);

        LocationOverlay locationOverlay = map.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(coord);
        locationOverlay.setBearing(location.getBearing());

        map.moveCamera(CameraUpdate.scrollTo(coord));

//        // TODO : TEST 중
//        distance = lastKnownLocation.distanceTo(location);
//        makeToast(distance+"");
//        Log.d("DeveloperLog","Distance:"+distance);
//

//        if(lastKnownLocation == null) {
//            lastKnownLocation = location;
//            Log.d("DeveloperLog","Distance: null");
//        }
//        else {
//            distance = lastKnownLocation.distanceTo(location);    //meter
//            Log.d("DeveloperLog","Distance:"+distance);
//            makeToast(distance+"");
////            lastKnownLocation = location;
//            //화면에 표시
//            double distanceKm = distance / 1000;
//            Log.d("DeveloperLog","distanceKm:"+String.format("%.2f", distanceKm));  //소숫점 2자리까지 표현
//            binding.textViewWalkDistance.setText(String.format("%.2f", distanceKm));
//            map.moveCamera(CameraUpdate.zoomIn());
//        }

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

    }

    //- 버튼 클릭시 배변횟수가 감소한다
    public void onClickMinusPoo(View view){

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
        stopClicked = false;

    }

    //일시정지 버튼 클릭시 산책시간 스톱워치를 일시정지시킬 수 있다
    public void onClickBtnFloatPause(View view){
        //스탑워치 정지
        if (!stopClicked) {
            timeWhenStopped = binding.chronometerStopWatch.getBase() - SystemClock.elapsedRealtime();
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[stop] timeWhenStopped : " + timeWhenStopped);
            binding.chronometerStopWatch.stop();    //시간 갱신을 중지한다
            stopClicked = true;
        }
    }

    //완료 버튼 클릭시 산책을 완료 시킬 수 있다
    public void onClickBtnFloatDone(View view){
        //스탑워치 기록 보여줌
        makeToast(timeWatch);
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

    }


    /**
     *
     * 위치 권한 허용 관련 코드
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (hasPermission() && locationManager != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);   //1초마다 10미터 변경될때마다 함수 호출
            }
            return;
        }

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (hasPermission()) {
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        } else {
            ActivityCompat.requestPermissions(
                    this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean hasPermission() {
        return PermissionChecker.checkSelfPermission(this, PERMISSIONS[0])
                == PermissionChecker.PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(this, PERMISSIONS[1])
                == PermissionChecker.PERMISSION_GRANTED;
    }
}