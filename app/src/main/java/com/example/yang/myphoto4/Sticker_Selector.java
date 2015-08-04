package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * Created by Ree on 2015/7/31.
 */
public class Sticker_Selector extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sticker_selector);
        GridView gridView=(GridView)findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Toast.makeText(Sticker_Selector.this, "" + position, Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(Sticker_Selector.this,DisplayImageActivity.class);

                intent.putExtra("id", position+"");
                setResult(RESULT_OK, intent);

                setContentView(R.layout.null_layout);
                //finishActivity(1);
                finish();


            }
        });

    }
}
