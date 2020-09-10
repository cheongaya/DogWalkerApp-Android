package com.example.dogwalker.owner.dialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityDialogPaymentBinding;
import com.example.dogwalker.databinding.ActivityPaymentDoneBinding;
import com.example.dogwalker.owner.PaymentDoneActivity;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listener.CancelListener;
import kr.co.bootpay.listener.CloseListener;
import kr.co.bootpay.listener.ConfirmListener;
import kr.co.bootpay.listener.DoneListener;
import kr.co.bootpay.listener.ErrorListener;
import kr.co.bootpay.listener.ReadyListener;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.BootUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogPaymentActivity extends BaseActivity {

    ActivityDialogPaymentBinding binding;

    String walker_id, owner_dog_name, walk_date, walk_time, payment_method;
    int walk_total_time, walk_total_price;

    private int stuck = 10; //부트페이 관련 변수
    String bootpay_application_ID = "5ec714b84f74b4001f17f4fb"; //부트페이 인증키

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dialog_payment);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dialog_payment);
        binding.setActivity(this);

        //받아온 데이터
        Intent intent = getIntent();
        walker_id= intent.getStringExtra("walker_id");
        owner_dog_name= intent.getStringExtra("owner_dog_name");
        walk_date= intent.getStringExtra("walk_date");
        walk_time= intent.getStringExtra("walk_time");
        walk_total_time= intent.getIntExtra("walk_total_time", 0);
        walk_total_price= intent.getIntExtra("walk_total_price", 0);

        //화면에 표시
        binding.textViewPaymentWalkerId.setText(walker_id);
        binding.textViewPaymentDogName.setText(owner_dog_name);
        binding.textViewPaymentWalkDate.setText(walk_date);
        binding.textViewPaymentWalkTime.setText(walk_time);
        binding.textViewPaymentWalkTotalTime.setText(walk_total_time+" 분");
        binding.textViewPaymentTotalPrice.setText(walk_total_price+" 원");

        //부트페이 : 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        BootpayAnalytics.init(this, bootpay_application_ID);
    }

    //결제하기 버튼 클릭
    public void onClickPaymentOK(View view){

        if(!binding.radioButtonPaymentPhone.isChecked() && !binding.radioButtonPaymentCard.isChecked() && !binding.radioButtonPaymentCard.isChecked()){
            makeToast("결제방법을 선택해주세요");
        }else if(binding.radioButtonPaymentPhone.isChecked()){
            //휴대폰 결제 다이얼로그
            makePaymentBootpayDialog("휴대폰으로", "PHONE");
            payment_method = "phone";

        }else if(binding.radioButtonPaymentCard.isChecked()){
            //신용카드 결제 다이얼로그
            makePaymentBootpayDialog("카드로", "CARD");
            payment_method = "card";

        }else if(binding.radioButtonPaymentKakaopay.isChecked()){
            makeToast("서비스 준비중입니다");
        }

    }

    public void makePaymentBootpayDialog(String paymentTitle, final String paymentBootpayMethod){
        AlertDialog.Builder builder = new AlertDialog.Builder(DialogPaymentActivity.this);
        builder.setTitle(paymentTitle+" 결제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //부트페이로 결제요청 (결제방법, 상품명, 결제금액)
                                requestBootpay(paymentBootpayMethod,walker_id+" / "+walk_date+" "+walk_time+" / "+ walk_total_time+"분 예약", walk_total_price);
                            }
                        })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "결제취소");
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void requestBootpay(String payMethodStr, String itemNameStr, int itemPriceInt){
        // 결제호출
        BootUser bootUser = new BootUser().setPhone("010-7476-7126");
        BootExtra bootExtra = new BootExtra().setQuotas(new int[] {0,2,3});

        kr.co.bootpay.Bootpay.init(getFragmentManager())
                .setApplicationId(bootpay_application_ID) // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.INICIS) // 결제할 PG 사
                .setMethod(Method.valueOf(payMethodStr)) // 결제수단
                .setContext(this)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName(itemNameStr) // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호 expire_month
                .setPrice(itemPriceInt) // 결제할 금액
                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    @Override
                    public void onConfirm(@Nullable String message) {
                        if (0 < stuck) kr.co.bootpay.Bootpay.confirm(message); // 재고가 있을 경우.
                        else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                        Log.d("confirm", message);
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {
                        Log.d("done", message);
                        //결제가 완료됬을 때 그 이후의 프로세스
                        //예약내역을 DB에 저장
                        saveBookingServiceDataToDB();
                    }
                })
                .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                    @Override
                    public void onReady(@Nullable String message) {
                        Log.d("ready", message);
                    }
                })
                .onCancel(new CancelListener() { // 결제 취소시 호출
                    @Override
                    public void onCancel(@Nullable String message) {

                        Log.d("cancel", message);
                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {
                        Log.d("error", message);
                    }
                })
                .onClose(
                        new CloseListener() { //결제창이 닫힐때 실행되는 부분
                            @Override
                            public void onClose(String message) {
                                Log.d("close", "close");
                            }
                        })
                .request();
    }


    //결제가 완료됬을 때 예약내역을 DB에 저장하는 메소드
    public void saveBookingServiceDataToDB(){

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("walker_id", walker_id);                         //예약한 도그워커 아이디
        parameters.put("owner_id", applicationClass.currentWalkerID);   //반려인 아이디
        parameters.put("owner_dog_name", owner_dog_name);               //예약한 반려견 이름
        parameters.put("walk_date", walk_date);                         //예약한 날짜
        parameters.put("walk_time", walk_time);                         //예약한 시간
        parameters.put("walk_total_time", walk_total_time);             //예약한 총 서비스 시간
        parameters.put("walk_total_price", walk_total_price);           //총 결제 금액
        parameters.put("payment_method", payment_method);               //결제 방법

        //현재시간 구하기
        long now = System.currentTimeMillis();
        //Date 형식으로 변환
        Date date = new Date(now);
        //형식 정하기
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = simpleDateFormat.format(date);

        parameters.put("payment_time", currentTime);               //결제 시간

        Call<ResultDTO> call = retrofitApi.insertBookingServiceData(parameters);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultData = resultDTO.getResponceResult();
//                makeToast("예약이 완료되었습니다");

                if(resultData.contentEquals("ok")){
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "예약정보 저장 성공 : " + resultData);
                    Intent paymentDoneIntent = new Intent(DialogPaymentActivity.this, PaymentDoneActivity.class);
                    paymentDoneIntent.putExtra("walker_id", walker_id);
                    paymentDoneIntent.putExtra("owner_dog_name", owner_dog_name);
                    paymentDoneIntent.putExtra("walk_date", walk_date);
                    paymentDoneIntent.putExtra("walk_time", walk_time);
                    paymentDoneIntent.putExtra("walk_total_time", walk_total_time);
                    paymentDoneIntent.putExtra("walk_total_price", walk_total_price);
                    paymentDoneIntent.putExtra("payment_method", payment_method);
                    paymentDoneIntent.putExtra("payment_time", currentTime);
                    startActivity(paymentDoneIntent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "예약정보 저장 실패 : " + t.toString());

            }
        });
    }
}