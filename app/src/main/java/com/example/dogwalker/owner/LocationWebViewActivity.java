package com.example.dogwalker.owner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
        webView.loadUrl("http://13.125.123.145/walker/daum_address.php");
    }

    private class AndroidBridge{
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3){
            handler.post(new Runnable() {
                @Override
                public void run() {
//                    tvResult.setText(String.format("(%s) %s %s", arg1, arg2, arg3));

                    String addressResultStr = String.format("(%s) %s %s", arg1, arg2, arg3);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "arg1 : " + arg1);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "arg2 : " + arg2);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "arg3 : " + arg3);

                    //지오코더 객체 생성 (주소 -> 좌표로 변환시켜주기 위해)
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    //getFromLocationName() 메소드(주소나 지명을 좌표로 변환시켜주는 메소드)를 사용하면 결과가 List<Address> 형태로 반환된다
                    List<Address> addressList = null;

                    String latitudeResultStr = null;
                    String longitudeResultStr = null;

                    try {
                        addressList = geocoder.getFromLocationName(addressResultStr, 1);    //지역이름, 읽을 개수
                    } catch (IOException e) {
                        //서버에서 주소 변환시 에러 발생
                        e.printStackTrace();
                    }

                    if(addressList != null){
                        if(addressList.size() == 0){
                            makeToast("해당되는 주소 정보가 없습니다");
                        }else{
                            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "주소 -> 위도 : " + addressList.get(0).getLatitude());  //위도
                            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "주소 -> 경도 : " + addressList.get(0).getLongitude()); //경도
                            double latitude = addressList.get(0).getLatitude();
                            double longitude = addressList.get(0).getLongitude();
                            //double -> String 형변환
                            latitudeResultStr = String.valueOf(latitude);
                            longitudeResultStr = String.valueOf(longitude);
                        }
                    }


                    //WebView를 초기화 하지 않으면 재사용할 수 업음
//                    initWebView();


                    //전 화면으로 돌아가기
                    intent.putExtra("addressResult", addressResultStr);
                    intent.putExtra("LatitudeResult", latitudeResultStr);
                    intent.putExtra("LongitudeResult", longitudeResultStr);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

}