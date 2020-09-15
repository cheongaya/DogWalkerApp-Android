package com.example.dogwalker.walker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerDogwalkingDoneBinding;
import com.example.dogwalker.owner.DogListAdapter;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.PolylineOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class WalkerDogwalkingDoneActivity extends BaseActivity implements OnMapReadyCallback{

    private static final int  PICK_FROM_MULTI_ALBUM = 6001;

    ActivityWalkerDogwalkingDoneBinding binding;
    String walkingTime, walkingDistance, walkingPooCount;
    MultiAlbumAdapter multiAlbumAdapter;

    ArrayList<LatLng> latLngArrayList;  //이동한 경로 좌표 배열
    PolylineOverlay polylineOverlay;    //폴리라인 오버레이 객체

    @Nullable
    private LocationManager locationManager;
    FusedLocationSource locationSource;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_dogwalking_done);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_dogwalking_done);
        binding.setActivity(this);

        Intent intent = getIntent();
        //받아온 데이터 담기
        walkingTime = intent.getStringExtra("walkingTime");
        walkingDistance = intent.getStringExtra("walkingDistance");
        walkingPooCount = intent.getStringExtra("walkingPooCount");
        latLngArrayList = intent.getParcelableArrayListExtra("latLngArrayList");
        //화면에 데이터 표시
        binding.textViewWalkDoneWalkingTime.setText(walkingTime);
        binding.textViewWalkDoneDistance.setText(walkingDistance);
        binding.textViewWalkDonePooCount.setText(walkingPooCount);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        //폴리라인 오버레이 객체 생성
        polylineOverlay = new PolylineOverlay();

//        binding.editTextWalkDoneMemo.setText(latLngArrayList.toString());

        //NaverMap 객체 얻어오기
        FragmentManager fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.navermap_dogwalking_root);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.navermap_dogwalking_root, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
//        mapFragment.getMapAsync(naverMap -> map = naverMap);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //TODO : 위치트랙커 테스트중
        locationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerViewDogwalkingFileUpload.setLayoutManager(linearLayoutManager); //?? 주석??
        binding.recyclerViewDogwalkingFileUpload.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        multiAlbumAdapter = new MultiAlbumAdapter(this);
        binding.recyclerViewDogwalkingFileUpload.setAdapter(multiAlbumAdapter);
    }

    //버튼 클릭시 갤러리에서 이미지를 다중선택해 불러올 수 있다
    public void btnMultiAlbumAction(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //여러장을 선택할 수 있다
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_MULTI_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            //앨범에서 이미지 다중 선택
            case PICK_FROM_MULTI_ALBUM :

                ArrayList imageList = new ArrayList<>();

                // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함
                if (data.getClipData() == null) {
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "1. single choice : " +  String.valueOf(data.getData()));
                    imageList.add(String.valueOf(data.getData()));
                } else {

                    ClipData clipData = data.getClipData();
                    makeLog(new Object() {
                    }.getClass().getEnclosingMethod().getName() + "()", "1. ClipData 갯수 : " + String.valueOf(clipData.getItemCount()));

                    if (clipData.getItemCount() > 6) {
                        makeToast("사진은 6개까지 선택가능 합니다.");
                        return;
                    }
                    // 멀티 선택에서 하나만 선택했을 경우
                    else if (clipData.getItemCount() == 1) {
                        String dataStr = String.valueOf(clipData.getItemAt(0).getUri());
                        makeLog(new Object() {
                        }.getClass().getEnclosingMethod().getName() + "()", "2. clipdata choice : " + String.valueOf(clipData.getItemAt(0).getUri()));
                        makeLog(new Object() {
                        }.getClass().getEnclosingMethod().getName() + "()", "2. single choice : " + clipData.getItemAt(0).getUri().getPath());
                        imageList.add(dataStr);
                        //멀티 선택에서 하나 초과 ~ 6 이하로 선택했을 경우
                    } else if (clipData.getItemCount() > 1 && clipData.getItemCount() <= 6) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            //content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F35/ORIGINAL/NONE/61072326
                            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "2. single choice : " + String.valueOf(clipData.getItemAt(i).getUri()));
                            imageList.add(String.valueOf(clipData.getItemAt(i).getUri()));
                        }
                    }
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        //UiSettings 는 UI 와 관련된 설정을 담당하는 클래스이다
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        //TODO : 위치트랙커 테스트중
        naverMap.setLocationSource(locationSource);

        //위치 추적 모드 follow 지정 -> 위치 추적 활성화 / 현위치 오버레이와 카메라의 좌표가 사용자의 위치를 따라 움직인다
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        //좌표 배열 수 만큼 반복문을 돌려서 좌표값을 폴리라인에 추가
        if(latLngArrayList.size() >= 2){

            for(int i=0; i<latLngArrayList.size(); i++){

                double latitude = latLngArrayList.get(i).latitude;
                double longitude = latLngArrayList.get(i).longitude;

//            //좌표값을 폴리라인에 지정
//            polylineOverlay.setCoords(Arrays.asList(
//                    new LatLng(latLngArrayList.get(0).latitude, latLngArrayList.get(0).longitude),
//                    new LatLng(latLngArrayList.get(1).latitude, latLngArrayList.get(1).longitude),
//                    new LatLng(latLngArrayList.get(2).latitude, latLngArrayList.get(2).longitude)
//
//            ));
            }
            polylineOverlay.setCoords(latLngArrayList);
        }


        polylineOverlay.setMap(naverMap);
//        naverMap.

    }
}