package com.example.dogwalker.owner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.dogwalker.ApplicationClass;
import com.example.dogwalker.R;
import com.example.dogwalker.retrofit2.RetrofitApi;
import com.example.dogwalker.retrofit2.RetrofitUtil;
import com.example.dogwalker.walker.WalkerChatActivity;
import com.example.dogwalker.walker.WalkerDogwalkingActivity;
import com.example.dogwalker.walker.WalkerLoginActivity;
import com.example.dogwalker.walker.WalkerMypageActivity;
import com.example.dogwalker.walker.WalkerScheduleActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public abstract class OwnerBottomNavigation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    BottomNavigationView bottomNavigationView;
    ApplicationClass applicationClass;

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    public static RetrofitApi retrofitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        Log.d(TAG, className + "onCreate()");

        applicationClass = (ApplicationClass)getApplicationContext();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavOwner);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //툴바 셋팅
        toolbarSetting();

        //객체와 인터페이스 연결
        retrofitApi = new RetrofitUtil().getRetrofitApi();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottomNavOwner01:
                startActivity(new Intent(this, OwnerBookingActivity.class));
                finish();
                return true;
            case R.id.bottomNavOwner02:
                startActivity(new Intent(this, OwnerChatActivity.class));
                finish();
                return true;
            case R.id.bottomNavOwner03:
                startActivity(new Intent(this, OwnerLiveActivity.class));
                finish();
                return true;
            case R.id.bottomNavOwner04:
                startActivity(new Intent(this, OwnerMypageActivity.class));
                finish();
                return true;
        }
        return false;
    }

    //툴바 셋팅
    public void toolbarSetting(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_owner);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 네비 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24); //네비 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if(id == R.id.logout){
//                    Toast.makeText(context, title + ": 로그아웃을 시도합니다.", Toast.LENGTH_SHORT).show();
                    makeToast("로그아웃 되었습니다.");
                    //쉐어드에 자동로그인 데이터 초기화
                    applicationClass.mySharedPref.saveStringPref("autoLoginID", "");
                    applicationClass.mySharedPref.saveStringPref("autoLoginPW", "");
                    applicationClass.mySharedPref.saveStringPref("autoLoginType", "");
                    applicationClass.mySharedPref.saveBooleanPref("autoLoginCheck", false);

                    //application 현재 로그인한 아이디 데이터 초기화
                    applicationClass.currentWalkerID = "";
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "로그인한 아이디(ApplicationClassID) : " + applicationClass.currentWalkerID);

                    Intent intent = new Intent(context, OwnerLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(id == R.id.accountout){
                    Toast.makeText(context, title + ": 회원탈퇴를 시도합니다.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

    }

//    툴바 왼쪽 햄버거 메뉴 눌렀을때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                //네비 상단 로그인한 유저 이름 데이터 노출
                TextView naviHeaderOwnerName = (TextView) findViewById(R.id.textView_navi_header_owner_name);
                naviHeaderOwnerName.setText(applicationClass.currentWalkerID);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();

    private void updateNavigationBarState() {
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);

    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = bottomNavigationView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, className + "_onStart()");
        updateNavigationBarState();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, className + "-onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, className + "_onPause()");
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);   //0,0 이면 전환효과 해제
//        Log.d(TAG, classname+"-onPause");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, className + "_onStop()");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, className + "_onRestart()");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, className + "_onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //앱 종료 다이얼로그 띄우기
        appFinishDialog();
    }


    //앱 종료 다이얼로그
    public void appFinishDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("앱 종료")
                .setMessage("앱을 종료하시겠습니까?");

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //로그 : 액티비티명 + 함수명 + 원하는 데이터를 한번에 보기위한 로그
    public void makeLog(String methodData, String strData) {
        Log.d(TAG, className + "_" + methodData + "_" + strData);
    }

    //토스트메세지 : 귀찮음을 없애기 위해 토스트를 띄우는 함수를 만듦
    public void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
