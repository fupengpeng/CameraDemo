package com.hjb.example.camera01;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.hjb.example.R;

public class Main01Activity extends AppCompatActivity {

    public static final int Cut_PHOTO = 1;
    public static final int SHOW_PHOTO = 2;
    public static final int PHOTO_ALBUM = 3;
    public static final int SHOW_PHOTO_ALBUM = 4;
    private Uri imageUri;
    private ImageView iv_photo;
    private Button btn_photo;
    private Button btn_photo_album;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main01);

        iv_photo = (ImageView) this.findViewById(R.id.iv_photo);
        btn_photo = (Button) this.findViewById(R.id.btn_photo);
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建File对象,用于存储选择的照片
                File outputImage = new File(Environment.getExternalStorageDirectory(), "test.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                //隐式意图启动相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                // 启动相机程序
                startActivityForResult(intent, Cut_PHOTO);

            }
        });
        btn_photo_album = (Button) this.findViewById(R.id.btn_photo_album);
        btn_photo_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建File对象,用于存储选择的照片
                File outputImage = new File(Environment.getExternalStorageDirectory(), "outputTest.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                //允许裁剪
                intent.putExtra("crop", true);
                //允许缩放
                intent.putExtra("scale", true);
                //图片的输出位置
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,PHOTO_ALBUM);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Cut_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    // 启动裁剪
                    startActivityForResult(intent, SHOW_PHOTO);
                }
                break;
            case SHOW_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        // 将裁剪后的照片显示出来
                        iv_photo.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PHOTO_ALBUM:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(uri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    // 启动裁剪
                    startActivityForResult(intent, SHOW_PHOTO_ALBUM);
                }
                break;
            case SHOW_PHOTO_ALBUM:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        // 将裁剪后的照片显示出来
                        iv_photo.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
