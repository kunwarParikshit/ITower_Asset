package com.isl.api;

import android.util.Base64;
import android.util.Log;

import com.isl.dao.cache.AppPreferences;
import com.isl.itower.MyApp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request newRequest;

        String id=new AppPreferences(MyApp.getAppContext()).getLoginId();
        String pas=new AppPreferences(MyApp.getAppContext()).getPassword();
        if(getAuthToken(id,pas) != null){
            newRequest = originalRequest.newBuilder().addHeader("Authorization", getAuthToken(id,pas)).build();
        }else{
            newRequest = originalRequest;
        }
        if(newRequest != null && newRequest.body() != null){
            Log.d("REQ_LENGTH-->", newRequest.body().contentLength()+"");
        }

        Log.d("Request_URL-->", newRequest.url().toString());

        Response response = chain.proceed(newRequest);
        Log.d("Response-->", response.body().source().toString());

        return response;
    }
    public static String getAuthToken(String id, String pas) {
        byte[] data = new byte[0];
        try {
            //data = ("postgres" + ":" + "11@").getBytes("UTF-8");
            data = (id + ":" + pas).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }
}
