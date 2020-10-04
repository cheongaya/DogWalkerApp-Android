package com.example.dogwalker.walker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;
import com.example.dogwalker.retrofit2.response.CustomerDTO;
import com.example.dogwalker.retrofit2.response.WalkerlistDTO;

import java.util.ArrayList;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.ItemViewHolder> implements Filterable {

    Context context;
    //ApplicationClass 객체 생성
    ApplicationClass applicationClass;
    //adapter에 들어갈 list
    public static ArrayList<CustomerDTO> customerDTOArrayList = new ArrayList<>(); //출력 개념
    public static ArrayList<CustomerDTO> customerDTOArrayListAll; //보관 개념
    //클릭 리스너
    private CustomerListAdapter.OnItemClickListener listenter = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(CustomerListAdapter.OnItemClickListener listener) {
        this.listenter = listener ;
    }

    public CustomerListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        applicationClass = (ApplicationClass)context.getApplicationContext();

        // return ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_walker_customer, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(customerDTOArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return customerDTOArrayList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView imvCustomerProfileImg;
        private TextView tvCustomerName;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imvCustomerProfileImg = itemView.findViewById(R.id.imageView_item_walker_customer_profile_img);
            tvCustomerName = itemView.findViewById(R.id.textView_item_walker_customer_name);

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

        }

        public void onBind(CustomerDTO customerDTO) {

            //반려인 아이디 셋팅
            tvCustomerName.setText(customerDTO.getCustomer_id());
            //반려인 프로필 이미지 셋팅
            Glide.with(context)
                    .load(customerDTO.getProfile_img_url())
                    .override(300,300)
                    .apply(applicationClass.requestOptions.fitCenter().circleCrop())
                    .into(imvCustomerProfileImg);
        }
    }

    public ArrayList<CustomerDTO> getCustomerDTOArrayList(){
        return customerDTOArrayList;
    }

    public void setCustomerDTOArrayListet(ArrayList<CustomerDTO> customerDTOArrayList){
        this.customerDTOArrayList = customerDTOArrayList;
        this.customerDTOArrayListAll = new ArrayList<>(customerDTOArrayList);
    }

    //필터 검색 기능
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                //필터 리스트
                ArrayList<CustomerDTO> filteredList = new ArrayList<>();
                //검색 키워드 없을때
                if(charString.isEmpty() || charString.length() == 0 || constraint == null) {
                    filteredList.addAll(customerDTOArrayListAll);
                    makeLog("charString.isEmpty() : " + charString);
                } else {
                    //검색 키워드 있을때
                    makeLog("charString.isNotEmpty() : " + charString);
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for(CustomerDTO customerDTO : customerDTOArrayListAll) {
                        if(customerDTO.getCustomer_id().toLowerCase().contains(filterPattern)) {
                            filteredList.add(customerDTO);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                customerDTOArrayList.clear();
                customerDTOArrayList.addAll((ArrayList)results.values);
                notifyDataSetChanged();
            }
        };
    }

    //로그 : 액티비티명 + 함수명 + 원하는 데이터를 한번에 보기위한 로그
    public void makeLog(String strData) {
        Log.d("DeveloperLog", strData);
    }
}
