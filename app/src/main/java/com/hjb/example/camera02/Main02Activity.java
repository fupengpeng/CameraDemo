package com.hjb.example.camera02;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hjb.example.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Main02Activity extends AppCompatActivity implements View.OnClickListener{


    Button btnCamera,btnPhoto;
    ImageView ivCamera,ivPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main02);
        btnCamera = (Button) findViewById(R.id.btn_camera);
        btnPhoto = (Button) findViewById(R.id.btn_photo);

        ivCamera = (ImageView) findViewById(R.id.image_camera);
        ivPhoto = (ImageView) findViewById(R.id.image_photo);
        btnCamera.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera:
                camera();
                break;
            case R.id.btn_photo:
                photo();

                break;
        }
    }

    private void photo() {
        //判断是否有相机和存储权限
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
            //如果没有授权，则请求授权
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }else {
            //如果有授权，则进行下一步操作
            // TODO: 2018/1/11 拍照等其他操作
//            // create Intent to take a picture and return control to the calling application
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//            //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
//            File file = new File(Environment.getExternalStorageDirectory().getPath(), "camera.png");
//
//            // set the image file name    android 7.0 之后不能再直接使用此方法
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//
//            // start the image capture Intent
//            startActivityForResult(intent, 1);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager())!=null){
                File photoFile = null;
                try {
                    photoFile = createImageFile();//创建临时图片文件，方法在下面
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    //FileProvider 是一个特殊的 ContentProvider 的子类，
                    //它使用 content:// Uri 代替了 file:/// Uri. ，更便利而且安全的为另一个app分享文件
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.sat.android.fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, 2);
                }
            }

            
        }


    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //.getExternalFilesDir()方法可以获取到 SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    /*File storageDir = new File(Environment.getExternalStorageDirectory(), "images");
    if (!storageDir.exists()) storageDir.mkdirs();*/
        Log.d("TAH",storageDir.toString());
        //创建临时文件,文件前缀不能少于三个字符,后缀如果为空默认未".tmp"
        File image = File.createTempFile(
                imageFileName,  /* 前缀 */
                ".jpg",         /* 后缀 */
                storageDir      /* 文件夹 */
        );
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    private void camera() {

        //checkSelfPermission 检测有没有 权限
        //PackageManager.PERMISSION_GRANTED 有权限
        //PackageManager.PERMISSION_DENIED  拒绝权限
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //权限发生了改变 true  //  false 小米
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                new AlertDialog.Builder(this).setTitle("title")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 请求授权
                                ActivityCompat.requestPermissions(Main02Activity.this,new String[]{Manifest.permission.CAMERA},1);

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
            }else {
                ActivityCompat.requestPermissions(Main02Activity.this,new String[]{Manifest.permission.CAMERA},1);

            }

        }else{

            camear();

        }

    }
    /**
     *
     * @param requestCode
     * @param permissions 请求的权限
     * @param grantResults 请求权限返回的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            // camear 权限回调
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                // 表示用户授权
                Toast.makeText(this, " user Permission" , Toast.LENGTH_SHORT).show();

                camear();


            } else {

                //用户拒绝权限
                Toast.makeText(this, " no Permission" , Toast.LENGTH_SHORT).show();

            }



        }else if (requestCode == 2 ){

            // 表示用户授权
            Toast.makeText(this, " 获取到了相机权限和存储权限" , Toast.LENGTH_SHORT).show();
        }

    }

    public void camear(){
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



//    public void onPermissionRequests(String permission, OnBooleanListener onBooleanListener) {
//        onPermissionListener = onBooleanListener;
//        Log.d("MainActivity", "0");
//        if (ContextCompat.checkSelfPermission(this,
//                permission)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Should we show an explanation?
//            Log.d("MainActivity", "1");
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.READ_CONTACTS)) {
//                //权限已有
//                onPermissionListener.onClick(true);
//            } else {
//                //没有权限，申请一下
//                ActivityCompat.requestPermissions(this,
//                        new String[]{permission},
//                        1);
//            }
//        }else{
//            onPermissionListener.onClick(true);
//            Log.d("MainActivity", "2"+ContextCompat.checkSelfPermission(this,
//                    permission));
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 1) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //权限通过
//                if (onPermissionListener != null) {
//                    onPermissionListener.onClick(true);
//                }
//            } else {
//                //权限拒绝
//                if (onPermissionListener != null) {
//                    onPermissionListener.onClick(false);
//                }
//            }
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
}
