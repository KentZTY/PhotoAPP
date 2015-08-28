package com.example.yang.myphoto4;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.myphoto4.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ree on 2015/8/23.
 */
public class FileSync extends Activity implements View.OnClickListener {
    private static final String Sync_URL = "http://raptor.kent.ac.uk/~wz57/Ree/Sync.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_STICKER = "sticker";
    public static String PHPSESSID = null;
    private static ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    ImageButton syncButton;
    TextView testText;
    String username, password;

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filesync);
        testText = (TextView) findViewById(R.id.testText);
        syncButton = (ImageButton) findViewById(R.id.sync);
        syncButton.setOnClickListener(this);
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
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

            //print("Attempting login...");
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
                    //print(TAG_STICKER);
                    //testText.setText(TAG_STICKER);

                    return json.getString(TAG_STICKER);
                } else {
                    Log.d("Login Failure!", json.getString(TAG_STICKER));
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

        private void print(String info) {
            Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
        }
    }


}

