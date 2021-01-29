package com.example.projectapp_201202.gps;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NaverUrlTask extends AsyncTask<String, Void, String> {
    String st=null;
    String clientId = "herjvgubhh";// 애플리케이션 클라이언트 아이디값";
    String clientSecret = "BW0utLOQfej0OwtkFKPIK1PuwoFcBXGsOp181MXo";// 애플리케이션 클라이언트 시크릿값";

    @Override
    protected String doInBackground(String... params) {
        String textUrl = params[0];
        try {
            URL url = new URL(textUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                return response.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String result) {
        if(result  != null){
            st=result;
        } else{
            Log.e("MyMessage", "Failed to fetch data!");
        }
    }
    public String getjson(){
        if(st!=null)
            return st;
        else
            return "null";
    }
}
