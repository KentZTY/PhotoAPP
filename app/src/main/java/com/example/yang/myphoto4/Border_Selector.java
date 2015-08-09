package com.example.yang.myphoto4;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Created by Ree on 2015/7/31.
 */
public class Border_Selector extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //BorderAdapter borderAdapter=new BorderAdapter(this);
        setContentView(R.layout.sticker_selector);
        GridView gridView=(GridView)findViewById(R.id.gridView);
        BorderAdapter bAdapter=new BorderAdapter(this);
        bAdapter.setRes(getResources());
        //bAdapter.setImage(getImage());

        //gridView.setAdapter(bAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent=new Intent(Border_Selector.this,DisplayImageActivity.class);
                intent.putExtra("id", position+"");
                setResult(RESULT_OK, intent);
                setContentView(R.layout.null_layout);
                //finishActivity(1);
                finish();


            }
        });

    }
    //print debug info
    public void print(String info){
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }

    private int[] getImage() {
        TypedArray ar = getResources().obtainTypedArray(R.array.border);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++)
            resIds[i] = ar.getResourceId(i, 0);
        ar.recycle();
        for(int i:resIds){
            print(""+i);
        }
        return resIds;
    }
}
