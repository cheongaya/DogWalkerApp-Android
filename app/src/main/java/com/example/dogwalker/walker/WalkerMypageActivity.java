package com.example.dogwalker.walker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.GpsTracker;
import com.example.dogwalker.owner.LocationWebViewActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.retrofit2.response.WalkerMypageDTO;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerMypageActivity extends WalkerBottomNavigation implements View.OnClickListener{

    private static final int PICK_FROM_ALBUM = 1001;
    private static final int PICK_FROM_CAMERA = 1002;
    private static final int WALKER_ADDRESS = 1003;     //주소검색 API
    private static final int WALKER_GPS = 1004;         //GPS (현재위치)
    private static final int WALKER_INTRODUCE = 1005;   //자기소개
    private static final int WALKER_WALKTYPE = 1006;    //산책가능유형
    private static final int WALKER_WALKPRICE = 1007;   //산책가격표

    //GPS 관련
    private GpsTracker gpsTracker;  //GpsTracker 클래스에서는 현재 위치를 가져와 주소로 변환하는 처리를 한다
    private static final int GPS_ENABLE_REQUEST_CODE = 1008;
    private static final int PERMISSIONS_REQUEST_CODE = 1009;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    //서버에서 받아온 결과값
    String getNameToServer;

    //xml 관련
    ImageView profileImg;
    ImageButton btnUserDataEdit, btnLocation, btnGPS;
    TextView tvLocation, tvName, tvReviewScore, tvSatisfationScore, tvCustomerScore;
    LinearLayout linearLocation, linearIntroduce, linearWalkableType, linearWalkPrice, linearCustomer, linearReview;
    File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_mypage);

        profileImg = (ImageView)findViewById(R.id.imageView_walker_profileImg);
//        btnLocation = (ImageButton)findViewById(R.id.imageButton_location);
        btnUserDataEdit = (ImageButton)findViewById(R.id.imageButton_edit_walker_data);
        btnGPS = (ImageButton)findViewById(R.id.imageButton_GPS);
        tvLocation = (TextView)findViewById(R.id.textView_location);
        tvName = (TextView)findViewById(R.id.textView_name);
        tvReviewScore = (TextView)findViewById(R.id.textView_walker_mypage_review_score);
        tvSatisfationScore = (TextView)findViewById(R.id.textView_walker_mypage_satisfation_score);
        tvCustomerScore = (TextView)findViewById(R.id.textView_walker_mypage_customer_score);
        linearLocation = (LinearLayout)findViewById(R.id.linearLayout_location);
        linearIntroduce = (LinearLayout)findViewById(R.id.linearLayout_introduce);
        linearWalkableType = (LinearLayout)findViewById(R.id.linearLayout_walkable_type);
        linearWalkPrice = (LinearLayout)findViewById(R.id.linearLayout_walk_price);
        linearCustomer = (LinearLayout)findViewById(R.id.linearLayout_walker_mypage_customer);
        linearReview = (LinearLayout)findViewById(R.id.linearLayout_walker_mypage_review);

//        profileImg.setOnClickListener(this);
        btnUserDataEdit.setOnClickListener(this);
        btnGPS.setOnClickListener(this);
        linearLocation.setOnClickListener(this);
        linearIntroduce.setOnClickListener(this);
        linearWalkableType.setOnClickListener(this);
        linearWalkPrice.setOnClickListener(this);
        linearCustomer.setOnClickListener(this);
        linearReview.setOnClickListener(this);

        //GPS 위치 권한 체크
        checkLocationGpsStatus();

        //DB에서 도그워커 데이터 불러오기 (아이디, 프로필이미지, 위치, 리뷰갯수, 평균만족도, 단골갯수)
        loadWalkerDataToDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //DB에 저장된 데이터 조회해서 setText() 해주기
