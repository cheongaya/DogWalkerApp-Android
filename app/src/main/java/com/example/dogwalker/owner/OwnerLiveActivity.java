package com.example.dogwalker.owner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.dogwalker.R;

public class OwnerLiveActivity extends OwnerBottomNavigation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_live);
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_owner_live;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavOwner03;
    }
}