package com.example.dogwalker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.dogwalker.retrofit2.response.ResultDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationWebViewActivity extends BaseActivity {

    private WebView webView;
//    private TextView tvResult;
    private Handler handler;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_web_view);

        webView = (WebView)findViewById(R.id.webView_location);
//        tvResult = (TextView)findViewById(R.id.textView_result);

        intent = getIntent();

        //WebView 초기화
        initWebView();

        //핸들러를 통한 javascript 이벤트 반응
        handler = new Handler();
    }

    //웹뷰 초기화
//    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(){
        //javascript 허용
        webView.getSettings().setJavaScriptEnabled(true);
        //javascript 의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //javascript 이벤트에 대응할 함수를 정의 한 클래스를 붙여준다
        webView.addJavascriptInterface(new AndroidBridge(), "TestApp");
        //web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());
        //webview url load.php 파일 주소
        webView.loadUrl("http://13.125.0.82/walker/daum_address.php");
    }

    private class AndroidBridge{
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3){
            handler.post(new Runnable() {
                @Override
                public void run() {
//                    tvResult.setText(String.format("(%s) %s %s", arg1, arg2, arg3));

                    String addressResultStr = String.format("(%s) %s %s", arg1, arg2, arg3);
                    //WebView를 초기화 하지 않으면 재사용할 수 업음
//                    initWebView();

//                    //데이터베이스에 내 위치 저장
//                    Call<ResultDTO> call = retrofitApi.saveWalkerLocation(applicationClass.currentWalkerID, addressResultStr);
//                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "서버에 보내는 id : " + applicationClass.currentWalkerID);
//                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "서버에 보내는 location : " + addressResultStr);
//                    //비동기 네트워크 처리
//                    call.enqueue(new Callback<ResultDTO>() {
//                        @Override
//                        public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
//                            ResultDTO resultDTO = response.body();
//                            String resultCodeStr = resultDTO.getResponceResult();
//
//                            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "서버에서 받아온 결과 : " + resultCodeStr);
//
//                            if(resultCodeStr.contentEquals("ok")){
//                                makeToast("위치 저장 완료");
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResultDTO> call, Throwable t) {
//                            makeToast("위치 저장 실패");
//                        }
//                    });

                    //전 화면으로 돌아가기
                    intent.putExtra("addressResult", addressResultStr);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

}