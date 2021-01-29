package com.example.projectapp_201202.ui.itempage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.projectapp_201202.MainActivity;
import com.example.projectapp_201202.R;
import com.example.projectapp_201202.ui.home.HomeItemData;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class ItempageFragment extends Fragment {

    String TAG = "ItempageFragment";

    ArrayList<HomeItemData> homeList;
    int position;

    ArrayList<ItempageItemData> itemList;
    ItempageRVAdapter rvAdapter;

    private ViewPager2 viewPager2;
    private CircleIndicator3 mIndicator;
    ToggleButton toggleBtn;
    Fragment fragment;

    TextView tv_title;
    TextView tv_category;
    TextView tv_price;
    TextView tv_substance;
    FrameLayout frameLayout;

    int deviceWidth, deviceHeight;

    public ItempageFragment(ArrayList<HomeItemData> homeList, int position) {
        this.homeList = homeList;
        this.position = position;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        deviceWidth = dm.widthPixels;
        deviceHeight = dm.heightPixels;

        // 이미지파일 리스트 정의
        itemList = new ArrayList<>();

        // 이미지파일 리스트에 추가
        String[] tempString = homeList.get(position).getImagesPath();
        for(int i=0; i<tempString.length; i++) {
            ItempageItemData tempItemData = new ItempageItemData();
            tempItemData.setImageUrl(tempString[i]);
            itemList.add(tempItemData);
        }

        // 이미지 클릭 리스너
        rvAdapter = new ItempageRVAdapter(itemList, deviceWidth, deviceHeight, 1);

        ((MainActivity)getActivity()).navView.setVisibility(View.GONE);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itempage, container, false);
        initView(view);

        // ========== 툴바 설정 ==========
        // Fragment 마다 툴바 구성을 다르게 할때
        setHasOptionsMenu(true);

        // statusBar background 투명화
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        // statusBar 변경에 따른 toolBar 위치 변경
        Toolbar toolbar = ((MainActivity)getActivity()).getToolBar();
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0 );
        ((MainActivity)getActivity()).getSupportActionBar().show(); //액션바 보이기
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 백버튼 활성화

        toggleBtn = (ToggleButton) view.findViewById(R.id.ip_toggleBtn);
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleBtn.isChecked()) {
                    Log.d(TAG, "onClick: toggle Button true");
                    toggleBtn.setBackgroundResource(R.drawable.ic_baseline_radio_button_checked_24);
                } else {
                    Log.d(TAG, "onClick: toggle Button false");
                    toggleBtn.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                }
            }
        });

        // ========== ViewPager2 설정 ==========

        rvAdapter.setOnItemClickListener(new ItempageRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                fragment = new ItempageImgFragment(itemList, position, deviceWidth, deviceHeight);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); //프래그먼트가 전환되는 애니메이션

                transaction.replace(R.id.ip_mainContainer, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // 어댑터 설정
        viewPager2.setAdapter(rvAdapter);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // indicator 설정
        mIndicator = view.findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager2);
        mIndicator.createIndicators(itemList.size(),0);

        return view;
    }// oncreate overide method

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.itempage_menu, menu);
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        ((MainActivity)getActivity()).navView.setVisibility(View.VISIBLE);
    }


    //status bar의 높이 계산
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            result = getResources().getDimensionPixelSize(resourceId);

        return result;
    }

    public void initView(View view) {
        frameLayout = view.findViewById(R.id.toolbar_frame_itempage);
        //frameLayout.setPadding(0,getStatusBarHeight(),0,0);

        viewPager2 = (ViewPager2) view.findViewById(R.id.ip_viewPager);

        tv_title = view.findViewById(R.id.ipage_tv_title);
        tv_category = view.findViewById(R.id.ipage_tv_category);
        tv_substance = view.findViewById(R.id.ipage_tv_substance);
        tv_price = view.findViewById(R.id.ip_tv_price);

        tv_title.setText(homeList.get(position).getTitle());
        tv_category.setText(homeList.get(position).getCategory());
        tv_substance.setText(homeList.get(position).getSubstance());
        tv_price.setText(homeList.get(position).getPrice());
    }
}