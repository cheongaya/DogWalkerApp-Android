package com.example.dogwalker.walker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;
import com.example.dogwalker.owner.RecordAlbumAdapter;
import com.example.dogwalker.retrofit2.RetrofitApi;
import com.example.dogwalker.retrofit2.RetrofitUtil;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.retrofit2.response.WalkerReviewManageDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.sql.Types.NULL;

public class WalkerReviewManageAdapter extends RecyclerView.Adapter<WalkerReviewManageAdapter.ItemViewHolder> {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //retrofit 객체 생성
    public static RetrofitApi retrofitApi;
    //adapter에 들어갈 list
    public static ArrayList<WalkerReviewManageDTO> walkerReviewManageDTOArrayList = new ArrayList<>();

    //클릭 리스너
    private WalkerReviewManageAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(WalkerReviewManageAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public WalkerReviewManageAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();
        //객체와 인터페이스 연결
        retrofitApi = new RetrofitUtil().getRetrofitApi();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_review_manage, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(walkerReviewManageDTOArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return walkerReviewManageDTOArrayList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        //댓글 관련
        private ImageView imvReviewOwnerImg;
        private TextView tvReviewOwnerName, tvReviewCreatedDate, tvReviewMemo;
        private RecyclerView recyReviewFile;
        //답글 관련
        private Button btnCreateReply;
        private LinearLayout linearReplyCnt;    //답글영역
        private TextView tvReplyWalkerName, tvReplyCreatedDate, tvReplyMemo;
        private ImageButton btnUpdateReply, btnDeleteReply;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            //댓글 관련
            imvReviewOwnerImg = itemView.findViewById(R.id.imageView_item_walker_review_profile_img);
            tvReviewOwnerName = itemView.findViewById(R.id.textView_item_walker_review_owner_name);
            tvReviewCreatedDate = itemView.findViewById(R.id.textView_item_walker_review_write_date);
            tvReviewMemo = itemView.findViewById(R.id.textView_item_walker_review);
            recyReviewFile = itemView.findViewById(R.id.recyclerView_item_walker_review);   //이미지 첨부파일 리사이클러뷰
            //답글 관련
            btnCreateReply = itemView.findViewById(R.id.button_item_reply_create);
            linearReplyCnt = itemView.findViewById(R.id.linearLayout_reply);
            tvReplyWalkerName = itemView.findViewById(R.id.textView_item_reply_walker_name);
            tvReplyCreatedDate = itemView.findViewById(R.id.textView_item_reply_write_date);
            tvReplyMemo = itemView.findViewById(R.id.textView_item_reply_text);
            btnUpdateReply = itemView.findViewById(R.id.imageButton_reply_edit);
            btnDeleteReply = itemView.findViewById(R.id.imageButton_reply_delete);

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

            //답글 버튼 클릭시
            btnCreateReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, WalkerReplyActivity.class);
                    intent.putExtra("order_act", "created_reply");   //명령행동
                    intent.putExtra("review_id", walkerReviewManageDTOArrayList.get(position).getReview_idx());
                    context.startActivity(intent);
                }
            });

            //수정 버튼 클릭시
            btnUpdateReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, WalkerReplyActivity.class);
                    intent.putExtra("order_act", "updated_reply");   //명령행동
                    intent.putExtra("reply_text", walkerReviewManageDTOArrayList.get(position).getReply_memo());
                    intent.putExtra("reply_id", walkerReviewManageDTOArrayList.get(position).getReply_idx());
                    intent.putExtra("review_id", walkerReviewManageDTOArrayList.get(position).getReview_idx());
                    context.startActivity(intent);
                }
            });

            //삭제 버튼 클릭시
            btnDeleteReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //답변 삭제 버튼 클릭시 한번더 삭제여부를 물어보는 다이얼로그
                    confirmReplyDeleteAlertDialog(position, linearReplyCnt, btnCreateReply);
                }
            });
        }

        public void onBind(WalkerReviewManageDTO walkerReviewManageDTO) {

            //유저 이미지
            Glide.with(context)
                    .load(walkerReviewManageDTO.getReview_owner_profile_img())
                    .override(300,300)
                    .apply(applicationClass.requestOptions.fitCenter().circleCrop())
                    .into(imvReviewOwnerImg);

            //리뷰 관련
//            imvReviewOwnerImg.setImageURI(bookingServiceDTO.getOwner_id());
            tvReviewOwnerName.setText(walkerReviewManageDTO.getReview_owner_id());
            tvReviewCreatedDate.setText(walkerReviewManageDTO.getReview_created_date());
            tvReviewMemo.setText(walkerReviewManageDTO.getReview_memo());
            //답변 관련
            if(walkerReviewManageDTO.getReply_idx() == NULL){
                linearReplyCnt.setVisibility(View.GONE);
                btnCreateReply.setVisibility(View.VISIBLE);
            }else{
                btnCreateReply.setVisibility(View.GONE);
                linearReplyCnt.setVisibility(View.VISIBLE);
                tvReplyWalkerName.setText(walkerReviewManageDTO.getReply_walker_id());
                tvReplyMemo.setText(walkerReviewManageDTO.getReply_memo());
                if(walkerReviewManageDTO.getReply_updated_date() == null){
                    tvReplyCreatedDate.setText(walkerReviewManageDTO.getReply_created_date());
                    applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "walkerReviewManageDTO.getReply_updated_date() : " + walkerReviewManageDTO.getReply_updated_date());
                }else{
                    tvReplyCreatedDate.setText(walkerReviewManageDTO.getReply_updated_date());
                    applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "walkerReviewManageDTO.getReply_updated_date() : " + walkerReviewManageDTO.getReply_updated_date());
                }
            }

            //리뷰 관련 첨부파일 이중리사이클러뷰 코드
            if(walkerReviewManageDTO.getMultiFileArrayList().size() != 0){

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                recyReviewFile.setLayoutManager(linearLayoutManager);
                recyReviewFile.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                RecordAlbumAdapter recordAlbumAdapter = new RecordAlbumAdapter(context);
                recyReviewFile.setAdapter(recordAlbumAdapter);

                recordAlbumAdapter.setImageUrlArraylist(walkerReviewManageDTO.getMultiFileArrayList());
                recordAlbumAdapter.notifyDataSetChanged();
            }

        }
    }

    public ArrayList<WalkerReviewManageDTO> getWalkerReviewManageDTOArrayList(){
        return walkerReviewManageDTOArrayList;
    }

    public void setWalkerReviewManageDTOArrayList(ArrayList<WalkerReviewManageDTO> walkerReviewManageDTOArrayList){
        this.walkerReviewManageDTOArrayList = walkerReviewManageDTOArrayList;
    }

    //답변 삭제 버튼 클릭시 한번더 삭제여부를 물어보는 다이얼로그
    public void confirmReplyDeleteAlertDialog(int position, View linearReplyCnt, View btnCreateReply){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("답변 삭제")
                .setMessage("답변을 삭제하시겠습니까?");

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteReplyDataToDB(dialog, position, linearReplyCnt, btnCreateReply);
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

    //서버에서 답변 데이터 삭제
    public void deleteReplyDataToDB(DialogInterface dialog, int position, View linearReplyCnt, View btnCreateReply){
        Call<ResultDTO> call = retrofitApi.deleteReplyData(walkerReviewManageDTOArrayList.get(position).getReply_idx());
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "답변 삭제 성공");
                ResultDTO resultDTO = response.body();
                if(resultDTO.getResponceResult().contains("ok")){
                    //삭제 진행
//                    walkerReviewManageDTOArrayList.remove(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, walkerReviewManageDTOArrayList.size());
                    linearReplyCnt.setVisibility(View.GONE);    //답변 영역 비노출
                    btnCreateReply.setVisibility(View.VISIBLE); //답변 추가 버튼 노출
                    applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "item 삭제 완료");
                    dialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "답변 삭제 실패");
                applicationClass.makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
            }
        });
    }
}