//        applicationClass.loadData1ColumnToDB("user_walker", "name", "id", applicationClass.currentWalkerID, tvName);
//        applicationClass.loadData1ColumnToDB("user_walker", "location", "id", applicationClass.currentWalkerID, tvLocation);
    }

    //클릭 이벤트
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imageButton_edit_walker_data :
                //카메라&사진 권한 승인 후 사진 추가 다이얼로그 띄움
                tedPermission();
                break;
            case R.id.linearLayout_location :
                //다음 주소 검색 화면으로 전환
                Intent intentLocation = new Intent(this, LocationWebViewActivity.class);
                startActivityForResult(intentLocation, WALKER_ADDRESS);
                break;
            case R.id.imageButton_GPS :
                //현재 위치 좌표 받아오기
                getCurrentLocationGpsTracker();
                break;
            case R.id.linearLayout_introduce :
                //자기소개 팝업창 띄우기
                Intent intentIntroduce = new Intent(this, WalkerIntroduceActivity.class);
                startActivityForResult(intentIntroduce, WALKER_INTRODUCE);
                break;
            case R.id.linearLayout_walkable_type :
                //산책 가능 유형 팝업창 띄우기
                Intent intentWalkType = new Intent(this, WalkableTypeActivity.class);
                startActivityForResult(intentWalkType, WALKER_WALKTYPE);
                break;
            case R.id.linearLayout_walk_price :
                //가격표 팝업창 띄우기
                Intent intentWalkPrice = new Intent(this, WalkPriceActivity.class);
                startActivityForResult(intentWalkPrice, WALKER_WALKPRICE);
                break;
            case R.id.linearLayout_walker_mypage_customer :
                //단골 고객 리스트 화면 띄우기
                Intent intentCustomerList = new Intent(this, WalkerCustomerListActivity.class);
                startActivity(intentCustomerList);
                break;
            case R.id.linearLayout_walker_mypage_review:
                //리뷰+답글 리스트 화면 띄우기
                Intent intentReviewManage = new Intent(this, WalkerReviewManageActivity.class);
                startActivity(intentReviewManage);
                break;
        }

    }

    //DB에서 도그워커 데이터 불러오기
    public void loadWalkerDataToDB(){
        Call<WalkerMypageDTO> call = retrofitApi.selectWalkerMypageData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<WalkerMypageDTO>() {
            @Override
            public void onResponse(Call<WalkerMypageDTO> call, Response<WalkerMypageDTO> response) {
                WalkerMypageDTO walkerMypageDTO = response.body();
                //도그워커 데이터 화면에 표시
                tvName.setText(walkerMypageDTO.getId());
                tvLocation.setText(walkerMypageDTO.getLocation());
                tvReviewScore.setText(walkerMypageDTO.getReview_count()+"개");
                tvSatisfationScore.setText(walkerMypageDTO.getSatisfation_score()+"점");
                tvCustomerScore.setText(walkerMypageDTO.getCustomer_count()+"명");
                //반려인 사진 셋팅
                Glide.with(getApplicationContext())
                        .load(walkerMypageDTO.getProfile_img())
                        .override(300,300)
                        .apply(applicationClass.requestOptions.fitCenter().centerCrop())
                        .into(profileImg);
            }

            @Override
            public void onFailure(Call<WalkerMypageDTO> call, Throwable t) {

            }
        });

    }

    //현재위치 좌표값 받아오기
    public void getCurrentLocationGpsTracker(){
        //GpsTracker 객체 생성
        gpsTracker = new GpsTracker(WalkerMypageActivity.this);
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        tvLocation.setText(address);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "현재위치 주소 : " + address);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "현재위치 위도 : " + latitude);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "현재위치 경도 : " + longitude);
//        Toast.makeText(this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();

        //TODO: 위치 데이터 DB에 저장해야함 (밑에 주석 코드 활성화)
        //위치 데이터를 user_walker 테이블에 저장하는 메소드
