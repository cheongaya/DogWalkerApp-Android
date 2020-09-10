package com.example.dogwalker.owner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.dogwalker.R;
import com.example.dogwalker.data.DogDTO;
import com.example.dogwalker.retrofit2.response.WalkerLocationDTO;
import com.naver.maps.map.overlay.InfoWindow;

import java.util.ArrayList;

public class LocationWalkerMakerAdapter extends InfoWindow.DefaultViewAdapter {
    private final Context mContext;
    private final ViewGroup mParent;

    String textTitle;

    private ArrayList<WalkerLocationDTO> walkerLocationDTOArrayList = new ArrayList<>();

    public LocationWalkerMakerAdapter(@NonNull Context context, ViewGroup parent) {
        super(context);
        mContext = context;
        mParent = parent;
    }

    @NonNull
    @Override
    protected View getContentView(@NonNull InfoWindow infoWindow) {

        View view = (View) LayoutInflater.from(mContext).inflate(R.layout.item_walker_location_marker, mParent, false);

        TextView txtTitle = (TextView) view.findViewById(R.id.txttitle);
        ImageView imagePoint = (ImageView) view.findViewById(R.id.imagepoint);
        TextView txtAddr = (TextView) view.findViewById(R.id.txtaddr);
        TextView txtTel = (TextView) view.findViewById(R.id.txttel);

        txtTitle.setText(textTitle);
        imagePoint.setImageResource(R.drawable.ic_baseline_add_24);
        txtAddr.setText("제주 제주시 문연로 6\n(지번) 연동 312-1");
        txtTel.setText("064-710-2114");

//        walkerLocationDTOArrayList.get()

        return view;
    }
}