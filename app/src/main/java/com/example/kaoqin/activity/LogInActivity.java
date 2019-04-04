package com.example.kaoqin.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kaoqin.Data.User;
import com.example.kaoqin.R;
import com.example.kaoqin.utils.CommonRequest;
import com.example.kaoqin.utils.CommonResponse;
import com.example.kaoqin.utils.Consts;
import com.example.kaoqin.utils.HttpUtil;
import com.example.kaoqin.utils.SharedPreferenceUtil;
import com.example.kaoqin.utils.UserManager;
import com.example.kaoqin.utils.Util;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class LogInActivity extends BaseActivity {

    private ProgressBar progressBar;
    private Button loginBtn;
    private EditText accountText;
    private EditText passwordText;
    private CheckBox isRememberPwd;
    private CheckBox isAutoLogin;

    private String account;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
        setListeners();

        // 自动填充
        SharedPreferenceUtil spu = new SharedPreferenceUtil(this);
        Boolean isRemember = (Boolean) spu.getParam("isRememberPwd",false);
        Boolean isAutoLogin = (Boolean) spu.getParam("isAutoLogin",false);
        // SharedPreference获取用户账号密码，存在则填充
        String account = (String) spu.getParam("account","");
        String pwd = (String)spu.getParam("pwd","");
        if(!account.equals("") && !pwd.equals("")){
            if(isRemember){
                accountText.setText(account);
                passwordText.setText(pwd);
                isRememberPwd.setChecked(true);
            }
            if(isAutoLogin)
                Login();
        }

    }

    private void initComponents() {
        loginBtn = findViewById(R.id.login);
        accountText = findViewById(R.id.account);
        passwordText = findViewById(R.id.password);
        isRememberPwd = findViewById(R.id.login_remember);
        isAutoLogin = findViewById(R.id.login_auto);
        progressBar = findViewById(R.id.progressbar);

        LitePal.getDatabase(); //建立数据库
        UserManager.clear();
    }

    private void setListeners() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }

    /**
     * POST方式Login
     */
    private void Login(){
        // 创建请求体对象
        CommonRequest request = new CommonRequest();

        // 前端参数校验，防SQL注入
        account = Util.StringHandle(accountText.getText().toString());
        password = Util.StringHandle(passwordText.getText().toString());

        // 检查数据格式是否正确
        final String resMsg = checkDataVaild(account,password);
        if (!resMsg.equals("")){
            showResponse(resMsg);
            return;
        }

        progressBar.setVisibility(View.VISIBLE); //显示进度条
        OptionHandle(account, password); //处理自动登录及记住密码

        // 填充参数
        request.addRequestParam("account", account);
        request.addRequestParam("pwd", password);

        // Post请求
        HttpUtil.sendPost(Consts.URL_Login, request.getJsonStr(), new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showResponse("Network ERROR");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                CommonResponse res = new CommonResponse(response.body().string());
                String resCode = res.getResCode();
                String resMsg = res.getResMsg();
                //登录成功 判断账户是否激活
                if (resCode.equals(Consts.SUCCESSCODE_LOGIN_VAILD)){
                    // 账户已激活
                    // 查找本地数据库中是否已存在当前用户，不存在则新建用户并写入
                    User user = DataSupport.where("account=?",account).findFirst(User.class);
                    if (user == null){
                        user = new User();
                        user.setAccount(account);
                        user.setPassword(password);
                        user.save();
                    }
                    UserManager.setCurrentUser(user); // 设置当前用户

                    autoStartActivity(MainActivity.class);
                }
                else if(resCode.equals(Consts.SUCCESSCODE_LOGIN__NOT_VAILD)){
                    // 账户未激活
                    User user = DataSupport.where("account=?",account).findFirst(User.class);
                    if (user == null){
                        user = new User();
                        user.setAccount(account);
                        user.setPassword(password);
                        user.save();
                    }
                    UserManager.setCurrentUser(user);

                    autoStartActivity(VaildActivity.class);
                }
                showResponse(resMsg);
            }
        });
    }

    private void OptionHandle(String account, String pwd) {
        SharedPreferences.Editor editor = getSharedPreferences("UserData", MODE_PRIVATE).edit();
        SharedPreferenceUtil spu = new SharedPreferenceUtil(this);
        if (isRememberPwd.isChecked()){
            editor.putBoolean("isRememberPwd",true);
            spu.setParam("account", account);
            spu.setParam("pwd", pwd);
        }else {
            editor.putBoolean("isRememberPwd",false);
        }
        if (isAutoLogin.isChecked()){
            editor.putBoolean("isAutoLogin",true);
        }else {
            editor.putBoolean("isAutoLogin",false);
        }
        editor.apply();
    }

    private void showResponse(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LogInActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String checkDataVaild(String account, String pwd) {
        if (TextUtils.isEmpty(account) | TextUtils.isEmpty(pwd))
            return "账号密码不能为空";
        if (account.length()!=8)
            return "请输入有效的用户名";
        return "";
    }
}
