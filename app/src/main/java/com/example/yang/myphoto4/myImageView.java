package com.example.yang.myphoto4;

/**
 * Created by Yang on 06/08/2015.
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

public class myImageView extends ImageView {
    static final float MAX_SCALE = 2.0f;
    public boolean isEditable = false;
    private Path path = new Path();
    float imageW;
    float imageH;
    int rotatedImageW;
    int rotatedImageH;
    int viewW;
    int viewH;
    int viewL;
    int viewT;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF pA = new PointF();
    PointF pB = new PointF();
    PointF mid = new PointF();
    PointF lastClickPos = new PointF();
    long lastClickTime = 0;
    double rotation = 0.0;
    float dist = 1f;
    private final Context mcontext;
    public Bitmap mBitmap, delmB, ctrlmB; // 原始图
    private Bitmap tmpmBitmap; // 原始图
    private final Paint paint;
    public Point cpoint;// 中心点
    public float angle;// 角度
    public float zoomFactor;// 缩放系数
    public int wW = 0, wH = 0;// 外圈扩大，用于放2个图标
    Point iconP1, iconP2;// 图标p1，p2 中心点坐标。
    Point np1;
    Point np2;
    Point np3;
    Point np4;
    int dx, dy;
    int id;
    private Drawable controlDrawable1;
    private Drawable controlDrawable2;
    Canvas outputDrawable=null;
    /*public void setID(int i){
        id=i;
    }
    public int getID(){
        return id;
    }*/

    public myImageView(Context context, Bitmap mBitmap) {
        super(context);
        mcontext = context;
        paint = new Paint();
        paint.setAntiAlias(true); // 消除锯齿
        paint.setStyle(Style.STROKE); // 绘制空心圆或 空心矩形
        init(mBitmap);
    }

    public myImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        paint = new Paint();
        paint.setAntiAlias(true); // 消除锯齿
        paint.setStyle(Style.STROKE); // 绘制空心圆或 空心矩形
        init(mBitmap);
    }

    public myImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mcontext = context;
        paint = new Paint();
        paint.setAntiAlias(true); // 消除锯齿
        paint.setStyle(Style.STROKE); // 绘制空心圆或 空心矩形
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
        // if(nviewW!=viewW||nviewH!=viewH||nviewL!=viewL||nviewT!=viewT){
        viewW = nviewW;
        viewH = nviewH;
        viewL = nviewL;
        viewT = nviewT;
        // System.out.println("L,T,W,H"+viewL+"|"+viewT+"|"+viewW+"|"+viewH);
        this.layout(viewL, viewT, viewL + viewW, viewT + viewH);// 定位，和大小
        // }
    }

    public void setCPoint(Point c) {
        cpoint = c;
        setViewWH(rotatedImageW, rotatedImageH, cpoint.x - rotatedImageW / 2,
                cpoint.y - rotatedImageH / 2);
        //
        // setViewWH(tmpmBitmap.getWidth(),tmpmBitmap.getHeight(),cpoint.x-tmpmBitmap.getWidth()/2,cpoint.y-tmpmBitmap.getHeight()/2);

    }

    /*
     * 设置图片 中心点，角度，缩放系数
     */
    public void setImageBitmap(Bitmap bm, Point c, float angle, float zoomFactor) {
        mBitmap = bm;
        cpoint = c;
        this.angle = angle;
        this.zoomFactor = zoomFactor;
        //if(isEditable){
        drawRectR(0, 0, (int) (mBitmap.getWidth() * zoomFactor),
                (int) (mBitmap.getHeight() * zoomFactor), angle);//}

        matrix = new Matrix();
        matrix.setScale(zoomFactor, zoomFactor);
        // 设置旋转角度
        matrix.postRotate(angle % 360, mBitmap.getWidth() * zoomFactor / 2,
                mBitmap.getHeight() * zoomFactor / 2);
        matrix.postTranslate(dx + wW, dy + wH);
        // 设置左边距和上边距

        // 绘制旋转图片
        // 创建新的图片
        // if(tmpmBitmap!=null&&!tmpmBitmap.isRecycled()){
        // tmpmBitmap.recycle();
        // }
        // tmpmBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
        // mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);

        setViewWH(rotatedImageW, rotatedImageH, cpoint.x - rotatedImageW / 2,
                cpoint.y - rotatedImageH / 2);
        // this.postInvalidate();
        // System.out.println("oldm:"+mBitmap.getWidth()+"|"+mBitmap.getHeight()+" newm:"+tmpmBitmap.getWidth()+"|"+tmpmBitmap.getHeight());
    }

    /**
     *
     * @param target
     *            围绕旋转的目标点
     * @param source
     *            要旋转的点
     * @param degree
     *            要旋转的角度 360
     * @return
     */
    public static Point roationPoint(Point target, Point source, float degree) {
        source.x = source.x - target.x;
        source.y = source.y - target.y;
        double alpha = 0;
        double beta = 0;
        Point result = new Point();
        double dis = Math.sqrt(source.x * source.x + source.y * source.y);// 2点间，距离
        if (source.x == 0 && source.y == 0) {
            return target;
            // 第一象限
        } else if (source.x >= 0 && source.y >= 0) {
            // 计算与x正方向的夹角
            alpha = Math.asin(source.y / dis);
            // 第二象限
        } else if (source.x < 0 && source.y >= 0) {
            // 计算与x正方向的夹角
            alpha = Math.asin(Math.abs(source.x) / dis);
            alpha = alpha + Math.PI / 2;
            // 第三象限
        } else if (source.x < 0 && source.y < 0) {
            // 计算与x正方向的夹角
            alpha = Math.asin(Math.abs(source.y) / dis);
            alpha = alpha + Math.PI;
        } else if (source.x >= 0 && source.y < 0) {
            // 计算与x正方向的夹角
            alpha = Math.asin(source.x / dis);
            alpha = alpha + Math.PI * 3 / 2;

        }
        // 弧度换算成角度
        alpha = radianToDegree(alpha);
        beta = alpha + degree;
        // 角度转弧度
        beta = degreeToRadian(beta);
        result.x = (int) Math.round(dis * Math.cos(beta));
        result.y = (int) Math.round(dis * Math.sin(beta));
        result.x += target.x;
        result.y += target.y;

        return result;
    }

    /**
     *
     * @return
     */
    public static double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
    }

    public static double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }



    public Bitmap getBitmap(){
        return mBitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        this.paint.setARGB(255, 138, 43, 226);
        this.paint.setStrokeWidth(2);
        // canvas.drawRect(0, 0, mBitmap.getWidth()/2, mBitmap.getHeight()/2,
        // paint);
        canvas.drawBitmap(mBitmap, matrix, paint);
        outputDrawable=canvas;



        // 换包围图的框
        if(isEditable){
            path.reset();
            path.moveTo(np1.x, np1.y);
            path.lineTo(np2.x, np2.y);
            path.lineTo(np3.x, np3.y);
            path.lineTo(np4.x, np4.y);
            path.lineTo(np1.x, np1.y);
            path.lineTo(np2.x, np2.y);
            canvas.drawPath(path, paint);
        /*canvas.drawLine(np1.x, np1.y, np2.x, np2.y, paint);
        canvas.drawLine(np2.x, np2.y, np3.x, np3.y, paint);
        canvas.drawLine(np3.x, np3.y, np4.x, np4.y, paint);
        canvas.drawLine(np4.x, np4.y, np1.x, np1.y, paint);*/
        // 画图
        // canvas.drawBitmap(tmpmBitmap, wW, wH, paint);
        /*canvas.drawBitmap(mBitmap, matrix, paint);*/
        // 画 2个功能图标
        /*canvas.drawBitmap(this.delmB, iconP1.x - wW, iconP1.y - wH, paint);
        canvas.drawBitmap(this.ctrlmB, iconP2.x - wW, iconP2.y - wH, paint);*/
            controlDrawable1.setBounds(iconP1.x - wW, iconP1.y - wH, iconP1.x + wW, iconP1.y + wH);
            controlDrawable2.setBounds(iconP2.x - wW, iconP2.y - wH, iconP2.x + wW, iconP2.y + wH);
            controlDrawable1.draw(canvas);
            controlDrawable2.draw(canvas);
        }
        // 画最外框
        // canvas.drawRect(0, 0,viewW, viewH, paint);
        //
        // 休眠控制最大帧率为每秒3绘制30次
        // //绘制圆环
        // this.paint.setARGB(255, 138, 43, 226);
        // this.paint.setStrokeWidth(ringWidth);
        // canvas.drawCircle(center, center, innerCircle + 1 +ringWidth/2,
        // this.paint);
        //
        // //绘制外圆
        // this.paint.setARGB(255, 138, 43, 226);
        // this.paint.setStrokeWidth(2);
        // canvas.drawCircle(center, center, innerCircle + ringWidth,
        // this.paint);

        setViewWH(rotatedImageW, rotatedImageH, cpoint.x - rotatedImageW / 2,
                cpoint.y - rotatedImageH / 2);
        // setViewWH(tmpmBitmap.getWidth(),tmpmBitmap.getHeight(),cpoint.x-tmpmBitmap.getWidth()/2,cpoint.y-tmpmBitmap.getHeight()/2);

    }


    // 画包围边框线
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
        // +中心点位置计算。
        Point npc = intersects(np4, np2, np1, np3);
        // System.out.println("中心点坐标："+npc.x+"|"+npc.y);
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

    // 2直线交点
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
     * 是否点中2个图标， 1点中 del 2点中 移动 旋转 0 没有点中
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

    /*public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            // 主点按下
            case MotionEvent.ACTION_DOWN:
                pA.set(event.getX() + viewL, event.getY() + viewT);
                // pB.set(event.getX(), event.getY());
                if (isactiondownicon((int) event.getX(), (int) event.getY()) == 2) {
                    mode = ZOOM_OR_ROTATE;
                }
                 else {
                    mode = DRAG;

                }

                break;
            // 副点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                // if (event.getActionIndex() > 1)
                // break;
                // dist = spacing(event.getX(0), event.getY(0), event.getX(1),
                // event.getY(1));
                // // 如果连续两点距离大于10，则判定为多点模式
                // if (dist > 10f) {
                // savedMatrix.set(matrix);
                // pA.set(event.getX(0), event.getY(0));
                // pB.set(event.getX(1), event.getY(1));
                // mid.set((event.getX(0) + event.getX(1)) / 2,
                // (event.getY(0) + event.getY(1)) / 2);
                // mode = ZOOM_OR_ROTATE;
                // }
                // break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:

                if (mode == ZOOM_OR_ROTATE) {// 进行 放大缩小
                    float sf = 1f;
                    // float newx=event.getX();
                    // float newy=event.getY();
                    pB.set(event.getX() + viewL, event.getY() + viewT);
                    float realL = (float) Math.sqrt((float) (mBitmap.getWidth()
                            * mBitmap.getWidth() + mBitmap.getHeight()
                            * mBitmap.getHeight()) / 4);
                    float newL = (float) Math.sqrt((pB.x - (float) cpoint.x)
                            * (pB.x - (float) cpoint.x) + (pB.y - (float) cpoint.y)
                            * (pB.y - (float) cpoint.y));
                    // System.out.println("r,n:"+realL+"|"+newL);
                    sf = newL / realL;
                    // 角度
                    double a = spacing(pA.x, pA.y, (float) cpoint.x,
                            (float) cpoint.y);
                    double b = spacing(pB.x, pB.y, pA.x, pA.y);
                    double c = spacing(pB.x, pB.y, (float) cpoint.x,
                            (float) cpoint.y);
                    double cosB = (a * a + c * c - b * b) / (2 * a * c);
                    if (cosB > 1) {// 浮点运算的时候 cosB 有可能大于1.
                        System.out.println(" sf:" + sf + " cosB:" + cosB);
                        cosB = 1f;
                    }
                    double angleB = Math.acos(cosB);
                    float newAngle = (float) (angleB / Math.PI * 180);
                    // newAngle=0f;

                    float p1x = pA.x - (float) cpoint.x;
                    float p2x = pB.x - (float) cpoint.x;

                    float p1y = pA.y - (float) cpoint.y;
                    float p2y = pB.y - (float) cpoint.y;
                    // 正反向。
                    if (p1x == 0) {
                        if (p2x > 0 && p1y >= 0 && p2y >= 0) {// 由 第4-》第3
                            newAngle = -newAngle;
                        } else if (p2x < 0 && p1y < 0 && p2y < 0) {// 由 第2-》第1
                            newAngle = -newAngle;
                        }
                    } else if (p2x == 0) {
                        if (p1x < 0 && p1y >= 0 && p2y >= 0) {// 由 第4-》第3
                            newAngle = -newAngle;
                        } else if (p1x > 0 && p1y < 0 && p2y < 0) {// 由 第2-》第1
                            newAngle = -newAngle;
                        }
                    } else if (p1x != 0 && p2x != 0 && p1y / p1x < p2y / p2x) {
                        if (p1x < 0 && p2x > 0 && p1y >= 0 && p2y >= 0) {// 由 第4-》第3
                            newAngle = -newAngle;
                        } else if (p2x < 0 && p1x > 0 && p1y < 0 && p2y < 0) {// 由
                            // 第2-》第1
                            newAngle = -newAngle;
                        } else {

                        }
                    } else {
                        if (p2x < 0 && p1x > 0 && p1y >= 0 && p2y >= 0) {// 由 第3-》第4

                        } else if (p2x > 0 && p1x < 0 && p1y < 0 && p2y < 0) {// 由
                            // 第1-》第2

                        } else {
                            newAngle = -newAngle;
                        }
                    }
                    pA.x = pB.x;
                    pA.y = pB.y;
                    if (sf == 0) {
                        sf = 0.1f;
                    } else if (sf >= 3) {
                        sf = 3f;
                    }
                    this.setImageBitmap(this.mBitmap, cpoint, angle + newAngle, sf);

                }

                if (mode == DRAG) {

                    pB.set(event.getX() + viewL, event.getY() + viewT);
                    // 修改中心点
                    cpoint.x += pB.x - pA.x;
                    cpoint.y += pB.y - pA.y;
                    pA.x = pB.x;
                    pA.y = pB.y;
                    // this.setImageBitmap(this.mBitmap, cpoint, jd, sfxs);
                    setCPoint(cpoint);

                }
                break;
        }
        return true;
    }*/

    /**
     * 两点的距离
     */
    public float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return FloatMath.sqrt(x * x + y * y);
    }

    /* 根据手机的分辨率从 dp 的单位 转成为 px(像素) */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public boolean isEditable() {
        return isEditable;
    }

    /**
     * 设置是否处于可缩放，平移，旋转状态
     * @param isEditable
     */
    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        invalidate();
    }


        // private void doubleClick(float x, float y) {
    // float p[] = new float[9];
    // matrix.getValues(p);
    // float curScale = Math.abs(p[0]) + Math.abs(p[1]);
    //
    // float minScale = Math.min((float) viewW / (float) rotatedImageW,
    // (float) viewH / (float) rotatedImageH);
    // if (curScale <= minScale + 0.01) { // 放大
    // float toScale = Math.max(minScale, MAX_SCALE) / curScale;
    // matrix.postScale(toScale, toScale, x, y);
    // } else { // 缩小
    // float toScale = minScale / curScale;
    // matrix.postScale(toScale, toScale, x, y);
    //
    // }
    // // setImageMatrix(matrix);
    // }
}
