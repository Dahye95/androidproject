package com.example.projectapp_201202.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectapp_201202.MainActivity;
import com.example.projectapp_201202.R;
import com.example.projectapp_201202.ui.itempage.ItempageFragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment implements View.OnClickListener {
    public static final String LOG_TAG =  "PopupMenuExample";
    String TAG = "HomeFragment";

    ArrayList<HomeItemData> newList = new ArrayList<>();
    HomeRVAdapter adapter;
    RecyclerView recyclerView;

    TextView textViewLocate;
    ImageView arrowImageLocate;
    Animation animation;

    static ProgressDialog pd;
    static Context mContext;
    Fragment fragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        // 리사이클러뷰에 표시할 데이터 리스트 생성.
        HomeLoadList homeLoadList = new HomeLoadList();
        try {
            newList = homeLoadList.execute("http://192.168.0.45:80").get(); // android : 10.0.2.2::80, com : 192.168.0.6:80
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 리사이클러뷰에 사용할 Adapter 객체 생성.
        adapter = new HomeRVAdapter(newList);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_bot_home, container, false);
        View view = inflater.inflate(R.layout.fragment_bot_home, container, false);
        Log.d(TAG, "onCreateView: ");

        // Fragment별로 툴바 구성을 다르게 할때 필요
        setHasOptionsMenu(true);

        // 스테이터스바 나타내기
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        getActivity().getWindow().setStatusBarColor(Color.DKGRAY);

        // 툴바 셋팅 초기화
        Toolbar toolbar = ((MainActivity)getActivity()).getToolBar();
        toolbar.setPadding(0, 0, 0, 0 );
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        textViewLocate = view.findViewById(R.id.textViewLocate);
        arrowImageLocate = view.findViewById(R.id.arrowImageLocate);
        textViewLocate.setOnClickListener(this);

        mContext = getContext();
        pd = new ProgressDialog(getContext());

        // ==================== recyclerView 설정. ====================

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView = view.findViewById(R.id.home_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 리사이클러뷰에 어댑터 설정
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new HomeRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                System.out.println("position : " + position);

                fragment = new ItempageFragment(newList, position);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                transaction.replace(R.id.nav_host_fragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }) ;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        /*MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.bot_menu_home, menu);*/
        getActivity().getMenuInflater().inflate(R.menu.bot_menu_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                System.out.println("Click home");
                return true;
            case R.id.menu_search:
                System.out.println("Click search");
                return true;
            case R.id.menu_camera:
                System.out.println("Click camera");
                return true;
            case R.id.ip_menu_share:
                System.out.println("Click slide");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // ==================== 툴바 버튼 메뉴 설정(좌측 상단) ====================
    @Override
    public void onClick(View view)  {
        animation = AnimationUtils.loadAnimation(getContext().getApplicationContext(),R.anim.rotate180);
        arrowImageLocate.startAnimation(animation);

        PopupMenu popup = new PopupMenu(getContext(), this.textViewLocate);
        popup.inflate(R.menu.home_top_popup);

        Menu menu = popup.getMenu();

        // popup 리스너 설정 (Register Menu Item Click event.)
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.popupSelectLoca:
                        Toast.makeText(getActivity(), "Click 부평4동", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.popupSetLoca:
                        Toast.makeText(getActivity(), "Click 내 동네 설정", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return popupItemClicked(item);
            }
        });

        // popup 메뉴가 닫혔음을 알리는 메소드
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Log.d("popup", "popup close");
                animation = AnimationUtils.loadAnimation(getContext().getApplicationContext(),R.anim.rotate_180);
                arrowImageLocate.startAnimation(animation);
            }
        });
        // PopupMenu 보이기
        popup.show();
    }

    // When user click on Menu Item.
    // @return true if event was handled.
    private boolean popupItemClicked(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popupSelectLoca:
                Toast.makeText(getActivity(), "Select Location", Toast.LENGTH_SHORT).show();
                break;
            case R.id.popupSetLoca:
                Toast.makeText(getActivity(), "New Location Select", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}