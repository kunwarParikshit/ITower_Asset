package com.isl.api;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.isl.dao.DataBaseHelper;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static IApiRequest iApiRequest;
    private static String url;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void init(Class<IApiRequest> requestClass, Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        DataBaseHelper db = new DataBaseHelper(context);
        db.open();
        url = db.getModuleIP("Audits");
        if (url.contains("~")) {
            url = url.substring(0, url.indexOf("~"));
        }
        if (url.contains("http") || url.contains("https")) {

        } else {
            //url="https://"+url+"/api/";
            url = "https://" + url + "/";
        }
        Log.d("URL_IP-->", url);


        db.close();

        OkHttpClient.Builder okclient = new OkHttpClient.Builder();
        okclient.connectTimeout(120, TimeUnit.SECONDS);
        okclient.readTimeout(120, TimeUnit.SECONDS);
        okclient.writeTimeout(120, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okclient.build())
                .build();

        iApiRequest = retrofit.create(requestClass);
    }

    public static IApiRequest getRequest() {
        return iApiRequest;
    }
}
