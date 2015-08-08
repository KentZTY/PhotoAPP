package com.example.yang.myphoto4;

/**
 * Created by Yang on 06/08/2015.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.widget.ImageView;

public class myImageView extends ImageView {
    public boolean isEditable = false;
    private Path path = new Path();
    int rotatedImageW;
    int rotatedImageH;
    int viewW;
    int viewH;
    int viewL;
    int viewT;
    Matrix matrix = new Matrix();
    PointF pA = new PointF();
    PointF pB = new PointF();
    private final Context mcontext;
    public Bitmap mBitmap, delmB, ctrlmB;
    private final Paint paint;
    public Point cpoint;
    public float angle;
    public float zoomFactor;
    public int wW = 0, wH = 0;
    Point iconP1, iconP2;
    Point np1;
    Point np2;
    Point np3;
    Point np4;
    int dx, dy;
    private Drawable controlDrawable1;
    private Drawable controlDrawable2;
    Canvas outputDrawable=null;

    public myImageView(Context context, Bitmap mBitmap) {
        super(context);
        mcontext = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        init(mBitmap);
    }

    public myImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        init(mBitmap);
    }

    public myImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mcontext = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        init(mBitmap);
    }

    public void init(Bitmap myBitmap) {
        mBitmap = myBitmap;
        delmB = BitmapFactory.decodeResource(getResources(),
                R.drawable.edit_del);
        ctrlmB = BitmapFactory.decodeResource(getResources(),
                R.drawable.edit_control);
        controlDrawable1 = getContext().getResources().getDrawable(R.drawable.edit_del);
        controlDrawable2 = getContext().getResources().getDrawable(R.drawable.edit_control);
        wW = delmB.getWidth() / 2;
        wH = delmB.getHeight() / 2;
        setImageBitmap(mBitmap, new Point(500, 500), 0, 0.5f);
    }

    public void setViewWH(int w, int h, int l, int t) {

        int nviewW = w + wW * 2;
        int nviewH = h + wH * 2;
        int nviewL = l - wW;
        int nviewT = t - wH;
        viewW = nviewW;
        viewH = nviewH;
        viewL = nviewL;
        viewT = nviewT;
        this.layout(viewL, viewT, viewL + viewW, viewT + viewH);
    }

    public void setCPoint(Point c) {
        cpoint = c;
        setViewWH(rotatedImageW, rotatedImageH, cpoint.x - rotatedImageW / 2,
                cpoint.y - rotatedImageH / 2);
    }

    /*
     * Set bitmap, centre point, rotate angle and zoom factor.
     */
    public void setImageBitmap(Bitmap bm, Point c, float angle, float zoomFactor) {
        mBitmap = bm;
        cpoint = c;
        this.angle = angle;
        this.zoomFactor = zoomFactor;
        drawRectR(0, 0, (int) (mBitmap.getWidth() * zoomFactor),
                (int) (mBitmap.getHeight() * zoomFactor), angle);
        matrix = new Matrix();
        matrix.setScale(zoomFactor, zoomFactor);
        matrix.postRotate(angle % 360, mBitmap.getWidth() * zoomFactor / 2,
                mBitmap.getHeight() * zoomFactor / 2);
        matrix.postTranslate(dx + wW, dy + wH);
        setViewWH(rotatedImageW, rotatedImageH, cpoint.x - rotatedImageW / 2,
                cpoint.y - rotatedImageH / 2);
    }


    public static Point roationPoint(Point target, Point source, float degree) {
        source.x = source.x - target.x;
        source.y = source.y - target.y;
        double alpha = 0;
        double beta = 0;
        Point result = new Point();
        double dis = Math.sqrt(source.x * source.x + source.y * source.y);
        if (source.x == 0 && source.y == 0) {
            return target;
        } else if (source.x >= 0 && source.y >= 0) {
            alpha = Math.asin(source.y / dis);
        } else if (source.x < 0 && source.y >= 0) {
            alpha = Math.asin(Math.abs(source.x) / dis);
            alpha = alpha + Math.PI / 2;
        } else if (source.x < 0 && source.y < 0) {
            alpha = Math.asin(Math.abs(source.y) / dis);
            alpha = alpha + Math.PI;
        } else if (source.x >= 0 && source.y < 0) {
            alpha = Math.asin(source.x / dis);
            alpha = alpha + Math.PI * 3 / 2;
        }

        alpha = radianToDegree(alpha);
        beta = alpha + degree;
        beta = degreeToRadian(beta);
        result.x = (int) Math.round(dis * Math.cos(beta));
        result.y = (int) Math.round(dis * Math.sin(beta));
        result.x += target.x;
        result.y += target.y;
        return result;
    }


    public static double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
    }

    public static double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }



    public Bitmap getBitmap(){
        return mBitmap;
    }

    public Matrix getMyMatrix(){
        return matrix;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        this.paint.setARGB(255, 138, 43, 226);
        this.paint.setStrokeWidth(2);
        canvas.drawBitmap(mBitmap, matrix, paint);
        outputDrawable=canvas;



        if(isEditable){
            path.reset();
            path.moveTo(np1.x, np1.y);
            path.lineTo(np2.x, np2.y);
            path.lineTo(np3.x, np3.y);
            path.lineTo(np4.x, np4.y);
            path.lineTo(np1.x, np1.y);
            path.lineTo(np2.x, np2.y);
            canvas.drawPath(path, paint);
            controlDrawable1.setBounds(iconP1.x - wW, iconP1.y - wH, iconP1.x + wW, iconP1.y + wH);
            controlDrawable2.setBounds(iconP2.x - wW, iconP2.y - wH, iconP2.x + wW, iconP2.y + wH);
            controlDrawable1.draw(canvas);
            controlDrawable2.draw(canvas);
        }
        setViewWH(rotatedImageW, rotatedImageH, cpoint.x - rotatedImageW / 2,
                cpoint.y - rotatedImageH / 2);
    }

    public void drawRectR(int l, int t, int r, int b, float jd) {

        Point p1 = new Point(l, t);
        Point p2 = new Point(r, t);
        Point p3 = new Point(r, b);
        Point p4 = new Point(l, b);
        Point tp = new Point((l + r) / 2, (t + b) / 2);
        np1 = roationPoint(tp, p1, jd);
        np2 = roationPoint(tp, p2, jd);
        np3 = roationPoint(tp, p3, jd);
        np4 = roationPoint(tp, p4, jd);
        int w = 0;
        int h = 0;
        int maxn = 0;
        int mixn = 0;
        maxn = np1.x;
        mixn = np1.x;
        if (np2.x > maxn) {
            maxn = np2.x;
        }
        if (np3.x > maxn) {
            maxn = np3.x;
        }
        if (np4.x > maxn) {
            maxn = np4.x;
        }

        if (np2.x < mixn) {
            mixn = np2.x;
        }
        if (np3.x < mixn) {
            mixn = np3.x;
        }
        if (np4.x < mixn) {
            mixn = np4.x;
        }
        w = maxn - mixn;

        maxn = np1.y;
        mixn = np1.y;
        if (np2.y > maxn) {
            maxn = np2.y;
        }
        if (np3.y > maxn) {
            maxn = np3.y;
        }
        if (np4.y > maxn) {
            maxn = np4.y;
        }

        if (np2.y < mixn) {
            mixn = np2.y;
        }
        if (np3.y < mixn) {
            mixn = np3.y;
        }
        if (np4.y < mixn) {
            mixn = np4.y;
        }

        h = maxn - mixn;
        Point npc = intersects(np4, np2, np1, np3);
        dx = w / 2 - npc.x;
        dy = h / 2 - npc.y;
        np1.x = np1.x + dx + wW;
        np2.x = np2.x + dx + wW;
        np3.x = np3.x + dx + wW;
        np4.x = np4.x + dx + wW;

        np1.y = np1.y + dy + wH;
        np2.y = np2.y + dy + wH;
        np3.y = np3.y + dy + wH;
        np4.y = np4.y + dy + wH;
        rotatedImageW = w;
        rotatedImageH = h;
        iconP1 = np1;
        iconP2 = np3;
    }

    public Point intersects(Point sp3, Point sp4, Point sp1, Point sp2) {
        Point localPoint = new Point(0, 0);
        double num = (sp4.y - sp3.y) * (sp3.x - sp1.x) - (sp4.x - sp3.x)
                * (sp3.y - sp1.y);
        double denom = (sp4.y - sp3.y) * (sp2.x - sp1.x) - (sp4.x - sp3.x)
                * (sp2.y - sp1.y);
        localPoint.x = (int) (sp1.x + (sp2.x - sp1.x) * num / denom);
        localPoint.y = (int) (sp1.y + (sp2.y - sp1.y) * num / denom);
        return localPoint;
    }

    /*
     * None = 0, Delete = 1, Zoom and Rotate = 2.
     */
    public int isactiondownicon(int x, int y) {
        int xx = x;
        int yy = y;
        int kk1 = ((xx - iconP1.x) * (xx - iconP1.x) + (yy - iconP1.y)
                * (yy - iconP1.y));
        int kk2 = ((xx - iconP2.x) * (xx - iconP2.x) + (yy - iconP2.y)
                * (yy - iconP2.y));
        if (kk1 < wW * wW) {
            return 1;
        } else if (kk2 < wW * wW) {
            return 2;
        }
        return 0;
    }

    /**
     * Calculate triangle's hypotenuse length.
     */
    public float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * Remove/Add sticker border.
     */
    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        invalidate();
    }
}
