package com.example.kaoqin.utils;

public class Consts {
    private static String URL = "http://10.0.2.2:5000/";
    public static String URL_Login = URL + "login";
    public static String URL_UploadImg = URL + "uploadImg";
    public static String URL_ModifyPwd = URL + "modifyPwd";



    // 服务器代码
    public static String ERRORCODE_PWD = "201";
    public static String ERRORCODE_ACCOUNTNOTEXIST = "202";

    public static String SUCCESSCODE_LOGIN_VAILD = "100";
    public static String SUCCESSCODE_LOGIN__NOT_VAILD = "101";
    public static String CANNOT_DETECT_FACE = "103";
    public static String SUCCESSCODE_UPLOADIMG = "104";
    public static String SUCCESSCODE_VAILD = "105";
    public static String SUCCESSCODE_MODIFYPWD = "106";






    // 代码对应信息
    public static String ERRORMSG_PWD = "密码错误";
    public static String ERRORMSG_ACCOUNTNOTEXIST = "账号不存在";

    public static String SUCCESSMSG_LOGIN_VAILD = "登录成功，且账号已激活";
    public static String SUCCESSMSG_LOGIN_NOT_VAILD = "登录成功，且账号未激活";

}
