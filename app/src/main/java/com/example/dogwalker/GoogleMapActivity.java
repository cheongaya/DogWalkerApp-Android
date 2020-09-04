package com.example.dogwalker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.Toast;

import com.example.dogwalker.databinding.ActivityGoogleMapBinding;
import com.example.dogwalker.walker.WalkerMypageActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMapActivity extends BaseActivity implements OnMapReadyCallback {

    ActivityGoogleMapBinding binding;

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;

    //스탑워치 관련 변수
    private long timeWhenStopped = 0;
    private boolean stopClicked;
    String timeWatch;

    //구글맵 관련 참조 변수
    private GoogleMap googleMap;
    private Marker currentMarker = null;
    private SupportMapFragment mapFragment;
    private Location mCurrentLocatiion;
    private LatLng currentPosition;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    //GPS 관련
    boolean needRequest = false;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초 = 위치가 Update 되는 주기
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 10; // 위치 획득후 업데이트되는 주기 -> (500 = 0.5초 단위로 화면 갱신됨) (1000 * 30)
    private static final int GPS_ENABLE_REQUEST_CODE = 1008;
    private static final int PERMISSION_REQUEST_CODE = 1009; //권한 요청 코드 (onRequestPermissionsResult 에서)
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}; // 앱을 실행하기 위해 필요한 퍼미션을 정의

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_google_map);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_google_map);
        binding.setActivity(this);

        //플로팅 버튼 열고 / 닫는 애니메이션 연결
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_btn_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_btn_close);

        binding.onClickBtnFloatVideo.startAnimation(fab_close);
        binding.onClickBtnFloatChat.startAnimation(fab_close);

        //크로노미터(스탑워치) 셋팅
        chronometerInitSetting();

        //구글맵 관련
        //구글맵 권한 허용 관련 start
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)    //정확도를 최우선적으로 고려
                .setInterval(UPDATE_INTERVAL_MS)                        //위치가 Update 되는 주기
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);        //위치 획득후 업데이트되는 주기

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //권한 허용 관련 end

        //맵을 빌드함
//        mapFragment = MapFragment.newInstance();
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.googleMap, mapFragment);
//        fragmentTransaction.commit();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googlemap) {

        googleMap = googlemap;

        //GPS를 찾지 못하는 장소에 있을 경우 지도의 초기 위치 서울로 설정 (런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전)
        setDefaultLocation();

        //런타임 퍼미션 처리
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
                &&  hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            startLocationUpdates(); // 3. 위치 업데이트 시작

        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                makeToast("이 앱을 실행하려면 위치 접근 권한이 필요합니다.");

                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);

            }
        }

        //
        googleMap.setMyLocationEnabled(true);    //현재위치 버튼
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));    //클수록 줌인 된다
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "지도 클릭 : " + latLng);
            }
        });

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
     * 지도 api 관련 코드
     */

    //위치콜백
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "LocationCallback 호출됨");

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude()) + " 경도:" + String.valueOf(location.getLongitude());

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "markerSnippet : " + markerSnippet);

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocatiion = location;
            }
        }
    };

    //위치 업데이트 메소드
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "위치 권한 허용안되 있음 -> 다이얼로그 띄어줌");
            showDialogForLocationServiceSetting();

        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "퍼미션 안가지고 있음");
                return;
            }

            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "위치 권한 허용되 있음");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                googleMap.setMyLocationEnabled(true);
        }
    }

    //마커 하나 찍기
    public void oneMarker() {
        // 서울 여의도에 대한 위치 설정
        LatLng seoul = new LatLng(37.52487, 126.92723);

        // 구글 맵에 표시할 마커에 대한 옵션 설정  (알파는 좌표의 투명도이다.)
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(seoul)
                .title("원하는 위치(위도, 경도)에 마커를 표시했습니다.")
                .snippet("여기는 여의도인거같네여!!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .alpha(0.5f);

        // 마커를 생성한다. showInfoWindow를 쓰면 처음부터 마커에 상세정보가 뜨게한다. (안쓰면 마커눌러야뜸)
        googleMap.addMarker(makerOptions); //.showInfoWindow();

        //정보창 클릭 리스너
        googleMap.setOnInfoWindowClickListener(infoWindowClickListener);

        //마커 클릭 리스너
        googleMap.setOnMarkerClickListener(markerClickListener);

        //카메라를 여의도 위치로 옮긴다.
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));
        //처음 줌 레벨 설정 (해당좌표=>서울, 줌레벨(16)을 매개변수로 넣으면 된다.) (위에 코드대신 사용가능)(중첩되면 이걸 우선시하는듯)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 16));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(), "눌렀습니다!!", Toast.LENGTH_LONG);
                return false;
            }
        });

    }


    //정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            String markerId = marker.getId();
            Toast.makeText(getApplicationContext(), "정보창 클릭 Marker ID : "+markerId, Toast.LENGTH_SHORT).show();
        }
    };

    //마커 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String markerId = marker.getId();
            //선택한 타겟위치
            LatLng location = marker.getPosition();
            Toast.makeText(getApplicationContext(), "마커 클릭 Marker ID : "+markerId+"("+location.latitude+" "+location.longitude+")", Toast.LENGTH_SHORT).show();
            return false;
        }
    };

    //현재 주소 얻어오는 메소드
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    //
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //현재 위치 셋팅 메소드
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = googleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        googleMap.moveCamera(cameraUpdate);

    }


    //지도의 초기위치를 서울로 이동하는 메소드
    public void setDefaultLocation() {

        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = googleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        googleMap.moveCamera(cameraUpdate);

    }

    //ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSION_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if(check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();

            }else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다. 2가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    makeToast("퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ");
                }
            }

        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        makeLog(new Object() {
                        }.getClass().getEnclosingMethod().getName() + "()", "GPS 활성화 되있음");
                        needRequest = true;

                        return;
                    }
                }
                break;
        }
    }


    /**
     *
     * 위치 권한 허용 관련 코드
     */



    //런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (checkPermission()) {
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "위치권한이 없습니다");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (googleMap!=null)
                googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mFusedLocationClient != null) {
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "LocationUpdates 중지 합니다.");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}