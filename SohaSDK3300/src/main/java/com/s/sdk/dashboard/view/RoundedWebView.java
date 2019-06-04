package com.s.sdk.dashboard.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.s.sdk.R;

public class RoundedWebView extends WebView {

    private Context context;
    private int width;

    private int height;

    private int radius;

    public RoundedWebView(Context context) {
        super(context);
        initialize(context);
    }

    public RoundedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public RoundedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        this.context = context;
        setBackgroundColor(Color.TRANSPARENT);
    }

    // This method gets called when the view first loads, and also whenever the
    // view changes. Use this opportunity to save the view's width and height.
    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        width = newWidth;
        height = newHeight;
        radius = (int) context.getResources().getDimension(R.dimen.s_round_webview);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();
        float[] radii = new float[8];
//        if (topLeft) {
//            radii[0] = radius;
//            radii[1] = radius;
//        }
        radii[2] = radius;
        radii[3] = radius;

        radii[4] = radius;
        radii[5] = radius;
//        if (bottomLeft) {
//            radii[6] = radius;
//            radii[7] = radius;
//        }
        Path pathBorder = new Path();
        pathBorder.setFillType(Path.FillType.WINDING);
        pathBorder.addRoundRect(new RectF(0, getScrollY(), width, getScrollY() + height), radii, Path.Direction.CW);
        canvas.drawPath(pathBorder, createPaintBorder());

        path.setFillType(Path.FillType.INVERSE_WINDING);
        path.addRoundRect(new RectF(0, getScrollY(), width, getScrollY() + height), radii, Path.Direction.CW);
        canvas.drawPath(path, createPorterDuffClearPaint());
    }

    private Paint createPorterDuffClearPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        return paint;
    }

    private Paint createPaintBorder(){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.bg_wv));
        paint.setStrokeWidth(context.getResources().getDimensionPixelOffset(R.dimen.s_2dp));
        return paint;
    }

}
