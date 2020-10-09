package com.example.dogwalker.walker;

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

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerDogwalkingRecordBinding;
import com.example.dogwalker.retrofit2.response.BookingDoneRecordDTO;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerDogwalkingRecordActivity extends BaseActivity {

    private static final int  PICK_FROM_MULTI_ALBUM = 6002;

    ActivityWalkerDogwalkingRecordBinding binding;

    RecordAlbumDeleteAdapter recordAlbumDeleteAdapter;  //산책기록 첨부파일 삭제 어댑터 (기존 이미지 삭제 가능)
    RecordAlbumUpdateAdapter recordAlbumUpdateAdapter;  //산책기록 첨부파일 추가 어댑터 (새로 이미지 추가 가능)

    public static ArrayList<MultipartBody.Part> addimageFileList;    //다중 이미지 서버에 업로드할 multipartbody 리스트
    ArrayList<String> addimageUrlArraylist;    //앨범에서 선택한 다중이미지 경로를 담은 이미지경로배열

    int record_idx;     //산책 기록 번호
    int booking_id;     //산책 예약 번호 (데이터 조회할 때 필수 필요 변수!!)
    String booking_dog_name; //산책예약 강아지 이름

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_dogwalking_record);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_dogwalking_record);
        binding.setActivity(this);

        //데이터 받아오기
        Intent intent = getIntent();
        //booking_id (예약번호) 데이터 불러오기
        booking_id = intent.getIntExtra("booking_id", 0);    //산책예약 번호
        booking_dog_name = intent.getStringExtra("booking_dog_name");   //산책예약 강아지이름

        //리사이클러뷰(이미지 삭제) 초기화 셋팅
        deleteRecyclerViewInitSetting();
        //리사이클러뷰(이미지 추가) 초기화 셋팅
        insertRecyclerViewInitSetting();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //DB에서 해당 예약 기록 데이터 불러오기
        selectBookingDoneRecordToDB();
    }

    //첨부파일 삭제 리사이클러뷰 초기화 셋팅
    public void deleteRecyclerViewInitSetting(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerViewWalkDoneRecordEditFile.setLayoutManager(linearLayoutManager); //?? 주석??
        binding.recyclerViewWalkDoneRecordEditFile.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recordAlbumDeleteAdapter = new RecordAlbumDeleteAdapter(this);
        binding.recyclerViewWalkDoneRecordEditFile.setAdapter(recordAlbumDeleteAdapter);
    }

    //첨부파일 추가 리사이클러뷰 초기화 셋팅
    public void insertRecyclerViewInitSetting(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerViewWalkDoneRecordAddFile.setLayoutManager(linearLayoutManager); //?? 주석??
        binding.recyclerViewWalkDoneRecordAddFile.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        recordAlbumUpdateAdapter = new RecordAlbumUpdateAdapter(this);
        binding.recyclerViewWalkDoneRecordAddFile.setAdapter(recordAlbumUpdateAdapter);
    }

    //DB에서 해당 예약 기록 데이터 불러오기
    public void selectBookingDoneRecordToDB(){

        Call<List<BookingDoneRecordDTO>> call = retrofitApi.selectBookingDoneRecordData(booking_id);
        call.enqueue(new Callback<List<BookingDoneRecordDTO>>() {
            @Override
            public void onResponse(Call<List<BookingDoneRecordDTO>> call, Response<List<BookingDoneRecordDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "산책기록 데이터 조회 성공");
                List<BookingDoneRecordDTO> bookingDoneRecordDTOList = response.body();
                //산책 기록 번호
                record_idx = bookingDoneRecordDTOList.get(0).getIdx();
                //view에 데이터 표시하기
                binding.textViewWalkDoneCurrentTime.setText(bookingDoneRecordDTOList.get(0).getDone_current_time());
                binding.textViewWalkDoneDogName.setText(booking_dog_name);
                binding.textViewWalkDoneWalkingTime.setText(bookingDoneRecordDTOList.get(0).getDone_walking_time());
                binding.textViewWalkDoneDistance.setText(bookingDoneRecordDTOList.get(0).getDone_distance());
                binding.textViewWalkDonePooCount.setText(bookingDoneRecordDTOList.get(0).getDone_poo_count()+"");
//                binding.textViewWalkDoneMemo.setText(bookingDoneRecordDTOList.get(0).getDone_memo());
                binding.editTextWalkDoneMemoEdit.setText(bookingDoneRecordDTOList.get(0).getDone_memo());
                //TODO: 산책 이미지 setImage 해야함
                if(bookingDoneRecordDTOList.get(0).getMultiFileArrayList().size() != 0){
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "이미지경로배열 : " + bookingDoneRecordDTOList.get(0).getMultiFileArrayList().toString());

                    //첨부파일 이미지 삭제 어댑터에 데이터 셋팅
                    recordAlbumDeleteAdapter.setImageUrlArraylist(bookingDoneRecordDTOList.get(0).getMultiFileArrayList());
                    recordAlbumDeleteAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<BookingDoneRecordDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "산책기록 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "에러 : " + t.toString());

            }
        });
    }

    //산책 기록 수정 버튼
    public void btnUpdateWalkDoneRecord(View view){

        //산책기록 수정할지 물어보는 다이얼로그
        updateWalkDoneRecordAlertDialog();
    }

    //앨범에서 다중이미지 선택 버튼
    public void btnMultiAlbumAction(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //여러장을 선택할 수 있다
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_FROM_MULTI_ALBUM);
    }

    //산책기록 수정할지 물어보는 다이얼로그
    public void updateWalkDoneRecordAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("산책기록 수정하기")
                .setMessage("산책 기록을 수정하시겠습니까?");

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DB에 산책기록 데이터 수정
                updateReviewDataToDB();
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

    //DB에 산책기록 데이터 수정
    public void updateReviewDataToDB(){

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "첨부파일 이미지 갯수 : " + addimageFileList.size());

        //수정할 산책 기록 데이터
        Map<String, RequestBody> partMap = new HashMap<>();
        partMap.put("record_id", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(record_idx)));
        partMap.put("booking_id", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(booking_id)));
        partMap.put("update_record_memo", RequestBody.create(MediaType.parse("text/plain"), binding.editTextWalkDoneMemoEdit.getText().toString()));

        Call<ResultDTO> call = retrofitApi.updateWalkDoneRecodeData(partMap, addimageFileList);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "수정할 산책기록 서버 저장 성공");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", resultDataStr);

                if(resultDataStr.contentEquals("ok")){
                    //쌓인 스택을 모두 비우고 메인 예약화면으로 화면전환하기
//                    Intent intent = new Intent(OwnerBookingRecordActivity.this, WalkerDogwalkingActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
                    finish();
                }else{
                    makeToast("산책기록 수정에 실패하였습니다");
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "수정할 산책기록 서버 저장 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            //앨범에서 파일(이미지, 동영상) 다중 선택해서 돌아왔을 때
            case PICK_FROM_MULTI_ALBUM :

                if (resultCode == RESULT_OK){

                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "requestCode : " + requestCode);
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "resultCode : " + resultCode);
//                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "data.getClipData().getItemCount() : " + data.getClipData().getItemCount());

                    addimageFileList = new ArrayList<>();
                    MultipartBody.Part body;

                    // 멀티 선택을 지원하지 않는 기기에서는 getClipdata()가 없음 => getData()로 접근해야 함
                    if (data.getClipData() == null) {
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "1. single choice : " +  String.valueOf(data.getData()));
//                    imageFileList.add(String.valueOf(data.getData()));
                        //앨범에서 getData Uri 받아온 후
