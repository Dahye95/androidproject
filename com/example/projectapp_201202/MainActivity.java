package com.example.projectapp_201202;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.projectapp_201202.ui.category.CategoryFragment;
import com.example.projectapp_201202.ui.chatting.ChattingFragment;
import com.example.projectapp_201202.ui.home.HomeFragment;
import com.example.projectapp_201202.ui.my.MyFragment;
import com.example.projectapp_201202.ui.write.WriteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    private final int MY_READ_EXTERNAL_STORAGE=1001;

    Fragment checkFragment;
    Fragment fragment;
    Fragment homefragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    FrameLayout navFragment;
    Toolbar toolbar;
    public BottomNavigationView navView;
    public MenuItem homeMenuItem;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ========== 메인화면을 HomeFragment로 설정 ==========
        homefragment = new HomeFragment();
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.nav_host_fragment, homefragment, "home").commitAllowingStateLoss();

        navView = findViewById(R.id.nav_view);
        navFragment = findViewById(R.id.nav_host_fragment);


        // ========== 툴바 설정 ==========
        toolbar = findViewById(R.id.toolbar_Main);
        toolbar.setPopupTheme(R.style.Theme_MaterialComponents_Light);
        setSupportActionBar(toolbar); // 액션바 대신 툴바로 대체 정의
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바의 타이틀을 보이지 않게 만든다.

        // ========== bottom 네비게이션을 클릭 시 이벤트 설정(코드로 설정, xml 파일 적용 X) ==========
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        //fragment = new HomeFragment();
                        transaction.replace(R.id.nav_host_fragment, homefragment);
                        break;
                    case R.id.navigation_category:
                        fragment = new CategoryFragment();
                        transaction.replace(R.id.nav_host_fragment, fragment);
                        break;
                    case R.id.navigation_write:
                        fragment = new WriteFragment();
                        transaction.replace(R.id.nav_host_fragment, fragment);
                        transaction.addToBackStack(null);
                        break;
                    case R.id.navigation_chatting:
                        fragment = new ChattingFragment();
                        transaction.replace(R.id.nav_host_fragment, fragment);
                        break;
                    case R.id.navigation_my:
                        fragment = new MyFragment();
                        transaction.replace(R.id.nav_host_fragment, fragment);
                        break;
                }
                transaction.commit();
                return true;
            }
        }); //setOnNavigationItemSelectedListener

        // 퍼미션 체크 (권한이 있는경우 PackageManager.PERMISSION_GRANTED, 권한이 없는 경우 PERMISSION_DENIED 반환)
        int permssionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);


        if (permssionCheck!= PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            /*
            퍼미션 체크 반환 => DENIED 인 경우 권한을 요청
            onRequestPermissionResult() 로 사용자의 응답을 전달
             */
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //Toast.makeText(this,"Application 사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_READ_EXTERNAL_STORAGE);
                //Toast.makeText(this,"Application 사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
        }


    } //onCreate

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    // ========== back button 터치 이벤트 ==========
    @Override
    public void onBackPressed() {

        //home fragment로 이동
        /*transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, homefragment).commit();*/

        // backStack 된 프래그먼트로 nav_bot check 변경
        /*homeMenuItem = navView.getMenu().getItem(0);
        homeMenuItem.setChecked(true);*/

        if(navFragment.getChildAt(0).getId() == R.id.home_Container ||
                navFragment.getChildAt(0).getId() == R.id.category_Container ){
            Log.d(TAG, "onBackPressed: navFragment.getChildAt(0).getId() == R.id.home_Container 같음");
            // AlertDialog 실행 (yes or no)
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("어플리케이션을 종료하시겠 습니까??");
            builder.setPositiveButton("아니오", (dialog, which) -> dialog.cancel());

            // task list에 앱화면을 남긴다.
            //builder.setNegativeButton("예", (dialog, which) -> finish());

            // 프로세스 종료 taskList 삭제
            builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveTaskToBack(true); // 태스크를 백그라운드로 이동
                    finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                    android.os.Process.killProcess(android.os.Process.myPid()); // 프로세스 종료
                }
            });

            builder.show();
            return;
        }
        super.onBackPressed(); // 활성화 시에는 앱을 종료시킴(app home 화면으로 이동)
    }

    public Toolbar getToolBar() {
        return toolbar;
    }
} //class