package com.example.projectapp_201202.gps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class gpstojusoJSON2 {
    String Juso="";
    JSONArray result;
    JSONObject base,land,addition0;
    JSONObject area[]=new JSONObject[4];
    public gpstojusoJSON2(String jsontext) throws IOException, JSONException {
        base=new JSONObject(jsontext);
        result=base.getJSONArray("results");
        land= (JSONObject) result.getJSONObject(0).get("land");
        addition0=land.getJSONObject("addition0");
        Juso=addition0.getString("value");
    }
    public String getJuso() throws JSONException {
        return Juso;
    }

}
