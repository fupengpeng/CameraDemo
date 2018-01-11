package com.hjb.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    private String[] galleryPermissions = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };

    private String[] cameraPermissions = {
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };
    public boolean verifyPermissions(Context context, String[] grantResults) {
        for (String result : grantResults) {
            if (ActivityCompat.checkSelfPermission(context, result) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.iv_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (verifyPermissions(MainActivity.this, cameraPermissions)) {
                         takePhoto(MainActivity.this, BuildConfig.APPLICATION_ID);
                    } else{
                        ActivityCompat.requestPermissions(MainActivity.this, cameraPermissions, TAKE_PICTURE);
                    }
                } else{
                     takePhoto(MainActivity.this, BuildConfig.APPLICATION_ID);
                }

            }
        });
    }


    public static final int TAKE_PICTURE = 101;
    public static String photoPath;
    public static final int LOCAL_PICTURE = 102;
    public static void takePhoto(Activity ac, String appID) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        if (takePictureIntent.resolveActivity(ac.getPackageManager()) != null) {
            try {
                photoFile = createImageFile(ac);
            } catch (IOException ex) {
            }
            if (photoFile != null) {

                Uri photoURI;
                if (Build.VERSION.SDK_INT >= 24) {
                    photoURI = FileProvider.getUriForFile(ac,
                            appID + ".provider", photoFile);
                } else {
                    photoURI = Uri.fromFile(photoFile);
                }
                takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                ac.startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }
        // Save a file: path for use with ACTION_VIEW intents
        if (Build.VERSION.SDK_INT >= 24) {
            photoPath = String.valueOf(FileProvider.getUriForFile(ac,
                    appID + ".provider", photoFile));
        } else {
            photoPath = String.valueOf(Uri.fromFile(photoFile));
        }
    }
    private static File createImageFile(Activity ac) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = ac.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
                ImageUtil.LoadPicture(photoPath, imageView);
            }
        }
    }

}
