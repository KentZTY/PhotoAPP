package com.example.yang.myphoto4;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Ree on 2015/8/24.
 */
public class Help extends Activity {
    ImageView helpImage;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        helpImage=(ImageView)findViewById(R.id.helpImage);
        helpImage.setImageDrawable(getResources().getDrawable(R.drawable.help0));
        helpImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                i++;
                switch (i){
                    case 1:
                        helpImage.setImageDrawable(getResources().getDrawable(R.drawable.help0));
                        break;
                    default:
                        i=0;
                        end();
                        break;

                }
                return false;
            }
        });
    }

    private void end() {
        finish();
    }
}
