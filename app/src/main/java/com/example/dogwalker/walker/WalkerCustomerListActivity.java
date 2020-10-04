package com.example.dogwalker.walker;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerCustomerListBinding;
import com.example.dogwalker.retrofit2.response.CustomerDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerCustomerListActivity extends BaseActivity {

    ActivityWalkerCustomerListBinding binding;

    CustomerListAdapter customerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_customer_list);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_customer_list);
        binding.setActivity(this);

        //리사이클러뷰 초기화 셋팅
        recyclerViewInitSetting();

        //DB 에서 해당 도그워커를 북마크한 유저 리스트 불러오기
        selectWalkerCustomerDataToDB();

        //단골고객 키워드 검색 기능
        searchCustomer();
    }

    //리사이클러뷰 초기화 셋팅
    public void recyclerViewInitSetting(){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerViewCustomerlist.setLayoutManager(linearLayoutManager); //?? 주석??

        customerListAdapter = new CustomerListAdapter(this);
        binding.recyclerViewCustomerlist.setAdapter(customerListAdapter);
    }

    //DB 에서 해당 도그워커를 북마크한 유저 리스트 불러오기
    public void selectWalkerCustomerDataToDB(){
        Call<List<CustomerDTO>> call = retrofitApi.selectWalkerCustomerData(applicationClass.currentWalkerID);
        call.enqueue(new Callback<List<CustomerDTO>>() {
            @Override
            public void onResponse(Call<List<CustomerDTO>> call, Response<List<CustomerDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "단골 고객 데이터 조회 성공");
                List<CustomerDTO> customerDTOList = response.body();    //List
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "단골 고객 리스트 사이즈 : " + customerDTOList.size());
                binding.textViewWalkerCustomerCount.setText(customerDTOList.size()+"");
                /**
                 * 리사이클러뷰 관련 코드
                 */
                ArrayList<CustomerDTO> customerDTOArrayList = new ArrayList<>();
                customerDTOArrayList.addAll(customerDTOList);
                customerListAdapter.setCustomerDTOArrayListet(customerDTOArrayList);
                customerListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<CustomerDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "단골 고객 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "error : "+ t.toString());

            }
        });
    }

    //단골고객 키워드 검색 기능
    public void searchCustomer(){
        binding.editTextCustomerKeywordSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "beforeTextChanged : " + charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "onTextChanged : " + charSequence);
                customerListAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "afterTextChanged : " + editable);
            }
        });
    }
}