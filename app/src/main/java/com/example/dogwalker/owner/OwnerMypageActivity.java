package com.example.dogwalker.owner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.retrofit2.response.UserOwnerDTO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerMypageActivity extends OwnerBottomNavigation implements View.OnClickListener {

    private static final int OWNER_ADD_DOG = 2001;
    private static final int PICK_FROM_OWNER_IMG_ALBUM = 2002;
    private static final int PICK_FROM_OWNER_IMG_CAMERA = 2003;

    ImageView imgOwnerProgile, imgMyDogProfile, imgDogDataNull;
    TextView tvOwnerName, tvMyDogName, tvMyDogSex, tvMyDogAge, tvMyDogType, tvMyDogWeight, tvMyDogKine, tvMyDogNumber;
    RecyclerView recyclerViewMyDog;
    FloatingActionButton btnFloatAddMyDog;
    LinearLayout linearDogDataNull;
    ConstraintLayout constraintDogDataIs;
    ImageButton btnMyDogDataDelete, btnOwnerDataEdit;

    DogListAdapter dogListAdapter;
    List<DogDTO> dogDTOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_mypage);

        imgOwnerProgile = (ImageView)findViewById(R.id.imageView_profileImg);
        imgMyDogProfile = (ImageView)findViewById(R.id.imageView_mydog_img);
        imgDogDataNull = (ImageView)findViewById(R.id.imageView_mydog_data_null);
        linearDogDataNull = (LinearLayout)findViewById(R.id.linearLayout_myDog_data_null);
        constraintDogDataIs = (ConstraintLayout)findViewById(R.id.constraintLayout_myDog_info);
        tvOwnerName = (TextView)findViewById(R.id.textView_name);
        tvMyDogName = (TextView)findViewById(R.id.textView_mydog_name);
        tvMyDogSex = (TextView)findViewById(R.id.textView_mydog_sex);
        tvMyDogAge = (TextView)findViewById(R.id.textView_mydog_age);
        tvMyDogType = (TextView)findViewById(R.id.textView_mydog_type);
        tvMyDogWeight = (TextView)findViewById(R.id.textView_mydog_weight);
        tvMyDogKine = (TextView)findViewById(R.id.textView_mydog_kind);
        tvMyDogNumber = (TextView)findViewById(R.id.textView_mydog_number);
        btnFloatAddMyDog = (FloatingActionButton)findViewById(R.id.button_floating_add_mydog);
        btnMyDogDataDelete = (ImageButton)findViewById(R.id.imageView_mydog_delete);
        btnOwnerDataEdit = (ImageButton)findViewById(R.id.imageButton_edit_owner_data);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        //DB에서 반려인 데이터 불러오기
        loadOwnerDataToDB();
        //DB에서 강아지 데이터 불러오기
        loadMyDogDataToDB();

        //클릭 리스너 이벤트
        btnFloatAddMyDog.setOnClickListener(this);
        btnMyDogDataDelete.setOnClickListener(this);
        btnOwnerDataEdit.setOnClickListener(this);

        //DB에 저장된 데이터 조회해서 setText() 해주기
