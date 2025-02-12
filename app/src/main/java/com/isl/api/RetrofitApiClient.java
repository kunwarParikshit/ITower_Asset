package com.isl.api;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.isl.dao.DataBaseHelper;
import com.isl.dao.cache.AppPreferences;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiClient {
    private static IApiRequest iApiRequest;
    private static String url;
    private static AppPreferences mAppPreferences;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void init(Class<IApiRequest> requestClass, Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        DataBaseHelper db = new DataBaseHelper(context);
        db.open();
        //Api Changes by Avdhesh
        url = db.getModuleIP("Preventive");
        if (url.equalsIgnoreCase("0")) {
            url = mAppPreferences.getConfigIP()+"/";
        } else {
            url = url + "/";
        }
        if (url.contains("~")) {
            url = url.substring(0, url.indexOf("~"));
        }
        if (url.contains("http") || url.contains("https")) {

        } else {

            url = "http://" + url + "/";
        }
        Log.d("URL_IP-->", url);


        db.close();
        OkHttpClient.Builder okclient = new OkHttpClient.Builder();
        okclient.connectTimeout(120, TimeUnit.SECONDS);
        okclient.readTimeout(120, TimeUnit.SECONDS);
        okclient.writeTimeout(120, TimeUnit.SECONDS);

            if(iApiRequest == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okclient.build())
                        .build();

                iApiRequest = retrofit.create(requestClass);
            }

    }

    public static IApiRequest getRequest() {
        return iApiRequest;
    }
}
