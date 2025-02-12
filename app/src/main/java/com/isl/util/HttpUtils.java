package com.isl.util;

import com.google.gson.Gson;
import com.isl.workflow.modal.UploadDocDetail;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class HttpUtils {

    private static Gson gson = new Gson();

    public static String httpPostRequest(String url,List<NameValuePair> parameterList) {

        //HttpsTrustManager.allowAllSSL();
        HttpPost httppost = null;
        BufferedReader reader = null;
        StringBuffer responseBfr = new StringBuffer();
        if(url.contains("https") || url.contains("http")){
            httppost = new HttpPost(url);
        }else{
            httppost = new HttpPost("http://"+url);
        }

        //System.out.println("HTTP Post Request on  - "+httppost.getURI());
        try {
            HttpClient httpClient = new DefaultHttpClient();
            httppost.setEntity(new UrlEncodedFormEntity(parameterList));
            HttpResponse response = httpClient.execute(httppost);
            reader = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String output = null;

            while ((output = reader.readLine()) != null) {
                responseBfr.append(output);
            }

            System.out.println("*****************************************************");
            System.out.println("HTTP Post Request on  - "+httppost.getURI());
            System.out.println("Respnse Code - "+response.getStatusLine().getStatusCode());
            System.out.println("Respnse  - "+responseBfr.toString());
            System.out.println("*****************************************************");
        } catch (Exception e) {
            e.printStackTrace();
            //e.getMessage();
            //e.getMessage();
            //DataBaseHelper dbHelper = new DataBaseHelper(context);
            //AppPreferences mAppPreferences = new AppPreferences(context);
            //dbHelper.open();
            //dbHelper.insertLog("errorlog="+e.getMessage()+"-date="+date()+"-userId="+mAppPreferences.getUserId());
            //dbHelper.close();
        }finally{
            if(reader!=null){
                try{
                    reader.close();
                }catch(Exception exp){
                    exp.printStackTrace();
                }
            }
        }
        return responseBfr.toString();
    }

    public static String httpGetRequest(String url) throws Exception {
        StringBuffer responseBfr = new StringBuffer();
        //HttpsTrustManager.allowAllSSL();
        HttpGet request= null;
        BufferedReader br = null;

        try{
            if(url.contains("https") || url.contains("http")){
                request = new HttpGet(url);
            }else{
                request = new HttpGet("http://"+url);
            }

            HttpClient httpClient = new DefaultHttpClient();

            HttpResponse response = httpClient.execute(request);

            br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String output;
            while ((output = br.readLine()) != null) {
                responseBfr.append(output);
            }
            System.out.println("*****************************************************");
            System.out.println("HTTP Get Request on  - "+request.getURI());
            System.out.println("Respnse Code - "+response.getStatusLine().getStatusCode());
            System.out.println("Respnse  - "+responseBfr.toString());
            System.out.println("*****************************************************");

            if (response.getStatusLine().getStatusCode() != 201 && response.getStatusLine().getStatusCode() != 200
                    && response.getStatusLine().getStatusCode() != 202) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            return responseBfr.toString();
        } finally{
            if(br!=null){
                try{
                    br.close();
                }catch(Exception exp){
                    exp.printStackTrace();
                }
            }
        }
    }

    public static String httpGetRequest(String url, List<NameValuePair> paramList) throws Exception {

        String queryString = URLEncodedUtils.format(paramList, "utf-8");
        url+=queryString;

        return httpGetRequest(url);
    }

    public static String httpMultipartBackground(String url, UploadDocDetail imgDetail, List<UploadDocDetail> imgDetailList, String otherDetail) {

        String str = "";
        InputStream is = null;
       // HttpsTrustManager.allowAllSSL();
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost;

        if (url.contains( "https" ) || url.contains( "http" )) {
            httppost = new HttpPost( url );
        } else {
            httppost = new HttpPost( "http://" + url );
        }

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        String imgJson = "";
        if(imgDetailList!=null){

            try {
                if (imgDetailList.size()>0) {
                    for (UploadDocDetail img : imgDetailList) {
                        File file = null;
                        file = new File(img.getPath());
                        builder.addBinaryBody( "image-"+img.getName(), file );
                    }

                }

                imgJson = gson.toJson(imgDetailList);

            } catch (Exception e) {
                e.printStackTrace();
            }

            imgJson = gson.toJson(imgDetailList);

        } else{

            try {
                File file = null;
                file = new File(imgDetail.getPath());
                builder.addBinaryBody( "image-"+imgDetail.getName(), file );
                imgJson = gson.toJson(imgDetail);
            } catch (Exception e) {}

        }

        try {


            builder.addTextBody( "data", otherDetail);
            builder.addTextBody( "imgdtl", imgJson);
            httppost.setEntity(builder.build());

            HttpResponse httpResponse = httpclient.execute( httppost );
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader( is ) );
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append( line + "\n" );
            }
            is.close();
            str = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}