//        if(){
//            updateLocationDataToDB(address);
//        }

        String latitudeStr = String.valueOf(latitude);
        String longitudeStr = String.valueOf(longitude);

        //위치 데이터를 user_walker 테이블에 저장하는 메소드
        updateLocationDataToDB(address, latitudeStr, longitudeStr);
    }

    //현재 위치 권한 체크
    public void checkLocationGpsStatus(){

        if(!checkLocationServicesStatus()){
            //권한 체크가 되어있지 않을시 다이얼로그 창 띄어줌
            showDialogForLocationServiceSetting();
        }else{
            //런타임 위치 퍼미션 처리
            checkRunTimeLocationPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case PICK_FROM_ALBUM :

                //앨범에서 getData Uri 받아온 후
                Uri photoUri = data.getData();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "photoUri : " + photoUri);
                //photoUri 값을 경로 변환 -> File 객체 생성해주는 메소드에 보내준다
                MultipartBody.Part body = applicationClass.updateAlbumImgToServer(photoUri, "uploaded_file");
                //위 메소드의 return 값은 return body(MultipartBody.Part) 형태로 반환된다
                Call<ResultDTO> call = retrofitApi.updateWalkerImageData(applicationClass.currentWalkerID, body);
                call.enqueue(new Callback<ResultDTO>() {
                    @Override
                    public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                        ResultDTO resultDTO = response.body();
                        String resultDataStr = resultDTO.getResponceResult();
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범이미지 서버 저장 성공");
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", resultDataStr);

                        //이미지 로딩 라이브러리 (반려인 프로필 사진 변경)
                        profileImg.setImageURI(data.getData());
                    }

                    @Override
                    public void onFailure(Call<ResultDTO> call, Throwable t) {
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범이미지 서버 저장 실패");
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
                    }
                });
                break;

            case PICK_FROM_CAMERA :
                break;

            case WALKER_GPS:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "현재위치 : GPS 활성화 되있음");
                        checkRunTimeLocationPermission();
                        return;
                    }
                }
                break;

            case WALKER_ADDRESS:  //주소 데이터 검색 팝업창 띄우기
                String resultAddress = data.getExtras().getString("addressResult");     //주소, 지명
                String resultLatitude = data.getExtras().getString("LatitudeResult");   //위도
                String resultLongitude = data.getExtras().getString("LongitudeResult"); //경도
                if(resultAddress != null){
                    tvLocation.setText(resultAddress);
                    //위치 데이터를 user_walker 테이블에 저장하는 메소드
                    updateLocationDataToDB(resultAddress, resultLatitude, resultLongitude);
                }
