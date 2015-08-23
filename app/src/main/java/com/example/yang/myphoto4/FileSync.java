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
        //Environment.getExtemalStorageState() ��ȡSDcard��״̬
        //Environment.MEDIA_MOUNTED �ֻ�װ��SDCard,���ҿ��Խ��ж�д
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
    ��:File sdCardDir=new File("/sdcard");//��ȡSD��Ŀ¼
    File saveFile=new File(sdCardDir,"zhzhg.txt");
    FileOutputStream outStream = new FileOutputStream(saveFile);
    outStream.write("�ļ��Ķ�д".getBytes());
    outStream.close();

    URL url=new URL(ͼƬ��ַ);
    URLConection conn=url.openConnection();
    conn.connect();
    InputStream is=conn.getInputStream();
    Bitmap bmp=BitmapFactory.decodeSteam(is);
    imageview.setImageBitmap(bm);
    */
}
