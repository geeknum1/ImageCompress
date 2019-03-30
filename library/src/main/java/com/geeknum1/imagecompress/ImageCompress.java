package com.geeknum1.imagecompress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.geeknum1.imagecompress.utils.BitmapUtil;

/**
 * 图片压缩
 *
 * @author geeknum1
 * @version V1.0.0
 */
public class ImageCompress implements Handler.Callback {
    private Builder mBuilder;
    private Handler mHandler;
    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;

    private ImageCompress(Builder builder) {
        this.mBuilder = builder;
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    private void launch() {

        if ((mBuilder.filePath == null) && mBuilder.listener != null) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, new NullPointerException("image file cannot be null")));
            return;
        }
        if (TextUtils.isEmpty(mBuilder.targetDir) && mBuilder.listener != null) {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, new NullPointerException("targetDir cannot be null")));
            return;
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String targetCompressPath = getImageCacheFile(checkSuffix(mBuilder.filePath));
                    String resultStr = CompressCore.saveBitmap(setPictureDegree(mBuilder.filePath), targetCompressPath, true);
                    if ("1".equals(resultStr)) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, targetCompressPath));
                    } else {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, new RuntimeException(resultStr)));
                    }
                } catch (final Exception e) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, e));
                }
            }
        }).start();

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mBuilder.listener == null) {
            return false;
        }
        switch (msg.what) {
            case MSG_COMPRESS_START:
                mBuilder.listener.onStart();
                break;
            case MSG_COMPRESS_SUCCESS:
                mBuilder.listener.onSuccess(msg.obj + "");
                break;
            case MSG_COMPRESS_ERROR:
                mBuilder.listener.onError((Throwable) msg.obj);
                break;
        }
        return false;
    }


    private String getImageCacheFile(String suffix) {
        return mBuilder.targetDir + "/" +
                System.currentTimeMillis() +
                (int) (Math.random() * 1000) +
                (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);
    }

    private String checkSuffix(String path) {
        if (TextUtils.isEmpty(path) || !path.contains(".")) {
            return ".jpg";
        }
        return path.substring(path.lastIndexOf("."), path.length());
    }

    public static class Builder {
        private Context context;
        private String targetDir;
        private OnCompressListener listener;
        private String filePath;


        public Builder setTargetDir(String targetDir) {
            this.targetDir = targetDir;
            return this;
        }


        private Builder(Context context) {
            this.context = context;
        }

        private ImageCompress build() {
            return new ImageCompress(this);
        }


        public Builder load(String localPath) {
            this.filePath = localPath;
            return this;
        }

        public Builder setOnCompressListener(OnCompressListener listener) {
            this.listener = listener;
            return this;
        }

        public void launch() {
            build().launch();
        }
    }

    public interface OnCompressListener {
        void onStart();

        void onSuccess(String filePath);

        void onError(Throwable e);
    }

    private Bitmap setPictureDegree(String filePath) {
        //旋转图片
        int photoDegree = BitmapUtil.readPictureDegree(filePath);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (photoDegree != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(photoDegree);

            // 创建新的图片
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        return bitmap;
    }

}