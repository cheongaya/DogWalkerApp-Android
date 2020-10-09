package com.example.dogwalker.walker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RecordAlbumUpdateAdapter extends RecyclerView.Adapter<RecordAlbumUpdateAdapter.ItemViewHolder>  {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
    public static ArrayList<String> imageUrlArraylist = new ArrayList<>();

    //클릭 리스너
    private RecordAlbumUpdateAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(RecordAlbumUpdateAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public RecordAlbumUpdateAdapter(Context context) {
        this.context = context;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_file_upload, parent, false);
        return new RecordAlbumUpdateAdapter.ItemViewHolder(view);
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
        private ImageButton btnImageDelete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imvMultiAlbum = itemView.findViewById(R.id.imageView_item_file_upload_img);
            btnImageDelete = itemView.findViewById(R.id.imageButton_item_file_upload_delete);

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

            //삭제 버튼 클릭시 이벤트
            btnImageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, WalkerStopWatchActivity.class);
//                    context.startActivity(intent);
                }
            });
        }

        public void onBind(String s){

            int position = getAdapterPosition();

            // 앨범에서 선택한 이미지 셋팅
            // string -> uri 변환

            Uri uri = Uri.parse(imageUrlArraylist.get(position));

            // 선택한 이미지에서 비트맵 생성
            InputStream inputStream = null;
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 이미지 표시
            imvMultiAlbum.setImageBitmap(bitmap);

        }
    }

    public ArrayList<String> getImageUrlArraylist(){
        return imageUrlArraylist;
    }

    public void setImageUrlArraylist(ArrayList<String> imageUrlArraylist){
        this.imageUrlArraylist = imageUrlArraylist;
    }
}
