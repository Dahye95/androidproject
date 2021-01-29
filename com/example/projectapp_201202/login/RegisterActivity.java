package com.example.projectapp_201202.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.projectapp_201202.R;
import com.example.projectapp_201202.gps.GpsTracker;
import com.example.projectapp_201202.gps.NaverUrlTask;
import com.example.projectapp_201202.gps.gpstojusoJSON2;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_id, et_pass, et_name, et_juso;
    private Button btn_register,btn_juso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_id=findViewById(R.id.et_id);
        et_pass=findViewById(R.id.et_pass);
        et_name=findViewById(R.id.et_name);
        et_juso=findViewById(R.id.et_juso);
        btn_juso=findViewById(R.id.btnjuso);
        btn_juso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String j=nGeo();
                et_juso.setText(j);
            }
        });
        btn_register=findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = et_id.getText().toString();
                String userPassword = et_pass.getText().toString();
                String userName = et_name.getText().toString();
                String userjuso = et_juso.getText().toString();


                Response.Listener<String>responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success){
                                Toast.makeText(getApplicationContext(),"회원등록에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(),"회원등록에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(userID,userPassword,userName,userjuso,responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);


            }
        });
    }
    String njson=null;
    public String nGeo(){
        String nGeoreturn = "";
        GpsTracker tracker=new GpsTracker(getBaseContext());
//        double latitude = tracker.getLatitude();//위도
//        double longitude = tracker.getLongitude();//경도
        double latitude = 37.45449524240399;//위도
        double longitude = 126.71095688785545;//경도
        if(latitude!=0 && longitude!=0) {
            String Nurl = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords=" + longitude + "," + latitude + "&sourcecrs=epsg:4326&output=json&orders=roadaddr";
//            String Nurl="https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords=126.71095688785545,37.45449524240399&sourcecrs=epsg:4326&output=json&orders=roadaddr";
            NaverUrlTask task = new NaverUrlTask();
            try {
                njson = task.execute(Nurl).get();
                gpstojusoJSON2 gpstojuso = new gpstojusoJSON2(njson);
                nGeoreturn = gpstojuso.getJuso();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            nGeoreturn="다시시도 해주세요";
        return nGeoreturn;
    }
}