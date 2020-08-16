package com.example.dogwalker;

import android.app.Application;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dogwalker.retrofit.RetroClient;
import com.example.dogwalker.retrofit.RetrofitAPI;
import com.example.dogwalker.retrofit2.RetrofitApi;
import com.example.dogwalker.retrofit2.RetrofitUtil;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.retrofit2.response.ResultStrDTO;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.dogwalker.BaseActivity.retrofitApi;

public class ApplicationClass extends Application {

    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    //쉐어드프리퍼런스 클래스 객체 생성
    public MySharedPref mySharedPref = MySharedPref.getInstance(this);

    //현재 로그인한 유저 정보
    public static String currentWalkerID;

    //서버에서 받아온 결과값
    String resultDataStr;

    //Retrofit 관련 변수
//    public RetroClient retroClient;

    @Override
    public void onCreate() {
        super.onCreate();

        //Retrofit 객체와 인터페이스를 연결
//        retroClient = RetroClient.getInstance(this).createBaseApi();

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

                makeToast("데이터 저장 성공 : "+resultServerStr);
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeToast("데이터 저장 실패");
            }
        });

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

