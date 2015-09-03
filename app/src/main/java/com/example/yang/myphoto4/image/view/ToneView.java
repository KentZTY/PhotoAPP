package com.example.yang.myphoto4.image.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.yang.myphoto4.R;

/**
 * @author bloodmarray
 */
public class ToneView {
    private static final int TEXT_WIDTH = 50;
    private final int MIDDLE_VALUE = 127;
    /**
     * saturation
     */
    private TextView mSaturation;
    private SeekBar mSaturationBar;
    /**
     * hue
     */
    private TextView mHue;
    private SeekBar mHueBar;
    /**
     * lumination
     */
    private TextView mLum;
    private SeekBar mLumBar;
    private float mDensity;
    private LinearLayout mParent;
    private ColorMatrix mLightnessMatrix;
    private ColorMatrix mSaturationMatrix;
    private ColorMatrix mHueMatrix;
    private ColorMatrix mAllMatrix;
    /**
     * lumination
     */
    private float mLightnessValue = 1F;
    /**
     * saturation
     */
    private float mSaturationValue = 0F;
    /**
     * hue
     */
    private float mHueValue = 0F;
    /**
     * edited image
     */
    private Bitmap mBitmap;

    public ToneView(Context context) {
        init(context);
    }

    private void init(Context context) {
        mDensity = context.getResources().getDisplayMetrics().density;

        mSaturation = new TextView(context);
        mSaturation.setText(R.string.saturation);
        mHue = new TextView(context);
        mHue.setText(R.string.contrast);
        mLum = new TextView(context);
        mLum.setText(R.string.lightness);

        mSaturationBar = new SeekBar(context);
        mSaturationBar.setMax(255);
        mSaturationBar.setProgress(127);
        mSaturationBar.setTag(1);

        mHueBar = new SeekBar(context);
        mHueBar.setMax(255);
        mHueBar.setProgress(127);
        mHueBar.setTag(2);

        mLumBar = new SeekBar(context);
        mLumBar.setMax(255);
        mLumBar.setProgress(127);
        mLumBar.setTag(3);

        LinearLayout saturation = new LinearLayout(context);
        saturation.setOrientation(LinearLayout.HORIZONTAL);
        saturation.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams txtLayoutparams = new LinearLayout.LayoutParams((int) (TEXT_WIDTH * mDensity), LinearLayout.LayoutParams.MATCH_PARENT);
        mSaturation.setGravity(Gravity.CENTER);
        saturation.addView(mSaturation, txtLayoutparams);

        LinearLayout.LayoutParams seekLayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        saturation.addView(mSaturationBar, seekLayoutparams);


        LinearLayout hue = new LinearLayout(context);
        hue.setOrientation(LinearLayout.HORIZONTAL);
        hue.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        mHue.setGravity(Gravity.CENTER);
        hue.addView(mHue, txtLayoutparams);

        hue.addView(mHueBar, seekLayoutparams);


        LinearLayout lum = new LinearLayout(context);
        lum.setOrientation(LinearLayout.HORIZONTAL);
        lum.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        mLum.setGravity(Gravity.CENTER);
        lum.addView(mLum, txtLayoutparams);
        lum.addView(mLumBar, seekLayoutparams);

        mParent = new LinearLayout(context);
        mParent.setOrientation(LinearLayout.VERTICAL);
        mParent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mParent.addView(saturation);
        mParent.addView(hue);
        mParent.addView(lum);
    }

    public View getParentView() {
        return mParent;
    }

    public void setSaturationBarListener(OnSeekBarChangeListener l) {
        mSaturationBar.setOnSeekBarChangeListener(l);
    }

    public void setHueBarListener(OnSeekBarChangeListener l) {
        mHueBar.setOnSeekBarChangeListener(l);
    }

    public void setLumBarListener(OnSeekBarChangeListener l) {
        mLumBar.setOnSeekBarChangeListener(l);
    }

    public void setSaturation(int saturation) {
        mSaturationValue = (float) (saturation * 1.0D / MIDDLE_VALUE);
    }

    public void setHue(int hue) {
        mHueValue = (float) (hue * 1.0D / MIDDLE_VALUE);
    }

    public void setLum(int lum) {
        mLightnessValue = (float) ((lum - MIDDLE_VALUE) * 1.0D / MIDDLE_VALUE * 180);
    }

    /**
     * return to edited image
     *
     * @return
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * @param flag bit0 means whether hue changed，bit1 whether saturation changed,bit2 means whether lumination changed
     */
    public Bitmap handleImage(Bitmap bm, int flag) {
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        // Create a variable of the same size of drawing area, for drawing editted image
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true); // Set the anti-aliasing, doing smoothing edges
        if (null == mAllMatrix) {
            mAllMatrix = new ColorMatrix();
        }

        if (null == mLightnessMatrix) {
            mLightnessMatrix = new ColorMatrix(); // android bitmap color operations are mainly depend on color matrix
        }

        if (null == mSaturationMatrix) {
            mSaturationMatrix = new ColorMatrix();
        }

        if (null == mHueMatrix) {
            mHueMatrix = new ColorMatrix();
        }

        switch (flag) {
            case 0: // need to change the hue
                // f means brightness proportion，if less than 1，means reduce the brightness，or means increase the brightness
                mHueMatrix.reset();
                mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1); // //R,G,B,1
                Log.d("may", "change saturation");
                break;
            case 1: // saturation that needs to change
                // saturation value，minimum value  can be 0，that means black and white image
                // 1 means saturaion isn't be changed，if setted greater than 1，image would be over saturated
                mSaturationMatrix.reset();
                mSaturationMatrix.setSaturation(mSaturationValue);
                Log.d("may", "change saturation");
                break;
            case 2: // lumination
                // hueColor is the rotation angle of the color wheel,During means clockwise, negative means counterclockwise
                mLightnessMatrix.reset(); // set to default
                mLightnessMatrix.setRotate(0, mLightnessValue); // let red area  rotation hueColor angles on the color wheel
                mLightnessMatrix.setRotate(1, mLightnessValue); // let green area  rotation hueColor angles on the color wheel
                mLightnessMatrix.setRotate(2, mLightnessValue); // let blue area  rotation hueColor angles on the color wheel
                // change the hole image hue
                Log.d("may", "change lumination");
                break;
        }
        mAllMatrix.reset();
        mAllMatrix.postConcat(mHueMatrix);
        mAllMatrix.postConcat(mSaturationMatrix); // stacking effect
        mAllMatrix.postConcat(mLightnessMatrix); // stacking effect

        paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));// Set the color transformation effect
        canvas.drawBitmap(bm, 0, 0, paint); // output edited image to the newly created a drawing area
        // return to mew image
        mBitmap = bmp;
        return bmp;
    }

}
