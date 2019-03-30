# ImageCompress

- 使用方式

```java
compile 'com.geeknum1:imagecompress:1.0.2' 或者 implementation project(':library')
```
- 样例
```java
  ImageCompress.with(this)
             .load(filePath)//1、加载需要压缩的bitmap图片
             .setTargetDir(externalStorageDirectory)//压缩后的存放路径
             .setOnCompressListener(new ImageCompress.OnCompressListener() {
                 @Override
                 public void onStart() {
                     Log.e("compress", "onStart");
                 }

                 @Override
                 public void onSuccess(String filePath) {
                     Log.e("compress", "onSuccess=" + filePath);
                     choose_bit.setImageBitmap(BitmapFactory.decodeFile(filePath));
                 }

                 @Override
                 public void onError(Throwable e) {
                     e.printStackTrace();
                     Log.e("compress", "onError");
                 }
             }).launch();
```


- 图片 
        
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190329232348540.jpg)
                    