//        applicationClass.loadData1ColumnToDB("user_owner", "name", "id", applicationClass.currentWalkerID, tvOwnerName);

        //아이템 클릭 리스너
        dogListAdapter.setOnItemClickListener(new DogListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //클릭했을때 처리
//                makeToast("aaa");
                myDogDataSetText(position);
            }
        });
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){
        recyclerViewMyDog =  (RecyclerView)findViewById(R.id.recyclerView_myDog);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMyDog.setLayoutManager(linearLayoutManager); //?? 주석??
        recyclerViewMyDog.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        dogListAdapter = new DogListAdapter(this);
        recyclerViewMyDog.setAdapter(dogListAdapter);
    }

    //DB에서 반려인 데이터 불러오기
    public void loadOwnerDataToDB(){
        Call<List<UserOwnerDTO>> call = retrofitApi.selectOwnerData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<List<UserOwnerDTO>>() {
            @Override
            public void onResponse(Call<List<UserOwnerDTO>> call, Response<List<UserOwnerDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "반려인 데이터 조회 성공");
                List<UserOwnerDTO> userOwnerDTOList = response.body();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "반려인 이름 : "+userOwnerDTOList.get(0).getName());
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "반려인 이미지 : "+userOwnerDTOList.get(0).getProfile_img());
                //반려인 이름 셋팅
                tvOwnerName.setText(userOwnerDTOList.get(0).getName());
                //반려인 사진 셋팅
                Glide.with(getApplicationContext())
                        .load(userOwnerDTOList.get(0).getProfile_img())
                        .override(300,300)
                        .apply(applicationClass.requestOptions.fitCenter().centerCrop())
                        .into(imgOwnerProgile);
            }

            @Override
            public void onFailure(Call<List<UserOwnerDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "반려인 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());

            }
        });

    }

    //DB에서 강아지 데이터 불러오기
    public void loadMyDogDataToDB(){
        Call<List<DogDTO>> call = retrofitApi.selectDogData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<List<DogDTO>>() {
            @Override
            public void onResponse(Call<List<DogDTO>> call, Response<List<DogDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "강아지 데이터 조회 성공");
                dogDTOList = response.body();

                //강아지 데이터 setText 해주기 (position 번째)
                if(dogDTOList.size() > 0){

                    //데이터 NULL 아니면
                    imgDogDataNull.setVisibility(View.GONE); //안보이게
                    linearDogDataNull.setVisibility(View.GONE); //안보이게
                    recyclerViewMyDog.setVisibility(View.VISIBLE); //보이게
                    constraintDogDataIs.setVisibility(View.VISIBLE); //보이게

                    ArrayList<DogDTO> arrayList = new ArrayList<DogDTO>();
                    arrayList.addAll(dogDTOList);

                    dogListAdapter.setDogDTOArrayList(arrayList);
                    dogListAdapter.notifyDataSetChanged();
                    //강아지 데이터 View 에 셋팅하기
                    myDogDataSetText(0);

                }else{
                    //데이터 NULL 이면
                    imgDogDataNull.setVisibility(View.VISIBLE); //보이게
                    linearDogDataNull.setVisibility(View.VISIBLE); //보이게
                    recyclerViewMyDog.setVisibility(View.GONE); //안보이게
                    constraintDogDataIs.setVisibility(View.GONE); //안보이게
                }

            }

            @Override
            public void onFailure(Call<List<DogDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "강아지 데이터 조회 실패");

            }
        });
    }

    //강아지 데이터 setText 해주기 (position 번째)
    public void myDogDataSetText(int position){
        //첫번째 강아지 data 정보 setText 해주기
        tvMyDogName.setText(dogDTOList.get(position).getName());
        if(dogDTOList.get(position).getSex().contentEquals("male")){
            tvMyDogSex.setText("남");
        }else{
            tvMyDogSex.setText("여");
        }
        //TODO: 개월수 날짜계산 해야함
        tvMyDogAge.setText(dogDTOList.get(position).getBirthday_month()+"월생");
        if(dogDTOList.get(position).getSize().contentEquals("S")){
            tvMyDogType.setText("소형");
        }else if(dogDTOList.get(position).getSize().contentEquals("M")){
            tvMyDogType.setText("중형");
        }else{
            tvMyDogType.setText("대형");
        }
        tvMyDogKine.setText(dogDTOList.get(position).getKind());
        tvMyDogWeight.setText(dogDTOList.get(position).getWeight()+" kg");
        tvMyDogNumber.setText(dogDTOList.get(position).getNumber());
        //반려견 프로필 이미지 셋팅
        Glide.with(getApplicationContext())
                .load(dogDTOList.get(position).getProfile_img())
                .override(300,300)
                .apply(applicationClass.requestOptions.fitCenter().circleCrop())
                .into(imgMyDogProfile);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.button_floating_add_mydog :
                //강아지 추가하기
                Intent intentAddDog = new Intent(this, OwnerAddDogActivity.class);
                startActivityForResult(intentAddDog, OWNER_ADD_DOG);
                break;

            case R.id.imageView_mydog_delete:
                //강아지 삭제 여부 묻는 다이얼로그 창 띄우기
                deleteDogDataDialog();
                break;

            case R.id.imageButton_edit_owner_data :
                //강아지 이미지 수정하는 다이얼로그 창 띄우기
                //사진 권한 요청 후 -> 사진 or 카메라 선택 다이얼로그 창 띄어줌
                tedPermission();
                break;

            default:
                break;
        }
    }

    //펫정보 삭제 다이얼로그
    public void deleteDogDataDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("펫 삭제")
                .setMessage("펫 정보를 삭제하시겠습니까?");

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DB 에서 강아지 데이터 삭제
                deleteMyDogDataToDB();
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //DB 에서 강아지 데이터 삭제하기
    public void deleteMyDogDataToDB(){

        String dogName = tvMyDogName.getText().toString();
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "삭제할 강아지 이름 : " + dogName);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "삭제할 로그인유저 이름 : " + applicationClass.currentWalkerID);

        Call<ResultDTO> call = retrofitApi.deleteDogData(applicationClass.currentWalkerID, dogName);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();

                if(resultDataStr.contentEquals("ok")){
                    //다시 디비에서 강아지데이터 리로드 시키기
                    loadMyDogDataToDB();
                    makeToast("펫 삭제 완료");
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case OWNER_ADD_DOG :
                //DB 에서 강아지 데이터 다시 로드하기
                loadMyDogDataToDB();
                break;

            case PICK_FROM_OWNER_IMG_CAMERA :
                break;

            case PICK_FROM_OWNER_IMG_ALBUM :

                //앨범에서 getData Uri 받아온 후
                Uri photoUri = data.getData();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "photoUri : " + photoUri);
                //photoUri 값을 경로 변환 -> File 객체 생성해주는 메소드에 보내준다
                MultipartBody.Part body = applicationClass.updateAlbumImgToServer(photoUri);
                //위 메소드의 return 값은 return body(MultipartBody.Part) 형태로 반환된다
                Call<ResultDTO> call = retrofitApi.updateOwnerImageData(applicationClass.currentWalkerID, body);
                call.enqueue(new Callback<ResultDTO>() {
                    @Override
                    public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                        ResultDTO resultDTO = response.body();
                        String resultDataStr = resultDTO.getResponceResult();
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범이미지 서버 저장 성공");
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", resultDataStr);

                        //이미지 로딩 라이브러리 (반려인 프로필 사진 변경)
                        Glide.with(getApplicationContext())
                                .load(resultDataStr)
                                .override(300,300)
                                .apply(applicationClass.requestOptions.fitCenter().centerCrop())
                                .into(imgOwnerProgile);

                    }

                    @Override
                    public void onFailure(Call<ResultDTO> call, Throwable t) {
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범이미지 서버 저장 실패");
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
                    }
                });
                break;

            default:
                break;
        }
    }

    //카메라&앨범에 관한 권한 허용을 사용자로부터 받는 메소드
    public void tedPermission(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //권한 요청 성공
                makeToast("권한 요청 성공");
                //사진 추가 다이얼로그 띄우기
                addImgAlertDialog();
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //권한 요청 실패
                makeToast("권한 요청 실패");
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
                .setMessage("프로필 사진 수정하기");

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
//        intent.setType("image/-");
//        intent.setAction(Intent.ACTION_GET_CONTENT);    //추가 코드
        startActivityForResult(intent, PICK_FROM_OWNER_IMG_ALBUM);
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_owner_mypage;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavOwner04;
    }


}