//                tvLocation.setText(resultAddress);
//                //위치 데이터를 user_walker 테이블에 저장하는 메소드
//                updateLocationDataToDB(resultAddress);
                break;

            case WALKER_INTRODUCE: //자기소개 데이터 등록 팝업창 띄우기
                String updateIntroduceData = data.getExtras().getString("updateIntroduceData");
                //자기소개 데이터를 user_walker 테이블에 저장하는 메소드
                updateIntroduceDataToDB(updateIntroduceData);
                break;

            case WALKER_WALKTYPE: //산책가능유형 데이터 등록 팝업창 띄우기
                Boolean checkedSizeSBol = data.getExtras().getBoolean("checkedSizeS");
                Boolean checkedSizeMBol = data.getExtras().getBoolean("checkedSizeM");
                Boolean checkedSizeLBol = data.getExtras().getBoolean("checkedSizeL");

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "bol 받아온 데이터 : " + checkedSizeSBol + " " + checkedSizeMBol + " " + checkedSizeLBol);

                int sizeS = bolIntoInt(checkedSizeSBol);
                int sizeM = bolIntoInt(checkedSizeMBol);
                int sizeL = bolIntoInt(checkedSizeLBol);

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "bol -> int 변환한 데이터 : " + sizeS + " " + sizeM + " " + sizeL);

                //산책가능유형 데이터를 user_walker 테이블에 저장하는 메소드
                updateWalkableTypeDataToDB(sizeS, sizeM, sizeL);
                break;

            case WALKER_WALKPRICE: //가격표 데이터 등록 팝업창 띄우기
                int priceInt01 =  data.getExtras().getInt("priceInt01");
                int priceInt02 =  data.getExtras().getInt("priceInt02");
                int priceInt03 =  data.getExtras().getInt("priceInt03");
                int priceInt04 =  data.getExtras().getInt("priceInt04");
                int priceInt05 =  data.getExtras().getInt("priceInt05");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()",
                        "int 받아온 데이터 : " + priceInt01 + " " + priceInt02 + " " + priceInt03 + " " + priceInt04 + " " + priceInt05);

                //가격표 데이터를 user_walker 테이블에 저장하는 메소드
                updateWalkPriceDataToDB(priceInt01, priceInt02, priceInt03, priceInt04, priceInt05);
                break;

            default:
                break;
        }
    }

    //boolean -> int 변환 (true = 1 & false = 0)
    public int bolIntoInt(Boolean checkedResult){

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "변환 : " + checkedResult.booleanValue());

        int changeInt = 0;

        if(checkedResult.booleanValue() == true){
            changeInt = 1;
        }else{
            changeInt = 0;
        }

        return changeInt;
    }

    //위치 데이터를 user_walker 테이블에 저장(수정)하는 메소드
    public void updateLocationDataToDB(String resultAddress, String latitude, String longitude){
        //데이터베이스에 내 위치 저장
        Call<ResultDTO> call = retrofitApi.updateWalkerLocation(applicationClass.currentWalkerID, resultAddress, latitude, longitude);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[1] 서버에 보내는 id : " + applicationClass.currentWalkerID);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[2] 서버에 보내는 location : " + resultAddress);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[3] 서버에 보내는 latitude : " + latitude);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[4] 서버에 보내는 longitude : " + longitude);
        //비동기 네트워크 처리
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultCodeStr = resultDTO.getResponceResult();

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "서버에서 받아온 결과 : " + resultCodeStr);

                if(resultCodeStr.contentEquals("ok")){
                    makeToast("위치가 추가되었습니다");
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeToast("다시 위치를 추가해주세요");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "위치 데이터 저장 실패 : " + t.toString());
            }
        });
    }

    //자기소개 데이터를 user_walker 테이블에 저장(수정)하는 메소드
    public void updateIntroduceDataToDB(String updateIntroduceStr){

        String tableName = "user_walker";
        String updateCol = "introduce";
        String updateVal = updateIntroduceStr;
        String primaryCol = "id";
        String primaryVal = applicationClass.currentWalkerID;

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("tableName", tableName);
        parameters.put("updateCol", updateCol);
        parameters.put("updateVal", updateVal);
        parameters.put("primaryCol", primaryCol);
        parameters.put("primaryVal", primaryVal);

        //데이터베이스에 내 위치 저장
        Call<ResultDTO> call = retrofitApi.updateWalkerData(parameters);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "서버에 보내는 id : " + primaryVal);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "서버에 보내는 수정할 데이터 : " + updateVal);
        //비동기 네트워크 처리
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultCodeStr = resultDTO.getResponceResult();

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "서버에서 받아온 결과 : " + resultCodeStr);

                if(resultCodeStr.contentEquals("ok")){
//                    makeToast("위치 데이터 저장 완료");

                    //이전 액티비티로 보내기
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeToast("위치 데이터 저장 실패");
            }
        });

    }

    //산책가능유형 데이터를 user_walker 테이블에 저장하는 메소드
    public void updateWalkableTypeDataToDB(int sizeS, int sizeM, int sizeL){

        String tableName = "user_walker";
        String updateCol1 = "walkable_type_s";
        int updateVal1 = sizeS;
        String updateCol2 = "walkable_type_m";
        int updateVal2 = sizeM;
        String updateCol3 = "walkable_type_l";
        int updateVal3 = sizeL;
        String primaryCol = "id";
        String primaryVal = applicationClass.currentWalkerID;

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("tableName", tableName);
        parameters.put("updateCol1", updateCol1);
        parameters.put("updateVal1", updateVal1);
        parameters.put("updateCol2", updateCol2);
        parameters.put("updateVal2", updateVal2);
        parameters.put("updateCol3", updateCol3);
        parameters.put("updateVal3", updateVal3);
        parameters.put("primaryCol", primaryCol);
        parameters.put("primaryVal", primaryVal);

        Call<ResultDTO> call = retrofitApi.updateWalkerData3Column(parameters);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();

                if(resultDataStr.contentEquals("ok")){
//                    makeToast("데이터 저장 성공");
                }else{
//                    makeToast("데이터 저장 실패");
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {

            }
        });

    }

    //가격표 데이터를 user_walker 테이블에 저장하는 메소드
    public void updateWalkPriceDataToDB(int priceInt01, int priceInt02, int priceInt03, int priceInt04, int priceInt05){

        int updateVal1 = priceInt01;
        int updateVal2 = priceInt02;
        int updateVal3 = priceInt03;
        int updateVal4 = priceInt04;
        int updateVal5 = priceInt05;
        String primaryVal = applicationClass.currentWalkerID;

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("updateVal1", updateVal1);
        parameters.put("updateVal2", updateVal2);
        parameters.put("updateVal3", updateVal3);
        parameters.put("updateVal4", updateVal4);
        parameters.put("updateVal5", updateVal5);
        parameters.put("primaryVal", primaryVal);

        Call<ResultDTO> call = retrofitApi.updateWalkPrice(parameters);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();

                if(resultDataStr.contentEquals("ok")){
//                    makeToast("가격 데이터 저장 성공");
                }else{
//                    makeToast("가격 데이터 저장 실패");
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {

            }
        });
    }


    /**
     * 카메라 관련 코드 + 카메라 관련 권한 허용 코드
     */

    //카메라&앨범에 관한 권한 허용을 사용자로부터 받는 메소드
    public void tedPermission(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //권한 요청 성공
//                makeToast("권한 요청 성공");

                //사진 추가 다이얼로그 띄우기
                addImgAlertDialog();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //권한 요청 실패
//                makeToast("권한 요청 실패");
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    //사진 추가할때 카메라 or 앨범 선택 다이얼로그
    public void addImgAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("프로필 사진")
                .setMessage("프로필 사진 추가하기");

        builder.setPositiveButton("앨범", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //앨범에서 사진 가져오기
                getImageFromAlbum();
            }
        }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setNegativeButton("카메라", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //카메라에서 사진 가져오기
//                getImageFromCamera();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //앨범에서 이미지 가져오는 메소드
    public void getImageFromAlbum(){
        //인텐트를 통해 앨범 화면으로 이동시켜줌
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        intent.setAction(Intent.ACTION_GET_CONTENT);    //추가 코드
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

//    //카메라에서 촬영으로 이미지 가져오는 메소드
//    public void getImageFromCamera(){
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(profileIconFile));
//        startActivityForResult(intent, PICK_FROM_CAMERA);
//    }

    //전역변수 tempFile 경로를 불러와 bitmap 파일로 변형한 후 imageView 에 해당 이미지를 넣어준다
    public void setImage(){

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        profileImg.setImageBitmap(bitmap);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범 이미지 bitmap : " + bitmap);
    }

    /**
     * 현재위치 관련 코드 + 현재위치 관련 권한 허용 코드
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */

    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
//            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "Geocoder addresses : " + addresses);
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
        }

        Address address = addresses.get(0);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "Geocoder address : " + addresses);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "Geocoder address.getAddressLine(0) : " + address.getAddressLine(0));
        return address.getAddressLine(0).toString()+"\n";
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(WalkerMypageActivity.this);
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

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    makeToast("퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.");
                    finish();
                }else {
                    makeToast("퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ");

                }
            }

        }
    }

    void checkRunTimeLocationPermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(WalkerMypageActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(WalkerMypageActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            // 3.  위치 값을 가져올 수 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(WalkerMypageActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                makeToast("이 앱을 실행하려면 위치 접근 권한이 필요합니다.");
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(WalkerMypageActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(WalkerMypageActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_walker_mypage;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavWorker04;
    }

}

