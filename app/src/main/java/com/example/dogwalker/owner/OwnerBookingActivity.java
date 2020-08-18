package com.example.dogwalker.owner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dogwalker.OwnerWalkerlistActivity;
import com.example.dogwalker.R;

public class OwnerBookingActivity extends OwnerBottomNavigation {

    Button btnBookingPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_booking);

        btnBookingPage = (Button)findViewById(R.id.button_booking_page);

        //도그워커 예약하기 버튼 클릭시 도그워커 리스트로 이동
        btnBookingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBookingPage = new Intent(OwnerBookingActivity.this, OwnerWalkerlistActivity.class);
                startActivity(intentBookingPage);
                finish();
            }
        });
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_owner_booking;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.bottomNavOwner01;
    }
}