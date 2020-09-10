package com.example.dogwalker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.MultiAutoCompleteTextView;

import com.example.dogwalker.data.WalkerlistDTO;
import com.example.dogwalker.databinding.ActivityNaverMapBinding;
import com.example.dogwalker.owner.OwnerWalkerDetailActivity;
import com.example.dogwalker.owner.OwnerWalkerlistActivity;
import com.example.dogwalker.retrofit2.response.WalkerLocationDTO;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NaverMapActivity extends BaseActivity implements OnMapReadyCallback, LocationListener {

    ActivityNaverMapBinding binding;
    FusedLocationSource locationSource;
    InfoWindow infoWindow;  //마커 정보창 객체

    //Intent 객체에서 받아온 데이터
    String walkDogName;
    String defaultWalkTime;
    int add30minTimeCount;

    //지도 관련 변수
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private NaverMap map;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; //10m
    private static final long MIN_TIME_BW_UPDATES = 1000;  //1분 (1000 * 60 * 1)

    @Nullable
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_naver_map);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_naver_map);
        binding.setActivity(this);

        //OwnerWalkerlistActivity 에서 받아온 산책시간 선택 데이터
        Intent intent = getIntent();
        walkDogName = intent.getStringExtra("walkDogName");
        defaultWalkTime = intent.getStringExtra("defaultWalkTime");
        add30minTimeCount = intent.getIntExtra("add30minTimeCount", 0);

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

        //DB에 저장된 도그워커의 주소(좌표) 데이터를 불러와 마커로 표시해준다
        selectWalkerLocationToDB();

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

    }

    //DB에 저장된 도그워커의 주소(좌표) 데이터를 불러온다
    public void selectWalkerLocationToDB(){

        Call<List<WalkerLocationDTO>> call = retrofitApi.selectWalkerLocation("abc");
        call.enqueue(new Callback<List<WalkerLocationDTO>>() {
            @Override
            public void onResponse(Call<List<WalkerLocationDTO>> call, Response<List<WalkerLocationDTO>> response) {
                List<WalkerLocationDTO> walkerLocationDTOList = response.body();

                for(int i=0; i<walkerLocationDTOList.size(); i++){
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "아이디("+i+") : " + walkerLocationDTOList.get(i).getId());
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "만족도("+i+") : " + walkerLocationDTOList.get(i).getSatisfation_score());
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "위도("+i+") : " + walkerLocationDTOList.get(i).getLatitude());
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "경도("+i+") : " + walkerLocationDTOList.get(i).getLongitude());
                    //결과 데이터 (위도/경도)
                    String walker_id = walkerLocationDTOList.get(i).getId();
                    int satisfation_score = walkerLocationDTOList.get(i).getSatisfation_score();
                    String latitudeStr = walkerLocationDTOList.get(i).getLatitude();
                    String longitudeStr = walkerLocationDTOList.get(i).getLongitude();
                    //String -> double 형변환
                    if(latitudeStr != null && longitudeStr != null){
                        double latitude = Double.valueOf(latitudeStr);
                        double longitude = Double.valueOf(longitudeStr);

                        //DB에서 불러온 도그워커의 위치(좌표)를 마커 객체를 생성해서 지도 위에 추가해준다
                        createWalkerLocationMarker(walker_id, satisfation_score, latitude, longitude);

                    }else{
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "아이디("+i+")" + walkerLocationDTOList.get(i).getId()+" 는 좌표값이 null 입니다");
                    }

                }
            }

            @Override
            public void onFailure(Call<List<WalkerLocationDTO>> call, Throwable t) {

            }
        });

    }

    //DB에서 불러온 도그워커의 위치(좌표)를 마커 객체를 생성해서 지도 위에 추가해준다
    public void createWalkerLocationMarker(String walker_id, int satisfation_score, double latitude, double longitude){

        Marker marker = new Marker();
        marker.setTag(walker_id);
        marker.setPosition(new LatLng(latitude, longitude));
        marker.setIcon(MarkerIcons.BLACK);

        //만족도에 따른 마커 색상 지정
        if(satisfation_score >= 0 && satisfation_score <= 20){
            marker.setIconTintColor(getColor(R.color.markerLevel1));
        }else if(satisfation_score > 20 && satisfation_score <= 40){
            marker.setIconTintColor(getColor(R.color.markerLevel2));
        }else if(satisfation_score > 40 && satisfation_score <= 60){
            marker.setIconTintColor(getColor(R.color.markerLevel3));
        }else if(satisfation_score > 60 && satisfation_score <= 80){
            marker.setIconTintColor(getColor(R.color.markerLevel4));
        }else if(satisfation_score > 80 && satisfation_score <= 100){
            marker.setIconTintColor(getColor(R.color.markerLevel5));
        }

        marker.setMap(map);

        createInfoWindow(marker, walker_id);

        marker.setOnClickListener(overlay -> {

            if(marker.getInfoWindow() == null){
                //현재 마커에 정보창이 열려있지 않을 경우 정보창을 엶
                infoWindow.open(marker);
            }else{
                //이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close();
            }

            return true;
        });
    }

    //마커 정보창 객체를 생성한다 (정보창에는 도그워커의 아이디가 표시된다)
    public void createInfoWindow(Marker marker, String title){
        //정보창 객체 생성
        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
//                return null;
                //정보창 클릭시 이벤트
                infoWindow.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {
                        makeToast(infoWindow.getMarker().getTag().toString());

                        Intent intentWalkerDetail = new Intent(NaverMapActivity.this, OwnerWalkerDetailActivity.class);
                        intentWalkerDetail.putExtra("walkerName", infoWindow.getMarker().getTag().toString());                  //도그워커 아이디
                        intentWalkerDetail.putExtra("walkDogName", walkDogName);                //산책시킬 강아지 이름
                        intentWalkerDetail.putExtra("defaultWalkTime", defaultWalkTime);        //산책 기본 시간
                        intentWalkerDetail.putExtra("add30minTimeCount", add30minTimeCount);    //산책 추가 시간
                        startActivity(intentWalkerDetail);
                        finish();

                        return true;
                    }
                });

                // 정보 창이 열린 마커의 tag를 텍스트로 노출하도록 반환
                return (CharSequence)infoWindow.getMarker().getTag();
            }
        });

//        ViewGroup rootView = (ViewGroup)findViewById(R.id.map);
//        LocationWalkerMakerAdapter adapter = new LocationWalkerMakerAdapter(getApplicationContext(), rootView);
//        infoWindow.setAdapter(adapter);
//        //인포창의 우선순위
//        infoWindow.setZIndex(10);
//        //투명도 조정
//        infoWindow.setAlpha(0.9f);

    }

    //주소, 지역명 으로 검색
    public void onClickKeywordWalkerSearch(View view){

    }

    /**
     * 버튼 클릭 이벤트 관련 코드
     */

    //닫기 버튼 클릭시 전 화면으로 돌아간다
    public void onClickClose(View view){
        finish();
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