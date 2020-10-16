package com.example.dogwalker.walker;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.RetrofitApi;
import com.example.dogwalker.retrofit2.RetrofitUtil;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordAlbumDeleteAdapter extends RecyclerView.Adapter<RecordAlbumDeleteAdapter.ItemViewHolder>  {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //retrofit 객체 생성
    public static RetrofitApi retrofitApi;
    //adapter에 들어갈 list
    public static ArrayList<String> imageUrlArraylist = new ArrayList<>();

    //클릭 리스너
    private RecordAlbumDeleteAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(RecordAlbumDeleteAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public RecordAlbumDeleteAdapter(Context context) {
        this.context = context;
        //TODO: 이미지경로배열 초기화 코드 추가
        imageUrlArraylist.clear();
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();
        //객체와 인터페이스 연결
        retrofitApi = new RetrofitUtil().getRetrofitApi();
        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_file_upload, parent, false);
        return new RecordAlbumDeleteAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(imageUrlArraylist.get(position));
    }

    @Override
    public int getItemCount() {
        return imageUrlArraylist.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imvMultiAlbum;
        private ImageButton btnDeleteImg;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imvMultiAlbum = itemView.findViewById(R.id.imageView_item_file_upload_img);
            btnDeleteImg = itemView.findViewById(R.id.imageButton_item_file_upload_delete);

            //onClick 선언
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(listenter !=null){
                            listenter.onItemClick(v,position);
                        }
                    }
                }
            });

            //삭제 버튼 클릭시 -> 해당 이미지 삭제
            btnDeleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    //서버와 DB 에서 첨부파일 이미지 삭제
                    confirmImgFileDeleteAlertDialog(position);

//                    //리사이클러뷰 아이템에서 삭제 진행
//                    imageUrlArraylist.remove(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, imageUrlArraylist.size());
                }
            });
        }

        public void onBind(String s){

            int position = getAdapterPosition();

            Glide.with(context)
                    .load(imageUrlArraylist.get(position))
                    .override(300,300)
                    .into(imvMultiAlbum);
        }
    }

    public ArrayList<String> getImageUrlArraylist(){
        return imageUrlArraylist;
    }

    public void setImageUrlArraylist(ArrayList<String> imageUrlArraylist){
        this.imageUrlArraylist = imageUrlArraylist;
    }

    //이미지 삭제 버튼 클릭시 한번더 삭제여부를 물어보는 다이얼로그
    public void confirmImgFileDeleteAlertDialog(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("이미지 삭제")
                .setMessage("해당 이미지를 삭제하시겠습니까?");

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImgFileDataToDB(dialog, position);
//                finish();
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

    //서버에서 첨부파일 데이터 삭제
    public void deleteImgFileDataToDB(DialogInterface dialog, int position){

        applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "삭제할 이미지 file_path : " + imageUrlArraylist.get(position));

        //문자열 자르기
        //2. 특정문자 이후의 문자열 출력
        String str = imageUrlArraylist.get(position); //http://52.78.138.74/common/multi/$2y$10$VgxHnbOYjdI8oBfJqKhD1.2An3Ne6ZMyqzN.ctu6iPy5P2Afvd1sO
        String file_path = str.substring(27); //결과값 multi/$2y$10$VgxHnbOYjdI8oBfJqKhD1.2An3Ne6ZMyqzN.ctu6iPy5P2Afvd1sO
        applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "삭제할 이미지 file_path (자르기) : " + file_path);

        Call<ResultDTO> call = retrofitApi.deleteWalkDoneRecordImage(file_path);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "첨부파일 이미지 삭제 성공");
                ResultDTO resultDTO = response.body();

                if(resultDTO.getResponceResult().contains("ok")){

                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    //리사이클러뷰 아이템에서 삭제 진행
                    imageUrlArraylist.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, imageUrlArraylist.size());
                    dialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "첨부파일 이미지 삭제 실패");
                applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
            }
        });
    }
}
