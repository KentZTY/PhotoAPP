package com.example.yang.myphoto4;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.yang.myphoto4.util.ImageDownloader;
import com.example.yang.myphoto4.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ree on 2015/8/23.
 */
public class FileSync extends Activity implements View.OnClickListener {
    private static final String Sync_URL = "http://raptor.kent.ac.uk/~wz57/Ree/Sync_mobile.php";
    private static final String Image_URL = "http://raptor.kent.ac.uk/~wz57/Ree/drawables/";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STICKER = "sticker";
    public static String PHPSESSID = null;
    private static ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    ImageButton syncButton;
    String username, password;
    GridView gridView;
    String[] stickers;

    ImageView test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filesync);
        syncButton = (ImageButton) findViewById(R.id.sync);
        syncButton.setOnClickListener(this);
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        gridView = (GridView) findViewById(R.id.contentList);
        //listView.setAdapter(adapter);
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //showPics();
                    //test.setImageURI(Uri.fromFile(new File(getDiskCacheDir(getBaseContext()) + "/6.png")));
                    //test.setImageURI(Uri.parse(new File(getDiskCacheDir(getBaseContext()) + "/5.png").toString()));
                    print("Sync Success!");
                    Intent intent = new Intent();
                    intent.setClass(FileSync.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sync:
                //Toast.makeText(getApplicationContext(), "start sync", Toast.LENGTH_SHORT).show();
                new AttemptSync().execute();
                break;

            default:
                break;
        }
    }

    class MyViewBinder implements SimpleAdapter.ViewBinder
    {
        @Override
        public boolean setViewValue(View view, Object data,String textRepresentation)
        {
            if((view instanceof ImageView) & (data instanceof Uri))
            {
                ImageView iv = (ImageView) view;
                Uri uri=(Uri)data;
                iv.setImageURI(uri);
                return true;
            }
            return false;
        }

    }



    public void showPics(){
        print("showPics");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(String name:stickers){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", name);
            map.put("info", "");
            Uri uri=Uri.parse(new File(getDiskCacheDir(getBaseContext()) + "/5.png").toString());
            //Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            map.put("img", uri);

            list.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(FileSync.this,list,R.layout.vlist,
                new String[] { "PIC", "TITLE" }, new int[] { R.id.griditem_pic,
                R.id.griditem_title, });
        adapter.setViewBinder(new MyViewBinder());
        gridView.setAdapter(adapter);
    }

    public void print(String info){
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }

    static public String getDiskCacheDir(Context context) {
        String cachePath = null;
        //Environment.getExtemalStorageState()
        //Environment.MEDIA_MOUNTED
        //getAbsolutePath()

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    class AttemptSync extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(FileSync.this);
            pDialog.setMessage("Attempting sync...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        Sync_URL, "POST", params, PHPSESSID);


                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);

                //Log.d("Get feedback", json.getString(TAG_STICKER));

                //print(success+"");
                if (success == 1) {
                    JSONArray stickersJ=json.getJSONArray(TAG_STICKER);
                    stickers=new String[stickersJ.length()];
                    for(int i=0;i<stickersJ.length();i++){
                        stickers[i]=""+ stickersJ.get(i);
                    }
                    List<String> URLs= Arrays.asList(stickers);
                    try{
                        new ImageDownloader( getDiskCacheDir(getBaseContext()), URLs, new ImageDownloader.DownloadStateListener() {

                            @Override
                            public void onFinish() {
                                failure=false;
                            }

                            @Override
                            public void onFailed() {
                                failure=true;
                            }
                        }).startDownload();
                    }catch(Exception e){Log.d("download","failed");
                    }

                    //save for use
                    SharedPreferences sharedPreferences = getSharedPreferences("sticker", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("stickers", json.toString());
                    editor.commit();
                    Log.d("Save sticker list", json.toString());

                    pDialog.dismiss();
                    return json.getString(TAG_STICKER);
                } else {
                    Log.d("Login Failure!", json.getString(TAG_STICKER));
                    pDialog.dismiss();
                    Intent i = new Intent(FileSync.this, Login.class);
                    startActivity(i);
                    return json.getString(TAG_STICKER);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if(failure==false){
                myHandler.sendEmptyMessage(0);
            }

        }

    }


}

