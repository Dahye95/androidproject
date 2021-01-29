package com.example.projectapp_201202.ui.itempage;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectapp_201202.MainActivity;
import com.example.projectapp_201202.R;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class ItempageImgFragment extends Fragment {
    String TAG = "ItempageImgFragment";

    ViewPager2 if_viewPager2;
    ArrayList<ItempageItemData> itempageItemData;

    int deviceWidth, deviceHeight;
    int position;
    private CircleIndicator3 mIndicator;

    public ItempageImgFragment(ArrayList<ItempageItemData> itempageItemData,
                               int position, int deviceWidth, int deviceHeight) {
        this.itempageItemData = itempageItemData;
        this.position = position;
        this.deviceWidth = deviceWidth;
        this.deviceHeight = deviceHeight;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.itempage_recycler_fullimage, container, false);
        if_viewPager2 = view.findViewById(R.id.ip_if_viewPager2);

        ((MainActivity)getActivity()).getSupportActionBar().hide(); //액션바 숨기기

        // 어댑터 설정
        ItempageRVAdapter rvAdapter = new ItempageRVAdapter(itempageItemData, deviceWidth, deviceHeight, 2);
        if_viewPager2.setAdapter(rvAdapter);
        if_viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        // indicator 설정
        mIndicator = view.findViewById(R.id.ip_if_indicator);
        mIndicator.setViewPager(if_viewPager2);
        mIndicator.createIndicators(itempageItemData.size(),position);

        // 클릭한 이미지로 이동
        if_viewPager2.setCurrentItem(position, false);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: -> onCreate -> onCraeteView -> onCreateActivity");
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        ((MainActivity)getActivity()).getSupportActionBar().show(); //액션바 보이기
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        super.onDetach();
    }
}