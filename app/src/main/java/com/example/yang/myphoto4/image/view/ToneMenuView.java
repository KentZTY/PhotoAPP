package com.example.yang.myphoto4.image.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.yang.myphoto4.R;


public class ToneMenuView {

    private PopupWindow mPopup;
    private ToneView mToneView;
    private Context mContext;
    private boolean mIsShow;

    public ToneMenuView(Context context) {
        mContext = context;
    }

    public boolean show() {
        if (hide()) {
            return false;
        }

        final Context context = mContext;
        mIsShow = true;

        mPopup = new PopupWindow(context);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();//the amount of color modification

        mToneView = new ToneView(context);
        View view = mToneView.getParentView();
        view.setBackgroundResource(R.drawable.popup);
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hide();
                }
                return false;
            }

        });

        float density = metrics.density;
        mPopup.setWidth(metrics.widthPixels);
        mPopup.setHeight((int) (105 * density));
        mPopup.setContentView(view);
        mPopup.setFocusable(true);
        mPopup.setOutsideTouchable(true);
        mPopup.setTouchable(true);
        // set background to null to avoid black background. Touch return button the PopupWindow would dissappear
        mPopup.setBackgroundDrawable(null);
        mPopup.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        return true;
    }

    public void setSaturationBarListener(OnSeekBarChangeListener l) {
        mToneView.setSaturationBarListener(l);
    }

    public void setHueBarListener(OnSeekBarChangeListener l) {
        mToneView.setHueBarListener(l);
    }

    public void setLumBarListener(OnSeekBarChangeListener l) {
        mToneView.setLumBarListener(l);
    }

    public boolean hide() {
        if (null != mPopup && mPopup.isShowing()) {
            mIsShow = false;
            mPopup.dismiss();
            mPopup = null;
            return true;
        }
        return false;
    }

    public boolean isShow() {
        return mIsShow;
    }

    public ToneView getToneView() {
        return mToneView;
    }
}
