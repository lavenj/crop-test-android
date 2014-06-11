package com.croptest.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * View to display crop overlay.
 */
public class CropOverlayView extends View {

    private RectF drawRect = null;
    private Paint backgroundColor = new Paint();
    private Paint borderColor = new Paint();
    private Paint mask = new Paint();


    public CropOverlayView(Context context) {
        super(context);
        init();
    }

    public CropOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        backgroundColor.setColor(0xcc000000);
        backgroundColor.setStyle(Paint.Style.FILL);

        borderColor.setColor(0xff666666);
        borderColor.setStyle(Paint.Style.STROKE);
        borderColor.setStrokeWidth(getResources().getDimensionPixelOffset(R.dimen.crop_border));

        mask.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }


    @Override
    protected void onDraw(Canvas canvas) {

        //assume portrait
        float height = canvas.getHeight() - getPaddingBottom() - getPaddingTop();
        float width = canvas.getWidth() - getPaddingLeft() - getPaddingRight();

        if (height > width) {
            //calc height and padding
            float cropHeight = width * 3f / 4f;
            float vertPadding = (height - cropHeight) / 2f;
            drawRect = new RectF(getPaddingLeft(),
                    getPaddingTop() + vertPadding,
                    getPaddingLeft() + width,
                    getPaddingTop() + vertPadding + cropHeight);

        } else {
            //calc width and padding
            float cropWidth = height * 4f / 3f;
            float horizPadding = (width - cropWidth) / 2f;
            drawRect = new RectF(getPaddingLeft() + horizPadding,
                    getPaddingTop(),
                    getPaddingLeft() + horizPadding + cropWidth,
                    getPaddingTop() + height);
        }

        canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        Bitmap srcBitmap = makeSrc(canvas.getWidth(), canvas.getHeight(), backgroundColor);
        Bitmap dstBitmap = makeDst(canvas.getWidth(), canvas.getHeight(), drawRect);
        canvas.drawBitmap(srcBitmap, 0, 0, null);
        canvas.drawBitmap(dstBitmap, 0, 0, mask);
        canvas.drawRect(drawRect, borderColor);

        canvas.restore();
    }

    private static Bitmap makeSrc(int w, int h, Paint paint) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        c.drawRect(0, 0, w, h, paint);
        return bm;
    }

    private static Bitmap makeDst(int w, int h, RectF maskRect) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFF000000);
        c.drawRect(maskRect, p);
        return bm;
    }
}
