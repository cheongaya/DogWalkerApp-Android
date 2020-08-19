package com.example.dogwalker.owner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityOwnerAddDogBinding;
import com.example.dogwalker.retrofit2.RetrofitApi;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

public class OwnerAddDogActivity extends BaseActivity {

    private static final int PICK_FROM_DOG_IMG_ALBUM = 3001;
    private static final int PICK_FROM_DOG_IMG_CAMERA = 3002;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ActivityOwnerAddDogBinding binding;
    String dogNameStr, dogSexStr, dogSizeStr, dogNeuterStr, dogKindStr = "";

    Intent intent;
    public static MultipartBody.Part body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_add_dog);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_owner_add_dog);
        binding.setActivity(this);

        intent = getIntent();

        //1번에서 생성한 field.xml의 item을 String 배열로 가져오기
        String[] models = getResources().getStringArray(R.array.spinnerArray);

        //2번에서 생성한 spinner_item.xml과 str을 인자로 어댑터 생성.
        final ArrayAdapter<String> adapter= new ArrayAdapter<String>(getBaseContext(), R.layout.spinner_item, models);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.spinnerDogType.setAdapter(adapter);

        //spinner 이벤트 리스너
        binding.spinnerDogType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(binding.spinnerDogType.getSelectedItemPosition() > 0){
                    //선택된 항목
                    Log.v("알림",binding.spinnerDogType.getSelectedItem().toString()+ "is selected");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    //강아지 등록하기 버튼 클릭시 -> 강아지 데이터 정보 DB에 저장
    public void onSaveMyDog(View view){

        dogNameStr = binding.editTextMydogName.getText().toString();
        int dogBirthdayYear = Integer.parseInt(binding.editTextMydogBirthdayYear.getText().toString());
        int dogBirthdayMonth = Integer.parseInt(binding.editTextMydogBirthdayMonth.getText().toString());
        int dogBirthdayDay = Integer.parseInt(binding.editTextMydogBirthdayDay.getText().toString());
        String dogWeightStr = binding.editTextMydogWeight.getText().toString();
        String dogNumberStr = binding.editTextMydogNumber.getText().toString();

        //라디오 체크버튼 값 가져오기
        getRadioCheckedData();

        //데이터 저장
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("owner_id", applicationClass.currentWalkerID);
//        parameters.put("profile_img", "null");
        parameters.put("name", dogNameStr);
        parameters.put("birthday_year", dogBirthdayYear);
        parameters.put("birthday_month", dogBirthdayMonth);
        parameters.put("birthday_day", dogBirthdayDay);
        parameters.put("sex", dogSexStr);
        parameters.put("size", dogSizeStr);
        parameters.put("weight", dogWeightStr);
        parameters.put("neuter", dogNeuterStr);
        parameters.put("kind", dogKindStr);
        parameters.put("number", dogNumberStr);

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "dogSexStr : " + dogSexStr);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "dogSizeStr : " + dogSizeStr);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "dogNeuterStr : " + dogNeuterStr);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "dogKindStr : " + dogKindStr);

        Call<ResultDTO> call = retrofitApi.insertDogData(parameters);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "강아지정보저장 성공 : " + resultDataStr);
                //그 전 액티비티로 보내기
                if(resultDataStr.contentEquals("ok")){
//                    setResult(RESULT_OK, intent);
//                    finish();
                    saveDogImg(dogNameStr);
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "강아지정보저장 실패");
            }
        });

    }

    public void saveDogImg(String dogName){
        //이미지 데이터 값 가져오기
        //위 메소드의 return 값은 return body(MultipartBody.Part) 형태로 반환된다
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "참조할 강아지 이름 : "+dogName);
        Call<ResultDTO> callImg = retrofitApi.updateDogImageData(dogName, body);
        callImg.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범이미지 서버 저장 성공");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", resultDataStr);

                setResult(RESULT_OK, intent);
                finish();

            }
            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범이미지 서버 저장 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
            }
        });
    }

    //라디오 체크버튼 값 가져오기
    public void getRadioCheckedData(){

        //라디오 체크버튼
        Boolean checkMale = binding.radioButtonMydogMale.isChecked();
        Boolean checkFemale = binding.radioButtonMydogFemale.isChecked();
        Boolean checkSizeS = binding.radioButtonMydogSizeS.isChecked();
        Boolean checkSizeM = binding.radioButtonMydogSizeM.isChecked();
        Boolean checkSizeL = binding.radioButtonMydogSizeL.isChecked();
        Boolean checkNeuterAfter = binding.radioButtonMydogNeuterAfter.isChecked();
        Boolean checkNeuterBefore = binding.radioButtonMydogNeuterBefore.isChecked();
        Boolean checkKindBreed = binding.radioButtonMydogKindBreed.isChecked();
        Boolean checkKindMix = binding.radioButtonMydogKindMix.isChecked();
        Boolean checkKindNo = binding.radioButtonMydogKindNo.isChecked();

        if(checkMale.equals(true)){
            dogSexStr = "male";
        }else if(checkFemale.equals(true)){
            dogSexStr = "female";
        }
        if(checkSizeS.equals(true)){
            dogSizeStr = "S";
        }else if(checkSizeM.equals(true)){
            dogSizeStr = "M";
        }else if(checkSizeL.equals(true)){
            dogSizeStr = "L";
        }
        if(checkNeuterAfter.equals(true)){
            dogNeuterStr = "after";
        }else if(checkNeuterBefore.equals(true)){
            dogNeuterStr = "before";
        }
        if(checkKindBreed.equals(true)){
            dogKindStr = "poodle";
        }else if(checkKindMix.equals(true)){
            dogKindStr = "mix";
        }else if(checkKindNo.equals(true)){
            dogKindStr = "no";
        }
    }

    /**
     * 강아지 이미지 추가 코드
     */

    public void onAddDogImg(View view){
        //사진 권한 요청 후 -> 사진 or 카메라 선택 다이얼로그 창 띄어줌
        tedPermission();
//        addImgAlertDialog();
    }

    //사진 추가할때 카메라 or 앨범 선택 다이얼로그
    public void addImgAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("강아지 프로필")
                .setMessage("강아지 사진 추가하기");

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case PICK_FROM_DOG_IMG_ALBUM:

                //앨범에서 getData Uri 받아온 후
                Uri photoUri = data.getData();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "photoUri : " + photoUri);
                //photoUri 값을 경로 변환 -> File 객체 생성해주는 메소드에 보내준다
                body = applicationClass.updateAlbumImgToServer(photoUri);

                //이미지 셋팅
                binding.imageViewMydogAddImg.setImageURI(data.getData());

//                //위 메소드의 return 값은 return body(MultipartBody.Part) 형태로 반환된다
//                Call<ResultDTO> call = retrofitApi.updateDogImageData(body, applicationClass.currentWalkerID);
//                call.enqueue(new Callback<ResultDTO>() {
//                    @Override
//                    public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
//                        ResultDTO resultDTO = response.body();
//                        String resultDataStr = resultDTO.getResponceResult();
//                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범이미지 서버 저장 성공");
//                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", resultDataStr);
//
//                    }
//                    @Override
//                    public void onFailure(Call<ResultDTO> call, Throwable t) {
//                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범이미지 서버 저장 실패");
//                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
//                    }
//                });

                break;

            case PICK_FROM_DOG_IMG_CAMERA:
                break;

            default:
                break;
        }
    }

    //앨범에서 이미지 가져오는 메소드
    public void getImageFromAlbum(){
        //인텐트를 통해 앨범 화면으로 이동시켜줌
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        intent.setType("image/-");
//        intent.setAction(Intent.ACTION_GET_CONTENT);    //추가 코드
        startActivityForResult(intent, PICK_FROM_DOG_IMG_ALBUM);
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

}