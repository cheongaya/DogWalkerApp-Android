package com.example.dogwalker.walker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.dogwalker.R;

public class WalkerDogwalkingActivity extends WalkerBottomNavigation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_dogwalking);
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_walker_dogwalking;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavWorker01;
    }
}