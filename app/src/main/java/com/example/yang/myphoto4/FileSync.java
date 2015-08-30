package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Ree on 2015/8/23.
 */
public class FileSync extends Activity {
    private static final String Sync_URL = "http://raptor.kent.ac.uk/~wz57/Ree/Sync.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_sync);

    }

    public String getDiskCacheDir(Context context){
        String cachePath = null;
        //Environment.getExtemalStorageState()
        //Environment.MEDIA_MOUNTED
        //getAbsolutePath()

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                ||!Environment.isExternalStorageRemovable()){
            cachePath=context.getExternalCacheDir().getPath();
        }else{
            cachePath=context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    /*
    File saveFile=new File("/sdcard/zhzhg.txt");
    or:File sdCardDir=new File("/sdcard");//get SDpath
    File saveFile=new File(sdCardDir,"zhzhg.txt");
    FileOutputStream outStream = new FileOutputStream(saveFile);
    outStream.write("".getBytes());
    outStream.close();

    URL url=new URL();
    URLConection conn=url.openConnection();
    conn.connect();
    InputStream is=conn.getInputStream();
    Bitmap bmp=BitmapFactory.decodeSteam(is);
    imageview.setImageBitmap(bm);
    */

}

