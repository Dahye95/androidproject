package com.example.projectapp_201202.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HomeLoadList extends AsyncTask<String, Void, ArrayList<HomeItemData>> {
    String TAG = "HomeLoadList";
    String ipAddress = "";

    ArrayList<HomeItemData> homeItemDataArrayList;
    //ProgressDialog pd;

    public HomeLoadList() {
    }

    public HomeLoadList(ProgressDialog pd) {
        //this.pd = pd;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*pd.setMessage("불러오는중입니다. 잠시만 기다리세요.");
        pd.show();*/
    }

    @Override
    protected ArrayList<HomeItemData> doInBackground(String... strings) {
        String url = strings[0];
        ipAddress = url;

        // ========== mysql에서 데이터 불러오기 ==========
        String result = getData(url); // 받아온 JSON의 공백을 제거한 상태로 리턴 받음

        // ========== 리사이클뷰 어댑터에 사용할 아이템리스트 ==========
        homeItemDataArrayList = createHomeItemData(result);

        //pd.cancel();

        return homeItemDataArrayList;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ArrayList<HomeItemData> s) { // doInBackgroundString에서 return한 값을 받음
        super.onPostExecute(s);
    }

    private ArrayList<HomeItemData> createHomeItemData(String jsonString) {
        ArrayList<HomeItemData> tmpArray = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("itemlist");

            for(int i=0; i<jsonArray.length(); i++) {
                HomeItemData tmpHomeItemData = new HomeItemData();
                tmpHomeItemData.setTitle(jsonArray.getJSONObject(i).getString("title"));
                tmpHomeItemData.setCategory(jsonArray.getJSONObject(i).getString("category"));
                tmpHomeItemData.setSubstance(jsonArray.getJSONObject(i).getString("substance"));
                tmpHomeItemData.setPrice(jsonArray.getJSONObject(i).getString("price"));
                tmpHomeItemData.setLocation(jsonArray.getJSONObject(i).getString("location"));

                int imageCount = 0;
                for(int j=0; j<5; j++) {
                    if(jsonArray.getJSONObject(i).getString("imagepath0"+(j+1)) != "null") {
                        imageCount++;
                    }
                }

                String[] tempImgPath = new String[imageCount];
                for(int k=0; k<imageCount; k++) {
                    String tempString = jsonArray.getJSONObject(i).getString("imagepath0"+(k+1));
                    tempImgPath[k] = "" + ipAddress + "/maket_php/image/" + tempString;
                }

                tmpHomeItemData.setImagesPath(tempImgPath);
                tmpArray.add(tmpHomeItemData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tmpArray;
    }

    // ========== mysql에서 data 불러오기 ==========
    private String getData(String url) {
        try {
            URL serverURL = new URL(url + "/maket_php/connect.php");
            Log.d(TAG, "text url : " + serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) serverURL.openConnection();
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.connect();

            //int responseStatusCode = httpURLConnection.getResponseCode();

            InputStream inputStream;
            if (httpURLConnection.getResponseCode() == httpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            Log.d(TAG, "텍스트 불러오기 : " + stringBuilder.toString().trim());

            return stringBuilder.toString().trim();

        } catch (MalformedURLException e) {
            Log.d(TAG, "Error : " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "Error : " + e.toString());
            e.printStackTrace();
        }
        return null;
    }
}