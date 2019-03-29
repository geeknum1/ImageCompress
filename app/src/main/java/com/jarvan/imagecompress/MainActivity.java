package com.jarvan.imagecompress;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.geeknum1.imagecompress.ImageCompress;

import java.io.File;



/**
 * @Description TODO
 * @Class MainActivity
 * @author geeknum1
 * @version V1.0.0
 */
public class MainActivity extends Activity implements CameraCore.CameraResult{
    private Button choose_image,camera_image;
    private CameraProxy cameraProxy;
    private ImageView choose_bit;
    /** SD卡根目录 */
    private final String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath()+"/picture/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //压缩后保存临时文件目录
        File tempFile = new File(externalStorageDirectory);
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }
        cameraProxy = new CameraProxy(this, MainActivity.this);
        choose_image = findViewById(R.id.choose_image);
        choose_image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cameraProxy.getPhoto2Album();
            }
        });
        choose_bit = findViewById(R.id.choose_bit);
        camera_image = findViewById(R.id.camera_image);
        camera_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String cameraPath = externalStorageDirectory+System.currentTimeMillis()/1000+".jpg";
                cameraProxy.getPhoto2Camera(cameraPath);
            }
        });
    }

    //拍照选图片成功回调
    @Override
    public void onCameraSuccess(final String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            ImageCompress.with(this)
                    //.load(BitmapFactory.decodeFile(filePath))//1、加载需要压缩的bitmap图片
                    .load(filePath)// 2、加载需要压缩的图片路径
                    .setTargetDir(externalStorageDirectory)//压缩后的存放路径
                    .maxSize(2048)//图片压缩后允许的最大size，不设置默认值为1024kb
                    .maxHeight(2000)//图片压缩后允许的最大高度、不设置默认值为1280，
                    .maxWidth(1400)//图片压缩后允许的最大宽度、不设置默认值为960
                    //当maxSize,maxHeight,maxWidth都大于原图宽、高、大小时，则为原图jni压缩，以上设置均无效（实际压缩后大小不可控），否则先质量压缩后jni压缩
                    .setOnCompressListener(new ImageCompress.OnCompressListener() {
                        @Override
                        public void onStart() {
                            Log.e("compress","onStart");
                        }
                        @Override
                        public void onSuccess(String filePath) {
                            Log.e("compress","onSuccess="+filePath);
                            choose_bit.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        }
                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Log.e("compress","onError");
                        }
                    }).launch();
        }
    }

    //拍照选图片失败回调
    @Override
    public void onCameraFail(String message) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MPermissionsUtils.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        cameraProxy.onResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        cameraProxy.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        cameraProxy.onSaveInstanceState(outState);
    }

}

