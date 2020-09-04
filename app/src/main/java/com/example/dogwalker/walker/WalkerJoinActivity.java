package com.example.dogwalker.walker;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.JoinAgreement01Activity;
import com.example.dogwalker.JoinAgreement02Activity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerJoinBinding;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.retrofit2.response.ResultStrDTO;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerJoinActivity extends BaseActivity implements Validator.ValidationListener {

    private ActivityWalkerJoinBinding binding;
    String userIDStr, userPWStr, userNameStr, userPhoneNumberStr;
    Boolean duplicateCheckIdIs = false;

    //유효성 검사
    @Length(min = 3,max = 15, message = "아이디는 3~15자 사이로 입력해주세요")//길이
    @NotEmpty(message = "아이디는 필수 입력사항입니다") //필수입력
        EditText editTextID;

    @Length(min = 6, max = 20, message = "6~20자")
    @NotEmpty(message = "비밀번호는 필수 입력사항입니다")
    @Password(scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS, message = "비밀번호는 9~20자의 영문 대/소문자, 숫자 및 특수문자 조합으로 입력해주세요")
    EditText editTextPW;
    @ConfirmPassword //패스워드 확인
            EditText editTextPWConfirm;

    @NotEmpty(message = "이름은 필수 입력사항입니다")
    EditText editTextName;

    @NotEmpty(message = "전화번호는 필수 입력사항입니다")
    @Length(min=10,max=11, message = "10~11자, 전화번호형식을 지켜주세요")
    EditText editPhoneNumber;

    @Checked(message = "약관동의는 필수 체크사항입니다.")
    CheckBox checkBoxAgree01;
    @Checked(message = "개인정보수집이용동의는 필수 체크사항입니다.")
    CheckBox checkBoxAgree02;

    public Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_join);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_join);
        binding.setActivity(this);
//        //액션바 숨기기
//        getSupportActionBar().hide();
        //
        editTextID = binding.editTextJoinID;
        editTextPW = binding.editTextJoinPW;
        editTextPWConfirm = binding.editTextJoinPWConfirm;
        editTextName = binding.editTextName;
        editPhoneNumber = binding.editTextPhoneNumber;
        checkBoxAgree01 = binding.checkboxAgreement01;
        checkBoxAgree02 = binding.checkboxAgreement02;

        //유효성 검사 객체 생성 + 리스너 이벤트 설정 (필수)
        validator = new Validator(this);
        validator.setValidationListener(this);

    }

    //회원가입 아이디 중복환인 하는 메소드
    public void onDuplicateCheckID(View view){

        //DB에서 회원가입된 유저 아이디 데이터를 불러온다
        if(editTextID.getText().toString().isEmpty() || editTextID.getText().toString() == ""){
            makeToast("아이디를 입력해주세요");
        }else{
            duplicateCheckUserID();
        }

    }

    //휴대전화 본인 인증하는 메소드
    public void onCertificationPhoneNumber(View view){
        makeToast("서비스 준비중입니다.");
    }

    //회원가입 완료 메소드
    public void onJoinOK(View view){
        validator.validate();   //버튼 클릭시 유효성 이벤트 발생 (필수)
    }

    //서비스 이용약관 팝업창 띄어주는 메소드
    public void onAgreementView01(View view){
        Intent intent = new Intent(this, JoinAgreement01Activity.class);
        startActivity(intent);
    }

    //개인정보 처리방침 팝업창 띄어주는 메소드
    public void onAgreementView02(View view){
        Intent intent = new Intent(this, JoinAgreement02Activity.class);
        startActivity(intent);
    }

    @Override
    public void onValidationSucceeded() {
        //        makeToast("유효성 검사 성공");
        makeLog(new Object() {
        }.getClass().getEnclosingMethod().getName() + "()", "아이디중복검사 값 : " + duplicateCheckIdIs);

        if(duplicateCheckIdIs == true){
            //회원가입 유저 데이터 DB에 저장
            saveUserDataToDB();
        }else{
            makeToast("아이디 중복체크를 해주세요");
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for(ValidationError error : errors){
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if(view instanceof EditText){
                ((EditText)view).setError(message);
            }else{
                makeToast(message);
            }
        }
    }

    //DB에서 회원가입된 유저 아이디를 조회
    public void duplicateCheckUserID(){
        Call<ResultStrDTO> call = retrofitApi.duplicateIdCheck("user_walker", editTextID.getText().toString());
        call.enqueue(new Callback<ResultStrDTO>() {
            @Override
            public void onResponse(Call<ResultStrDTO> call, Response<ResultStrDTO> response) {

                ResultStrDTO ResultStrDTO = response.body();
                String resultData = ResultStrDTO.toString();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "무슨값을받아올까 : " + resultData);
                if(resultData.contentEquals("ok")){
                    makeToast("사용가능한 아이디입니다");
                    duplicateCheckIdIs = true;
                }else{
                    makeToast("해당 아이디가 존재합니다");
                    duplicateCheckIdIs = false;
                }
            }

            @Override
            public void onFailure(Call<ResultStrDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "실패실패 : "+t.toString());
            }
        });
    }

    //회원가입 유저 데이터 DB에 저장
    public void saveUserDataToDB(){
        //입력한 데이터를 -> String 변수에 담는다
        userIDStr = binding.editTextJoinID.getText().toString();
        userPWStr = binding.editTextJoinPW.getText().toString();
        userNameStr = binding.editTextName.getText().toString();
        userPhoneNumberStr = binding.editTextPhoneNumber.getText().toString();
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "확인 userIDStr : " + userIDStr);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("tableName", "user_walker");
        parameters.put("id", userIDStr);
        parameters.put("pw", userPWStr);
        parameters.put("name", userNameStr);
        parameters.put("phonenumber", userPhoneNumberStr);

//        UserWalkerDTO userWalkerDTO = new UserWalkerDTO(userIDStr, userPWStr, userNameStr, userPhoneNumberStr);

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "확인 parameters : " + parameters.toString());

        //레트로핏 결과 처리
        Call<ResultDTO> call = retrofitApi.joinUser(parameters);
        //비동기 네트워크 처리
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "통신성공 : " + "onResponse");

                ResultDTO resultDTO = response.body();
                String resultCodeStr = resultDTO.getResponceResult();

                if(resultCodeStr.contentEquals("ok")){
                    makeToast("회원가입이 완료되었습니다.");

                    Intent intent = new Intent(WalkerJoinActivity.this, WalkerLoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    makeToast("회원가입 실패");
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "통신실패 : " + "onFailure");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
                makeToast("회원가입 통신 실패");
            }
        });
    }
}