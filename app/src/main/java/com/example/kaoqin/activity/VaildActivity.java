package com.example.kaoqin.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kaoqin.R;
import com.example.kaoqin.utils.CommonResponse;
import com.example.kaoqin.utils.Consts;
import com.example.kaoqin.utils.FileProviders;
import com.example.kaoqin.utils.HttpUtil;
import com.example.kaoqin.utils.PermissionUtil;
import com.example.kaoqin.utils.PhotoUtils;
import com.example.kaoqin.utils.UserManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;


public class VaildActivity extends BaseActivity implements View.OnLongClickListener {

    public static final int PHOTO_REQUEST_CAREMA = 1; // 请求拍照code
    public static final int CROP_PHOTO = 2; // 请求剪裁code

    private ProgressBar progressBar;
    private Button takePicBtn;
    private Button uploadPicBtn;
    private ImageView pictureView;


    private String account;
    private String filename;
    private String filename_2;
    private String name;
    private Uri fileuri;
    private Uri cropuri;
    private File mPhotoFile;
    private File mPhotoFile_2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaild);

        initComponents();
        setListeners();

        filename = Environment.getExternalStorageDirectory().getAbsolutePath()+"/photos";
        filename_2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/crop_photos";
    }

    private void initComponents() {
        takePicBtn = findViewById(R.id.takePicture);
        uploadPicBtn = findViewById(R.id.uploadPicture);
        progressBar = findViewById(R.id.progressbar);
        pictureView = findViewById(R.id.picture);
//        mFlowLayout = (com.nex3z.flowlayout.FlowLayout) findViewById(R.id.mFlowLayout);

        account = UserManager.getCurrentUser().getAccount();

    }

    private void setListeners() {
        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takephoto();
            }
        });

        uploadPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *
                 */

                uploadphoto();

            }
        });
    }

//    private void getPressFile(Uri path) {
//        File file = new File(path.getPath());//创建文件
//        Luban.get(this)
//                .load(file)           // 传入要压缩的图片
//                .putGear(Luban.THIRD_GEAR)   // 设定压缩档次,默认三挡自己可以选择
//                .setCompressListener(new OnCompressListener() { // 设置回调
//                    @Override
//                    public void onStart() {
//                        // 压缩开始前调用,可以在方法内启动 loading UI
//                    }
//                    @Override
//                    public void onSuccess(File file) {
//                        // 压缩成功后调用,返回压缩后的图片文件
//                        uploadphoto(file);
//                    }
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//                }).launch();
//    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // 在这保存name的数据
        outState.putString("name", name);
    }

    // 读取数据
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        name = savedInstanceState.getString("name");
    }


    private void uploadphoto() {
        try{
            final Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(cropuri));

            progressBar.setVisibility(View.VISIBLE); //显示进度条

            String imgName = mPhotoFile_2.getName();
//            System.out.println("bababa   "+imgName);

            HttpUtil.uploadImage(Consts.URL_UploadImg, bitmap, imgName, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    showResponse("Network ERROR!");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    CommonResponse res = new CommonResponse(response.body().string());
                    String resCode = res.getResCode();
                    String resMsg = res.getResMsg();
                    // 上传成功
                    if (resCode.equals(Consts.SUCCESSCODE_UPLOADIMG)){
                        // 跳转到更改初始密码页
                        autoStartActivity(MainActivity.class);
                    }
                    else if (resCode.equals(Consts.CANNOT_DETECT_FACE)){
                        autoStartActivity(MainActivity.class);
                    }
                    showResponse(resMsg);
                }
            });
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    private void showResponse(final String msg) {
        Log.e("VaildActivity", msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(VaildActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void takephoto(){
        // 判断是否有相机
        boolean b = PhotoUtils.hasCamera(VaildActivity.this);
        if (b){
            // 判断存储卡是否可用，可用进行存储
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (hasSdcard()){
                name = getPhotoFileName();
                File mfile = new File(filename);
                if (!mfile.exists()){
                    mfile.mkdir();
                }
                File mfile_2 = new File(filename_2);
                if (!mfile_2.exists()){
                    mfile_2.mkdir();
                }
                mPhotoFile = new File(filename, name);
                mPhotoFile_2 = new File(filename_2,name);

                // 检查是否有存储权限，以免崩溃
                if (!PermissionUtil.verifyStoragePermissions(this)){
                    // 申请WRITE_EXTERANAL_STORAGE权限
                    Toast.makeText(this,"请开启存储权限", Toast.LENGTH_SHORT).show();
                    PermissionUtil.addStoragepermissions(this);
                }

                fileuri = FileProviders.getUriForFile(this, mPhotoFile, captureIntent);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileuri);


            }
            startActivityForResult(captureIntent, PHOTO_REQUEST_CAREMA);
        } else {
            Toast.makeText(VaildActivity.this, "系统无相机", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断sdcard是否被挂载
     * @return
     */
    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HH-mm-ss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 获取到相机拍的照片

        switch (requestCode){
            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_OK){
                    Intent intent = new Intent("com.android.camera.action.CROP");

                    intent.setDataAndType(PhotoUtils.getImageContentUri(this,mPhotoFile), "image/*");
                    intent.putExtra("crop",true);
                    intent.putExtra("scale",true);
                    intent.putExtra("return-data",true);
                    cropuri = FileProviders.getUriForFile(this, mPhotoFile_2, intent);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cropuri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动剪裁程序

                }
            case CROP_PHOTO:
                if (resultCode == RESULT_OK){
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(cropuri));
                        pictureView.setImageBitmap(bitmap);
                        pictureView.setOnLongClickListener(this);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
        }

    }

    @Override
    public boolean onLongClick(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除");
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                pictureView.setImageBitmap(null);
            }
        });
        builder.create().show();
        return true;
    }

}
