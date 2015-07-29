package com.example.yang.myphoto4;

/**
 * Created by Yang on 27/07/2015.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class imageBorderView extends ImageView {

    private int co;
    private int borderwidth;

    public imageBorderView(Context context) {
        super(context);
    }
    public imageBorderView(Context context, AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
    }

    public imageBorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setColour(int color){
        co = color;
    }

    public void setBorderWidth(int width){
        borderwidth = width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rec = canvas.getClipBounds();
        rec.bottom--;
        rec.right--;
        Paint paint = new Paint();
        paint.reset();
        paint.setColor(co);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderwidth);
        canvas.drawRect(rec, paint);

    }
}