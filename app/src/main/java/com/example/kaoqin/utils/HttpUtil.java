package com.example.kaoqin.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();

    /**
     * POST请求（参数为JSON格式）
     * @param address
     * @param json
     * @param callback
     */
    public static void sendPost(String address, String json, okhttp3.Callback callback) {
        System.out.println(json);
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        RequestBody requestBody = RequestBody.create(JSON, json);
        //创建一个请求对象
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 上传照片——方法一：将图片加密转为string作为参数之一提交
     * @param address
     * @param bitmap
     * @param callback
     */
    public static void uploadImage(String address, Bitmap bitmap, String imgName, okhttp3.Callback callback){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//将Bitmap转成Byte[]
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, baos);//压缩
        String imgStr = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);//加密转换成String

        CommonRequest commonrequest = new CommonRequest();
        commonrequest.addRequestParam("account",UserManager.getCurrentUser().getAccount());
        commonrequest.addRequestParam("imgName", imgName);
        commonrequest.addRequestParam("vaildImg",imgStr);
        RequestBody requestBody = RequestBody.create(JSON,commonrequest.getJsonStr());

        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 上传照片——方法二：使用Multipart上传整个文件
     * @param address
     * @param file
     * @param callback
     */
    public static void uploadImage(String address, File file, okhttp3.Callback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("avatar", file.getName(),
                        RequestBody.create(MediaType.parse("image/*"), file))
                .addFormDataPart("account",UserManager.getCurrentUser().getAccount());
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }



}
