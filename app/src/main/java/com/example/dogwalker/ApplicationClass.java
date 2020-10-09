package com.example.dogwalker;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.dogwalker.BaseActivity.retrofitApi;

public class ApplicationClass extends Application {

    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    public static String BASE_URL = "http://52.78.138.74/";

    //쉐어드프리퍼런스 클래스 객체 생성
    public MySharedPref mySharedPref = MySharedPref.getInstance(this);

    //현재 로그인한 유저 정보
    public static String currentWalkerID;

    //서버에서 받아온 결과값
    String resultDataStr;

    //이미지 로딩 라이브러리 Glide 관련 객체
    public static RequestOptions requestOptions;

    @Override
    public void onCreate() {
        super.onCreate();

        //Retrofit 객체와 인터페이스를 연결
//        retroClient = RetroClient.getInstance(this).createBaseApi();

        //Glide 옵션
        requestOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_perm_identity_24)
                .error(R.drawable.ic_baseline_perm_identity_24)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

    }

    //DB에 저장된 칼럼 1개 데이터 불러와서 setText() 하는 메소드
    public String loadData1ColumnToDB(String tableName, String selectCol, String primaryCol, String primaryVal, TextView textView){
//
//        String tableName = "user_walker";
//        String selectCol = "introduce";
//        String primaryCol = "id";
//        String primaryVal = applicationClass.currentWalkerID;

        Call<ResultDTO> call = retrofitApi.selectWalkerData1Column(tableName, selectCol, primaryCol, primaryVal);

        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                resultDataStr = resultDTO.getResponceResult();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[1] 데이터 조회 selectCol("+selectCol+") : " + resultDataStr);;

                textView.setText(resultDataStr);
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[2] 조회한 데이터 setText() : " + resultDataStr);;
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {

                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "데이터조회 실패");

            }
        });

        return resultDataStr;
    }

    //DB에 칼럼3개 데이터 저장하는 메소드
    public void insertData3ColumnToDB(String tableName, String insertCol1, String insertVal1, String insertCol2, String insertVal2, String insertCol3, String insertVal3){

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("tableName", tableName);
        parameters.put("insertCol1", insertCol1);
        parameters.put("insertVal1", insertVal1);
        parameters.put("insertCol2", insertCol2);
        parameters.put("insertVal2", insertVal2);
        parameters.put("insertCol3", insertCol3);
        parameters.put("insertVal3", insertVal3);

        Call<ResultDTO> call = retrofitApi.insertWalkerData3Column(parameters);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultServerStr = resultDTO.getResponceResult();

                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "데이터 저장 성공 : " + resultServerStr);
//                makeToast("데이터 저장 성공 : "+resultServerStr);
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
//                makeToast("데이터 저장 실패");
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "데이터 저장 실패 : "+t.toString());
            }
        });

    }

    //앨범에서 불러온 이미지 데이터 -> file 형태로 변환
    public File changeToFile(Uri photoUri){

        File tempFile;

        Cursor cursor = null;
        try {
            /*
             *  Uri 스키마를
             *  content:/// 에서 file:/// 로  변경한다.
             */
            String[] proj = { MediaStore.Images.Media.DATA};
            assert photoUri != null;
            cursor = getContentResolver().query(photoUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            tempFile = new File(cursor.getString(column_index));
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "tempFile : " + tempFile);
            //_tempFile : /storage/emulated/0/Download/ST_20181108_BUZZ083DPE_4397649.jpg
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        //파일 생성 img_url은 이미지의 경로
        File file = new File(String.valueOf(tempFile));

        return file;
    }

    //앨범에서 불러온 이미지 데이터를 서버에 저장하는 메소드
    public MultipartBody.Part updateAlbumImgToServer(Uri photoUri, String fileName){
        //_photoUri : content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F36/ORIGINAL/NONE/929063286

        File tempFile;

        Cursor cursor = null;
        try {
            /*
             *  Uri 스키마를
             *  content:/// 에서 file:/// 로  변경한다.
             */
            String[] proj = { MediaStore.Images.Media.DATA};
            assert photoUri != null;
            cursor = getContentResolver().query(photoUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            tempFile = new File(cursor.getString(column_index));
            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "tempFile : " + tempFile);
            //_tempFile : /storage/emulated/0/Download/ST_20181108_BUZZ083DPE_4397649.jpg
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        //파일 생성
        //img_url은 이미지의 경로
        File file = new File(String.valueOf(tempFile));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        RequestBody requestFile2 = RequestBody.create(MediaType.parse("text/"), strvar);
        //Multipart는 HTTP를 통해 File을 SERVER 로 전송하기위해 사용되는 Content-type  이다
        //multipart/form-data는 파일 업로드가 있는 양식요소에 사용되는 enctype 속성의 값중 하나
        //폼이 제출될 떄 이 형식이 encType="multipart/form-data" 라는 것을 알려준다
        MultipartBody.Part body = MultipartBody.Part.createFormData(fileName, file.getName(), requestFile);
        //name : "uploaded_file"

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "file.getName() : "+file.getName());
        //_file.getName() : ST_20181108_BUZZ083DPE_4397649.jpg
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "requestFile.contentType() : "+requestFile.contentType());
        //_requestFile.contentType() : multipart/form-data
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "uploadFile.body() : "+body.body());
        //_uploadFile.body() : okhttp3.RequestBody$Companion$asRequestBody$1@ebaa241

        return body;
    }

    //로그 : 액티비티명 + 함수명 + 원하는 데이터를 한번에 보기위한 로그
    public void makeLog(String methodData, String strData) {
        Log.d(TAG, className + "_" + methodData + "_" + strData);
    }

    //토스트메세지 : 귀찮음을 없애기 위해 토스트를 띄우는 함수를 만듦
    public void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


}
