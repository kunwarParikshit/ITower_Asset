package com.isl.workflow.modal;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImagesList {
    String latitude;
    String longitude;
    String name;
    String path;
    String type;
    String tag;
    String time;
    String filedkey;
    Uri imageUri;
    Bitmap bitmap;

    public ImagesList(String latitude, String longitude, String name, String path, String type, String tag, String time, String filedkey,Uri imageuri,Bitmap bitmap) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.path = path;
        this.type = type;
        this.tag = tag;
        this.time = time;
        this.filedkey = filedkey;
        this.imageUri = imageuri;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFiledkey() {
        return filedkey;
    }

    public void setFiledkey(String filedkey) {
        this.filedkey = filedkey;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }





}
