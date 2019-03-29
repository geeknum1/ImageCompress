# ImageCompress

- 使用方式

```java
compile 'com.geeknum1:imagecompress:1.0.1' 或者 implementation project(':library')
```
- 样例
```java
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
```


- 图片 
        
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190329232348540.jpg)
                    
