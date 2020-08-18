package com.example.dogwalker.owner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityOwnerAddDogBinding;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerAddDogActivity extends BaseActivity {

    private static final int PICK_FROM_DOG_IMG_ALBUM = 3001;
    private static final int PICK_FROM_DOG_IMG_CAMERA = 3002;

    private ActivityOwnerAddDogBinding binding;
    String dogSexStr, dogSizeStr, dogNeuterStr, dogKindStr = "";

    Intent intent;

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

        String dogNameStr = binding.editTextMydogName.getText().toString();
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
        parameters.put("profile_img", "null");
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
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "강아지정보저장 실패");
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

//                File tempFile;

                Uri photoUri = data.getData();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "photoUri : " + photoUri);
                Cursor cursor = null;
                try {
                    /*
                     *  Uri 스키마를
                     *  content:/// 에서 file:/// 로  변경한다.
                     */
                    String[] proj = { MediaStore.Images.Media.DATA };
                    assert photoUri != null;
                    cursor = getContentResolver().query(photoUri, proj, null, null, null);
                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    File tempFile = new File(cursor.getString(column_index));
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "tempFile : " + tempFile);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }


//                Uri img_url  = data.getData();;    //TODO: 레트로핏 이미지 업로드 테스트중

//                //파일 생성
//                //img_url은 이미지의 경로
//                File file = new File(img_url);
//                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//                MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);
//
//                Call<ResultDTO> call = retrofitApi.insertImage(body);
//                call.enqueue(new Callback<ResultDTO>() {
//                    @Override
//                    public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
//                        ResultDTO resultDTO = response.body();
//                        String resultDataStr = resultDTO.getResponceResult();
//                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범이미지 서버 저장 성공");
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResultDTO> call, Throwable t) {
//                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범이미지 서버 저장 실패");
//                    }
//                });

//                try {
////                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
////                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범 data.getData() : " + data.getData());
////
////                    Bitmap bitmapImg = BitmapFactory.decodeStream(inputStream);
////                    inputStream.close();
////
////                    binding.imageViewMydogAddImg.setImageBitmap(bitmapImg);
////                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범 bitmapImg : " + bitmapImg);
//
//                    Bitmap bitmapImg = MediaStore.Images.Media.getBitmap( getContentResolver(), data.getData());
//
//                    //비트맵 이미지 리사이징
//                    bitmapImg = resize(bitmapImg);
//                    //비트맵 인코딩
//                    BitMapToString(bitmapImg);
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                break;

            case PICK_FROM_DOG_IMG_CAMERA:
                break;

            default:
                break;
        }
    }

    //비트맵 이미지 리사이징
    private Bitmap resize(Bitmap bm){
        Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp>=800)
            bm = Bitmap.createScaledBitmap(bm, 400, 240, true);
        else if(config.smallestScreenWidthDp>=600)
            bm = Bitmap.createScaledBitmap(bm, 300, 180, true);
        else if(config.smallestScreenWidthDp>=400)
            bm = Bitmap.createScaledBitmap(bm, 200, 120, true);
        else if(config.smallestScreenWidthDp>=360)
            bm = Bitmap.createScaledBitmap(bm, 180, 108, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);
        return bm;
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

    //bitmap 이미지를 인코딩 하는 코드
    public void BitMapToString(Bitmap bitmap){
        //[1] bitmapImage -> byte array 형태로 변환
        ByteArrayOutputStream byteArrayOutputStream = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);    //bitmap compress //JPEG
        //
//        String image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
//        String name = String.valueOf(Calendar.getInstance().getTimeInMillis());
        //
        byte [] arr = byteArrayOutputStream.toByteArray();
        //[2] 변환된 데이터를 -> Bse64 로 String 형태 인코딩 함
        String image= Base64.encodeToString(arr, Base64.DEFAULT);
        String temp="";
        try{
            //[3] 인코딩된 데이터를 UTF-8 로 다시 인코딩 ([2] 데이터를 보내야하는데 중간에 공백이 있어서 HTTP GET 방식으로 보낼 수 없음)
            temp="&imagedevice="+ URLEncoder.encode(image,"utf-8");
        }catch (Exception e){
            Log.e("exception",e.toString());
        }
        //DB에 업로드하는 코드 추가
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범 image : " + image);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "앨범 temp : " + temp);

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