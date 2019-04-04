package com.example.kaoqin.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionUtil {

    // 外部存储读写权限
    private static final int REQUEST_EXRERANAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // 验证是否有外存读写权限
    public static boolean verifyStoragePermissions(Context context){
        // 检查是否有写权限
        int permission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED){
            return false;
        }else
            return true;
    }

    // 增加外存读写权限
    public static void addStoragepermissions(Activity activity){
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                REQUEST_EXRERANAL_STORAGE);
    }
}
