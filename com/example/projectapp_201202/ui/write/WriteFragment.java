package com.example.projectapp_201202.ui.write;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.request.MultiPartRequest;
import com.example.projectapp_201202.BuildConfig;
import com.example.projectapp_201202.MainActivity;
import com.example.projectapp_201202.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.example.projectapp_201202.ui.home.HomeFragment;
import com.example.projectapp_201202.ui.itempage.ItempageFragment;

import static android.app.Activity.RESULT_OK;

public class WriteFragment extends Fragment implements View.OnClickListener {

    String TAG = "WriteFragment";

    TextView tvImageCount; // DB 전송, listener 설정
    EditText etTitle; // DB 전송
    TextView tvCategory; // DB 전송, listener 설정
    EditText etPrice;// DB 전송
    EditText etSubstance; // DB 전송
    EditText etLocation; // DB 전송
    ImageButton ibComment; // listener 설정
    ImageButton ibCancel; // listener 설정
    String[] imagePath;
    String[] abs_imgPath;

    // TextWatcher에 사용할 변수 설정
    private DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private String result="";

    // recycler 관련 변수 설정
    RecyclerView recyclerView;
    ImageRVAdapter imageAdapter;
    Fragment fragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).navView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity)getActivity()).navView.setVisibility(View.VISIBLE);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bot_write, container, false);

        tvImageCount = view.findViewById(R.id.write_tv_ImageCount);
        etTitle = view.findViewById(R.id.write_et_Title);
        tvCategory = view.findViewById(R.id.write_tv_Category);
        etPrice = view.findViewById(R.id.write_et_Price);
        etSubstance = view.findViewById(R.id.write_et_Substance);
        //etLocation = view.findViewById(R.id.textView_SearchLoca);
        ibComment = view.findViewById(R.id.write_iB_Comment);
        ibCancel = view.findViewById(R.id.write_iv_imageCancel);

        tvImageCount.setOnClickListener(this);
        tvCategory.setOnClickListener(this);
        ibComment.setOnClickListener(this);

        setHasOptionsMenu(true); // Fragment별로 툴바 구성을 다르게 할때 설정

        // ========== etPrice의 원 단위 형식 설정 ==========
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){
                    result = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    etPrice.setText(result);
                    etPrice.setSelection(result.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };
        etPrice.addTextChangedListener(watcher);

        // ==================== recyclerView 설정 ====================
        // RecyclerView에 LinearLayoutManager 객체 지정.
        recyclerView = view.findViewById(R.id.write_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // RecyclerView의 레이아웃 방식을 지정
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        // RecyclerView에 SimpleTextAdapter 객체 지정.
        ArrayList<Uri> tempList = new ArrayList<>();
        imageAdapter = new ImageRVAdapter(tempList);        
        recyclerView.setAdapter(imageAdapter);

        // imageFile 삭제 시 RecyclerView.tvImageCount text 변경
        imageAdapter.setOnItemClickListener(new ImageRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d(TAG, "onClick : setTextPhotoUpload");
                setTextPhotoUpload();
            }
        }) ;
        return view;
    } //onCreateView

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        Log.d("change", "write Menu inflate");
        getActivity().getMenuInflater().inflate(R.menu.bot_menu_write, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_my:
                Log.d("Click", "Click write menu complete");
                if(etSubstance.getText().toString().length()>5) {
                    ConnectThread th = new ConnectThread(imagePath);
                    th.start();
                    setWrite();
                } else {
                    minWrite();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ==================== fragment 내부 View의 Listener 설정  ====================
    @Override
    public void onClick(View view) {
        Log.d("Click", "Click write onClick");
        switch (view.getId()) {
            case R.id.write_tv_ImageCount:
                Log.d("Click", "Click write onClick // photoupload");
                getAlbumImage();
                break;
            case R.id.write_iB_Comment:
                Log.d("Click", "Click write onClick // comment");
                break;
            case R.id.write_tv_Category:
                Log.d("Click", "Click write onClick // category");
                getCategoryList();
                break;
        }
    }

    // ========== 글 작성 완료 method ==========
    public void setWrite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("작성 완료");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                Log.d("click", "home frament 불러오기");
                fragment = new HomeFragment();
                //((MainActivity)getActivity()).replaceFragment(fragment);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                transaction.replace(R.id.nav_host_fragment, fragment).commit();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // ========== 글 내용이 너무 짧을 시 알림 method ==========
    public void minWrite() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("글이 너무 짧아요, 조금 더 길게 작성해주세요");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // ========== 카테고리 alertDialog 불러오기 ==========
    public void getCategoryList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setItems(R.array.Category, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                String[] items = getResources().getStringArray(R.array.Category);
                tvCategory.setText(items[pos]);
                Toast.makeText(getActivity(),items[pos],Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // ========== gallery 에서 imageFile 불러오기 ==========
    static final int REQUEST_CODE = 0;
    static final int GET_GALLERY_IMAGE = 100;
    public void getAlbumImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent, GET_GALLERY_IMAGE);
    }

    // ========== 불러온 imageFile 설정 ==========
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "requestCode : " + requestCode + "resultCode : " + resultCode);
        if (requestCode == GET_GALLERY_IMAGE &&
                resultCode == RESULT_OK && data != null && data.getData() != null) {
            //System.out.println(data.getClipData().getItemCount());

            /*// ========== recyclerView 교체 ==========
            for(int i=0; i<data.getClipData().getItemCount(); i++) {
                ivImage.get(i).setImageURI(data.getClipData().getItemAt(i).getUri());
            }
            // =======================================*/

            imagePath = new String[data.getClipData().getItemCount()]; // 이미지패스를 저장할 변수
            abs_imgPath = new String[data.getClipData().getItemCount()]; // abs 이미지패스를 저장할 변수

            // 리사이클러뷰에 표시할 데이터 리스트 생성.
            for (int i=0; i<data.getClipData().getItemCount(); i++) {
                /*
                갤러리앱에서 관리하는 DB정보가 나온다 [실제 파일 경로가 아님!!]
                얻어온 Uri는 Gallery앱의 DB번호임. (content://-----/2854)
                업로드를 하려면 이미지의 절대경로(실제 경로: file:// -------/aaa.png 이런식)가 필요함
                Uri -->절대경로(String)로 변환 (절대경로를 가져오는 메소드 작성)
                */
                Uri tempUri = data.getClipData().getItemAt(i).getUri();
                abs_imgPath[i] = getRealPathFromUri(tempUri);
                Log.d(TAG, "onActivityResult : 1" + abs_imgPath);
                String[] tempString = abs_imgPath[i].toString().split("/");
                imagePath[i] = tempString[tempString.length-1];

                Log.d("TAG", "absolute path : " + abs_imgPath[i]);

                //tempUri = cropLeftRight_Rate(getContext(), tempUri, 50, 50);
                imageAdapter.addItem(tempUri);
            }
            imageAdapter.notifyDataSetChanged();

            setTextPhotoUpload();
        }
    }

    public void setTextPhotoUpload() {
        tvImageCount.setText(""+ imageAdapter.getNum() + "/5");
    }

    // ========== Uri -> 절대경로로 바꿔서 리턴시켜주는 메소드 ==========
    String getRealPathFromUri(Uri uri) {
        String[] proj= {MediaStore.Images.Media.DATA};
        CursorLoader loader= new CursorLoader(getActivity(), uri, proj, null, null, null);
        Cursor cursor= loader.loadInBackground();
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result= cursor.getString(column_index);
        cursor.close();
        return  result;
    }

    // ========== uri를 받아, 높이는 비율로 줄이고 좌우를 크롭하는 것.(상품이미지->섬네일) ==========
    public static Uri cropLeftRight_Rate(Context context, Uri uri, int width, int height){

        String filename = "crop_width_"+width+".png";
        File storageDir = new File(Environment.getExternalStorageDirectory() +
                context.getString(R.string.title_notice));//내장메모리/폴더명 에 저장
        File image = new File(storageDir,filename);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);//원본 이미지

            int oriWidth = bitmap.getWidth();   //원본 너비
            int oriHeight = bitmap.getHeight();//원본 높이

            //비율에 맞춰서(높이기준) 변경
            int newWIdth = height * oriWidth / oriHeight;
            int newHeight = height;

            //리사이징
            Bitmap resize = Bitmap.createScaledBitmap(bitmap, newWIdth, newHeight, true);

            Bitmap newbitmap = cropCenterBitmap(resize,width,-1);//좌우 크롭

            if(image.exists()) {//만약 이미 이 파일이 존재한다면(1회 이상 했다면)
                image.delete();//중복되므로 과거 파일은 삭제
                image = new File(storageDir, filename);//그리고 다시 오픈
            }

            FileOutputStream outputStream = new FileOutputStream(image);
            newbitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.close();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//sdk 24 이상, 누가(7.0)
                uri = FileProvider.getUriForFile(context,// 7.0에서 바뀐 부분은 여기다.
                        BuildConfig.APPLICATION_ID + ".provider", image);
            } else {//sdk 23 이하, 7.0 미만
                uri = Uri.fromFile(image);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return uri;
    }

    // ========== image.center 기준으로 상하좌우 크롭 ==========
    public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
        if (src == null)
            return null;

        //기본 크기
        int width = src.getWidth();
        int height = src.getHeight();

        //높이, 너비 중 하나의 옵션만 사용할 때
        if (w == -1) w = width;
        if (h == -1) h = height;

        if (width < w && height < h)
            return src;

        int x = 0;
        int y = 0;

        if (width > w) x = (width - w) / 2;

        if (height > h) y = (height - h) / 2;

        int cw = w; // crop width
        int ch = h; // crop height

        if (w > width) cw = width;

        if (h > height) ch = height;

        return Bitmap.createBitmap(src, x, y, cw, ch);//x,y 좌표부터 cw, ch크기의 비트맵을 생성.
    }


    // ==================== server 연결을 위한 thread ====================
    class ConnectThread extends Thread {
        String[] imagePath;
        String TAG = "WriteFragment.ConnectThread";
        String ipAddress = "http://192.168.0.45:80";
        private String imgPath;

        ConnectThread(String[] imagePath) {
            this.imagePath = imagePath;
        }
        @Override
        public void run() {
            String title = etTitle.getText().toString();
            String category = tvCategory.getText().toString();
            String price = etPrice.getText().toString();
            String substance = etSubstance.getText().toString();
            String location = "location empty"; //etLocation.getText().toString();

            // ========== 쿼리문 작성 ==========
            String inputStr = "insert into itemlist(title, category, price, substance, location";
            for(int i=1; i<=imagePath.length; i++) {
                inputStr = inputStr + ", imagepath0" + i;
            }
            inputStr = inputStr + ") values('" + title + "', '"
                    + category + "', '" + price + "', '" + substance + "', '" + location;

            String ips = "";
            for(int i=0; i<imagePath.length; i++) {
                ips = ips + "', '" + imagePath[i];
            }
            inputStr = inputStr + ips + "');";
            System.out.println(inputStr);

            // ========== connect ==========
            // data 업로드
            String query = inputStr;
            String serverURL = "" + ipAddress + "/maket_php/data_insert.php";
            String postData = "Data1="+query;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection connData = (HttpURLConnection)url.openConnection();
                connData.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connData.setRequestMethod("POST");
                connData.setConnectTimeout(5000);
                connData.setDoOutput(true);
                connData.setDoInput(true);
                //conn.connect();

                OutputStream outputStream = connData.getOutputStream();
                outputStream.write(postData.getBytes("UTF_8"));
                outputStream.flush();
                outputStream.close();

                StringBuilder jsonHtml = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connData.getInputStream()));
                String line = null;

                int i = 1;
                while((line = reader.readLine()) != null) {
                    jsonHtml.append(line);
                    i++;
                }

                reader.close();
                String result = jsonHtml.toString();

                connData.disconnect();
                Log.d(TAG, "run result : " + result);

                for(int j=0; j<abs_imgPath.length; j++) {
                    clickUpload(abs_imgPath[j]);
                }
            }
            catch (Exception e) {
                Log.i("PHPRequest", "request was failed.");
            }
        } // run

        // ========== 이미파일 서버로 보내기 ==========
        public void clickUpload(String imgPath) {
            // 서버로 보낼 데이터
            //String name= etName.getText().toString();
            //String msg= etMsg.getText().toString();

            // 안드로이드에서 보낼 데이터를 받을 php 서버 주소
            String serverUrl = "" + ipAddress + "/maket_php/image_insert.php";

            // Volley plus Library를 이용해서 파일 전송
            // 파일 전송 요청 객체 생성[결과를 String으로 받음]
            SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("TAG", "ImageFile :" + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG", "ImageFile upload : ERROR");
                }
            });

            // 요청 객체에 보낼 데이터를 추가
            //smpr.addStringParam("name", name);
            //smpr.addStringParam("msg", msg);

            // 이미지 파일 추가
            smpr.addFile("img", imgPath);
            // 요청객체를 서버로 보낼 우체통 같은 객체 생성
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            // 서버에 요청
            requestQueue.add(smpr);
        }
    } // thread class
} // class