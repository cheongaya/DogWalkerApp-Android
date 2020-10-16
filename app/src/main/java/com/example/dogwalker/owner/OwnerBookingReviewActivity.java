package com.example.dogwalker.owner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.SeekBar;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityOwnerBookingReviewBinding;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.walker.MultiAlbumAdapter;
import com.example.dogwalker.walker.WalkerDogwalkingActivity;
import com.example.dogwalker.walker.WalkerDogwalkingDoneActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerBookingReviewActivity extends BaseActivity {

    ActivityOwnerBookingReviewBinding  binding;

    private static final int  PICK_FROM_MULTI_ALBUM = 7001;

    int booking_id;          //예약 번호
    String booking_dog_name; //산책예약 강아지 이름
    String walker_id, owner_id;
    int satisfationProgress = 0;    //seekBar 데이터

    public static ArrayList<MultipartBody.Part> imageFileList;    //다중 이미지 서버에 업로드할 multipartbody 리스트
    MultiAlbumAdapter multiAlbumAdapter;
    ArrayList<String> imageUrlArraylist;    //앨범에서 선택한 다중이미지 경로를 담은 이미지경로배열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_booking_review);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_owner_booking_review);
        binding.setActivity(this);

        //booking_id (예약번호) 데이터 불러오기
        Intent intent = getIntent();
        booking_id = intent.getIntExtra("booking_id", 0);
        booking_dog_name = intent.getStringExtra("booking_dog_name");
        walker_id = intent.getStringExtra("walker_id");
        owner_id = intent.getStringExtra("owner_id");

        //데이터 화면에 표시
        binding.textViewReviewWalkerName.setText(walker_id);
        binding.textViewReviewBookingContent.setText(booking_dog_name+"(반려견) 도그워커 서비스 예약");

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        //서비스 이용 만족도 평가 seekBar 리스너
        seekBarChangeListener();

    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerViewReviewFileUpload.setLayoutManager(linearLayoutManager); //?? 주석??
        binding.recyclerViewReviewFileUpload.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        multiAlbumAdapter = new MultiAlbumAdapter(this);
        binding.recyclerViewReviewFileUpload.setAdapter(multiAlbumAdapter);
    }

    //서비스 이용 만족도 seekBar 리스너
    public void seekBarChangeListener(){
        //만족도 seekBar
        binding.seekBarReviewSatisfationScore.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //seekBar 변할 때
                binding.textViewReivewSatisfationScore.setText("만족도 "+progress);
                satisfationProgress = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //버튼 - 리뷰 사진/동영상 다중 파일 업로드
    public void btnReviewFileUpload(View view){
//        makeToast("서비스 준비중입니다");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //코드 추가시 여러장을 선택할 수 있다
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_MULTI_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            //앨범에서 파일(이미지, 동영상) 다중 선택해서 돌아왔을 때
            case PICK_FROM_MULTI_ALBUM :

                imageFileList = new ArrayList<>();
                MultipartBody.Part body;

                // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함
                if (data.getClipData() == null) {
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "1. single choice : " +  String.valueOf(data.getData()));
//                    imageFileList.add(String.valueOf(data.getData()));
                    //앨범에서 getData Uri 받아온 후
                    Uri photoUri = data.getData();
                    //photoUri 값을 경로 변환 -> File 객체 생성해주는 메소드에 보내준다
                    body = applicationClass.updateAlbumImgToServer(photoUri, "uploaded_files[]");
                } else {

                    ClipData clipData = data.getClipData();
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "1. ClipData 갯수 : " + String.valueOf(clipData.getItemCount()));

                    //앨범에서 받은 이미지 데이터 경로를 담을 배열 생성
                    imageUrlArraylist = new ArrayList<>();

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        //content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F35/ORIGINAL/NONE/61072326
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "2. multi choice : " + String.valueOf(clipData.getItemAt(i).getUri()));

//                        //이미지 경롤 배열에 이미지 Uri 추가
//                        imageUrlArraylist.add(clipData.getItemAt(i).getUri().toString());

                        //uri -> file 변환
                        imageUrlArraylist.add(applicationClass.changeToFile(clipData.getItemAt(i).getUri()).toString());


                        //앨범에서 getData Uri 받아온 후
                        Uri photoUri = clipData.getItemAt(i).getUri();
                        //photoUri 값을 경로 변환 -> File 객체 생성해주는 메소드에 보내준다
                        body = applicationClass.updateAlbumImgToServer(photoUri, "uploaded_files[]");
                        imageFileList.add(body);
                    }

                    //이미지 경로 배열 adapter setting
                    multiAlbumAdapter.setImageUrlArraylist(imageUrlArraylist);
                    multiAlbumAdapter.notifyDataSetChanged();
                }

                break;

            default:
                break;
        }
    }

    //버튼 - 리뷰쓰기 취소
    public void btnReviewCancel(View view){
        //리뷰 취소 버튼 클릭시 한번더 취소여부를 물어보는 다이얼로그
        confirmReviewCancelAlertDialog();
    }

    //버튼 - 리뷰쓰기 등록
    public void btnReviewUpload(View view){
        //리뷰 등록 버튼 클릭시 한번더 등록여부를 물어보는 다이얼로그
        confirmReviewUploadAlertDialog();
    }

    //리뷰 취소 버튼 클릭시 한번더 취소여부를 물어보는 다이얼로그
    public void confirmReviewCancelAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("리뷰 취소")
                .setMessage("리뷰 작성을 취소하시겠습니까?");

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

    //리뷰 등록 버튼 클릭시 한번더 등록여부를 물어보는 다이얼로그
    public void confirmReviewUploadAlertDialog(){

        //산책 리뷰 데이터 추가
        Map<String, RequestBody> partMap = new HashMap<>();
        partMap.put("booking_id", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(booking_id)));
        partMap.put("walker_id", RequestBody.create(MediaType.parse("text/plain"), walker_id));
        partMap.put("owner_id", RequestBody.create(MediaType.parse("text/plain"), owner_id));
        partMap.put("booking_dog_name", RequestBody.create(MediaType.parse("text/plain"), booking_dog_name));
        partMap.put("review_satisfation_score", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(satisfationProgress)));
        partMap.put("review_memo", RequestBody.create(MediaType.parse("text/plain"), binding.editTextReivewMemo.getText().toString()));
//        partMap.put("review_files_url", RequestBody.create(MediaType.parse("text/plain"), "null"));

        //서버에 저장
        Call<ResultDTO> call = retrofitApi.insertBookingReviewData(partMap, imageFileList);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "워커 리뷰 서버 저장 성공");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", resultDataStr);

                if(resultDataStr.contentEquals("ok")){
                    //쌓인 스택을 모두 비우고 메인으로 화면전환하기
                    Intent intent = new Intent(OwnerBookingReviewActivity.this, OwnerBookingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    makeToast("리뷰 등록에 실패하였습니다");
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "워커 리뷰 서버 저장 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
            }
        });

    }
}