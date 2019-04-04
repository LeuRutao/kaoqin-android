package com.example.kaoqin.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 功能类，封装一些功能函数
 */

public class Util {

    /**
     * 字符串处理，防止SQL注入
     */
    public static String StringHandle(String input){
        String output;
        // 将包含有 单引号(')，分号(;) 和 注释符号(--)的语句替换掉
        output = input.trim().replaceAll(".*([';]+|(--)+).*", " ");
        return output;
    }

    /**
     * Toast封装
     * @param context
     * @param msg
     */
    public static void makeToast(Context context, String msg){
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
    }
}
