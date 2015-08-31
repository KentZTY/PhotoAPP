package com.example.yang.myphoto4;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;

import com.example.yang.myphoto4.util.ImageDownloader;
import com.example.yang.myphoto4.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filesync);
        syncButton = (ImageButton) findViewById(R.id.sync);
        syncButton.setOnClickListener(this);
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        gridView = (GridView) findViewById(R.id.contentList);
        myListAdapter adapter = new myListAdapter();
        //listView.setAdapter(adapter);


    }

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

    public String getDiskCacheDir(Context context) {
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

            //print("Attempting sync...");
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

                Log.d("Get feedback", json.getString(TAG_STICKER));

                //print(success+"");
                if (success == 1) {
                    Log.d("Login Successful!", json.toString());
                    JSONArray stickersJ=json.getJSONArray(TAG_STICKER);

                    stickers=new String[stickersJ.length()];
                    for(int i=0;i<stickersJ.length();i++){
                        stickers[i]=""+ stickersJ.get(i);
                        //Log.d("Login Successful!", stickers[i]);
                    }
                    List<String> URLs= Arrays.asList(stickers);


                    try{ new ImageDownloader( getDiskCacheDir(getBaseContext()), URLs, new ImageDownloader.DownloadStateListener() {

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onFailed() {
                        }
                    }).startDownload();
                    }catch(Exception e){

                    }
                    SharedPreferences sharedPreferences = getSharedPreferences("sticker", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("stickers", json.getString(TAG_STICKER));
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
            if (file_url != null) {
                //Toast.makeText(this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }

    class myListAdapter extends ListActivity {
        // private List<String> data = new ArrayList<String>();
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            SimpleAdapter adapter = new SimpleAdapter(this,getData(stickers),R.layout.vlist,
                    new String[]{"title","info","img"},
                    new int[]{R.id.title,R.id.info,R.id.img});
            setListAdapter(adapter);
        }

        private List<Map<String, Object>> getData(String[] stickerNames) {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            for(String name:stickerNames){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", name);
                map.put("info", "");
                map.put("img", R.drawable.a1);
                list.add(map);
            }



/*
            map = new HashMap<String, Object>();
            map.put("title", "G2");
            map.put("info", "google 2");
            map.put("img", R.drawable.i2);
            list.add(map);

            map = new HashMap<String, Object>();
            map.put("title", "G3");
            map.put("info", "google 3");
            map.put("img", R.drawable.i3);
            list.add(map);
*/
            return list;
        }
    }

}