//                    Uri photoUri = data.getData();
                        //photoUri 값을 경로 변환 -> File 객체 생성해주는 메소드에 보내준다
//                    body = applicationClass.updateAlbumImgToServer(photoUri, "uploaded_files[]");
                    } else {

                        ClipData clipData = data.getClipData();
                        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "1. ClipData 갯수 : " + String.valueOf(clipData.getItemCount()));

                        //앨범에서 받은 이미지 데이터 경로를 담을 배열 생성
                        addimageUrlArraylist = new ArrayList<>();

                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            //content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F35/ORIGINAL/NONE/61072326
                            makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "2. multi choice : " + String.valueOf(clipData.getItemAt(i).getUri()));

                            //이미지 경롤 배열에 이미지 Uri 추가
                            addimageUrlArraylist.add(clipData.getItemAt(i).getUri().toString());

                            //앨범에서 getData Uri 받아온 후
                            Uri photoUri = clipData.getItemAt(i).getUri();
                            //photoUri 값을 경로 변환 -> File 객체 생성해주는 메소드에 보내준다
                            body = applicationClass.updateAlbumImgToServer(photoUri, "uploaded_files[]");
                            addimageFileList.add(body);
                        }

                        //이미지 경로 배열 adapter setting
                        recordAlbumUpdateAdapter.setImageUrlArraylist(addimageUrlArraylist);
                        recordAlbumUpdateAdapter.notifyDataSetChanged();
                    }
                }



                break;

            default:
                break;
        }
    }
}