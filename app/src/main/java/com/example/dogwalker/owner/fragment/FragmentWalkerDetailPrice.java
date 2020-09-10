package com.example.dogwalker.owner.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.dogwalker.R;
import com.example.dogwalker.databinding.FragmentWalkerDetailPriceBinding;
import com.example.dogwalker.databinding.FragmentWalkerDetailProfileBinding;

public class FragmentWalkerDetailPrice extends FragmentBase {

    FragmentWalkerDetailPriceBinding binding;

    String walkerName;  //bundle 을 통해 전달받은 도그워커 이름 데이터
    int priceThirtyMinutes, priceSixtyMinutes, priceAddLargeSize, priceAddOneDog, priceAddHoliday;
    int walkableTypeS, walkableTypeM, walkableTypeL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_walker_detail_price, container, false);
        binding.setFragment(this);
        View view = binding.getRoot();

        if(getArguments() != null){
            //bundle 을 통해 전달받은 도그워커 이름 데이터
            walkerName = getArguments().getString("walkerName");
            priceThirtyMinutes = getArguments().getInt("priceThirtyMinutes");
            priceSixtyMinutes = getArguments().getInt("priceSixtyMinutes");
            priceAddLargeSize = getArguments().getInt("priceAddLargeSize");
            priceAddOneDog = getArguments().getInt("priceAddOneDog");
            priceAddHoliday = getArguments().getInt("priceAddHoliday");
            walkableTypeS = getArguments().getInt("walkableTypeS");
            walkableTypeM = getArguments().getInt("walkableTypeM");
            walkableTypeL = getArguments().getInt("walkableTypeL");
//            makeToast(walkerName);
        }

        //화면에 데이터 표시
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "기본산책가격 : " + priceThirtyMinutes);
        binding.textViewWalkerProfilePriceThirtyMinutes.setText(priceThirtyMinutes+"");
        binding.textViewWalkerProfilePriceSixtyMinutes.setText(priceSixtyMinutes+"");
        binding.textViewWalkerProfilePriceAddLargeSize.setText(priceAddLargeSize+"");
        binding.textViewWalkerProfilePriceAddOneDog.setText(priceAddOneDog+"");
        binding.textViewWalkerProfilePriceAddHoliday.setText(priceAddHoliday+"");

        if(walkableTypeS == 1){
            binding.imageViewWalkerProfileWalkableTypeS.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorMain), PorterDuff.Mode.MULTIPLY);
        }
        if(walkableTypeM == 1){
            binding.imageViewWalkerProfileWalkableTypeM.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorMain), PorterDuff.Mode.MULTIPLY);
        }
        if(walkableTypeL == 1){
            binding.imageViewWalkerProfileWalkableTypeL.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorMain), PorterDuff.Mode.MULTIPLY);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
