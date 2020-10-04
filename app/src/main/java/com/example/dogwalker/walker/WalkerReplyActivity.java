package com.example.dogwalker.walker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerReplyBinding;
import com.example.dogwalker.owner.OwnerBookingActivity;
import com.example.dogwalker.owner.OwnerBookingReviewActivity;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerReplyActivity extends BaseActivity {

    ActivityWalkerReplyBinding binding;
    String order_act, reply_text;
    int review_id, reply_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_reply);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_reply);
        binding.setActivity(this);

        //받아온 데이터
        Intent intent = getIntent();
        order_act = intent.getStringExtra("order_act");
        review_id = intent.getIntExtra("review_id", 0);

        //행동 명령 - 답변 생성
        if(order_act.contains("created_reply")){

            binding.textViewReplyTitle.setText("답변 작성");
            binding.buttonReplySave.setVisibility(View.VISIBLE);
            binding.buttonReplyUpdate.setVisibility(View.GONE);

        //행동 명령 - 답변 수정
        }else if(order_act.contains("updated_reply")){

            //intent 로 받아온 답변 텍스트 데이터, 답변 인덱스
            reply_text = intent.getStringExtra("reply_text");
            reply_id = intent.getIntExtra("reply_id", 0);

            binding.textViewReplyTitle.setText("답변 수정");
            binding.editTextReply.setText(reply_text);
            binding.buttonReplySave.setVisibility(View.GONE);
            binding.buttonReplyUpdate.setVisibility(View.VISIBLE);
        }
    }

    //버튼 - 답변 작성 취소
    public void onCancelReply(View view){
        //답변 취소 버튼 클릭시 한번더 취소여부를 물어보는 다이얼로그
        confirmReplyCancelAlertDialog();
    }

    //버튼 - 답변 작성 등록
    public void onSaveReply(View view){
        //답변 등록 버튼 클릭시 한번더 등록여부를 물어보는 다이얼로그
        confirmReplyUploadAlertDialog();
    }

    //버튼 - 답변 수정 완료
    public void onUpdateReply(View view){
        //DB에 답변 데이터 수정
        updateReplyDataToDB();
    }

    //답변 취소 버튼 클릭시 한번더 취소여부를 물어보는 다이얼로그
    public void confirmReplyCancelAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작성 취소")
                .setMessage("작성을 취소하시겠습니까?");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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

    //답변 등록 버튼 클릭시 한번더 등록여부를 물어보는 다이얼로그
    public void confirmReplyUploadAlertDialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("답변 등록")
//                .setMessage("작성하신 답변을 등록하시겠습니까?");
//        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //DB에 답변 데이터 저장
//                insertReplyDataToDB();
//            }
//        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();

        //DB에 답변 데이터 저장
        insertReplyDataToDB();
    }

    //DB에 답변 데이터 저장
    public void insertReplyDataToDB(){

        //답변 데이터 추가
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("review_id", review_id);
        parameters.put("walker_id", applicationClass.currentWalkerID);
        parameters.put("reply_memo", binding.editTextReply.getText().toString());

        //서버에 저장
        Call<ResultDTO> call = retrofitApi.insertReplyForReviewData(parameters);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "답변 데이터 서버 저장 성공");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", resultDataStr);

                if(resultDataStr.contentEquals("ok")){
                    makeToast("답변이 등록되었습니다");
//                    쌓인 스택을 모두 비우고 메인으로 화면전환하기
//                    Intent intent = new Intent(WalkerReplyActivity.this, WalkerMypageActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "답변 데이터 서버 저장 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
            }
        });

    }

    //DB에 답변 데이터 수정
    public void updateReplyDataToDB(){

        String reply_updated_memo = binding.editTextReply.getText().toString();

        Call<ResultDTO> call = retrofitApi.updateReplyData(reply_id, reply_updated_memo);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "답변 데이터 서버 수정 성공");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", resultDataStr);

                if(resultDataStr.contentEquals("ok")){
                    makeToast("답변이 수정되었습니다");
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "답변 데이터 서버 수정 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
            }
        });
    }
}