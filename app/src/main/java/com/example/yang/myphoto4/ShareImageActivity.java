package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ShareImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_image);
        (findViewById(R.id.share))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        share();
                    }
                });

        (findViewById(R.id.HOME))
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        home();
                        finish();
                    }
                });
    }

    private void home(){
        MainActivity.instance.finish();
        Intent intent = new Intent();
        intent.setClass(ShareImageActivity.this, MainActivity.class);
        intent.putExtra("close",true);
        startActivity(intent);
    }

    private void share(){
        Uri uri = getIntent().getData();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Please select"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


