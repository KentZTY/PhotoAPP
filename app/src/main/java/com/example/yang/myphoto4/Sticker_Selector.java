package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Ree on 2015/7/31.
 */
public class Sticker_Selector extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        GridView gridView=(GridView)findViewById(R.id.gridView);
        final ImageAdapter adapter = new ImageAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(Sticker_Selector.this, DisplayImageActivity.class);
                //intent.putExtra("id",position + "");
                String sid=adapter.getItem(position) + "";
                Log.d("id",sid);
                intent.putExtra("id",sid);
                setResult(RESULT_OK, intent);

                setContentView(R.layout.null_layout);
                //finishActivity(1);
                finish();

            }
        });

    }
    public class ImageAdapter extends BaseAdapter {
        int size=300;
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return mThumbIds[position];
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            getImages();
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(size,size));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            getImages();
            //Log.d("Uri passed",mThumbIds[position].toString());
            imageView.setImageURI(mThumbIds[position]);
            return imageView;
        }

        // references to our images
        private Uri[] mThumbIds=new Uri[getC()];


        public void getImages() {

            SharedPreferences sharedPreferencesOut = getSharedPreferences("sticker", Context.MODE_MULTI_PROCESS);
            String temp=sharedPreferencesOut.getString("stickers", "def");
            Log.d("Get prefference", temp);
            if (temp.equals("def")){
                Log.d("temp","null" );
                TypedArray ar = getResources().obtainTypedArray(R.array.sticker);
                int len = ar.length();
                int[] resIds = new int[len];
                //Integer[] temp = new Integer[len++];
                for (int i = 0; i < len; i++){
                    resIds[i] = ar.getResourceId(i, 0);
                    mThumbIds[i]=Uri.parse("android.resource://com.example.yang.myphoto4/"+ar.getResourceId(i,0));}
                ar.recycle();
            }else{
                try{
                    Log.d("temp","not null" );
                    JSONObject jsonObject = new JSONObject(temp);
                    JSONArray ar = jsonObject.getJSONArray("sticker");
                    int len = ar.length();
                    Uri[] resIds = new Uri[len];
                    //Integer[] temp = new Integer[len++];
                    for (int i = 0; i < len; i++){
                        Log.d("id", ar.getString(i));
                        resIds[i] =Uri.parse(new File(getDiskCacheDir(getBaseContext())+"/"+ar.getString(i))+".png");
                        mThumbIds[i]=resIds[i];}
                }catch (Exception e){
                    Log.d("Exception",e.toString());

                }

            }

        }

        public String getDiskCacheDir(Context context) {
            String cachePath = null;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = context.getExternalCacheDir().getPath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
            return cachePath;
        }

        private int getC(){
            TypedArray ar = getResources().obtainTypedArray(R.array.sticker);
            int len = ar.length();
            return len;
        }
    }
}
