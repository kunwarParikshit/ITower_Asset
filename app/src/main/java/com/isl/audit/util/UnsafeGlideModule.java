package com.isl.audit.util;


import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.LibraryGlideModule;;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

@GlideModule
public class UnsafeGlideModule extends LibraryGlideModule {
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        OkHttpClient.Builder okclient = new OkHttpClient.Builder();
        okclient.connectTimeout(120, TimeUnit.SECONDS);
        okclient.readTimeout(120, TimeUnit.SECONDS);
        okclient.writeTimeout(120, TimeUnit.SECONDS);
        registry.replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(okclient.build()));
    }
}
