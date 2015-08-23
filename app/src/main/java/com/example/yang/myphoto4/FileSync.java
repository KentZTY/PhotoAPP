package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

/**
 * Created by Ree on 2015/8/23.
 */
public class FileSync extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.null_layout);

    }

    public String getDiskCacheDir(Context context){
        String cachePath = null;
        //Environment.getExtemalStorageState() 获取SDcard的状态
        //Environment.MEDIA_MOUNTED 手机装有SDCard,并且可以进行读写
        //getAbsolutePath()

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                ||!Environment.isExternalStorageRemovable()){
            cachePath=context.getExternalCacheDir().getPath();
        }else{
            cachePath=context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /*
    File saveFile=new File("/sdcard/zhzhg.txt");
    或:File sdCardDir=new File("/sdcard");//获取SD卡目录
    File saveFile=new File(sdCardDir,"zhzhg.txt");
    FileOutputStream outStream = new FileOutputStream(saveFile);
    outStream.write("文件的读写".getBytes());
    outStream.close();

    URL url=new URL(图片网址);
    URLConection conn=url.openConnection();
    conn.connect();
    InputStream is=conn.getInputStream();
    Bitmap bmp=BitmapFactory.decodeSteam(is);
    imageview.setImageBitmap(bm);
    */
}
