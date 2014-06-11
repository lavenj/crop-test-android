package com.croptest.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * Created by victorsima on 6/9/14.
 */
public class ScaleImageView extends ImageView {

    private static final float SCALE_MAX = 5f;
    private static final float SCALE_MIN = 0.5f;

    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float mLastTouchX, mLastTouchY;
    private float mPosX, mPosY;
    private Matrix mMatrix = new Matrix();


    public ScaleImageView(Context context) {
        super(context);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        setScaleType(ScaleType.MATRIX);

    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        setScaleType(ScaleType.MATRIX);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        setScaleType(ScaleType.MATRIX);
    }


    public void setImageBitmapAndCenter(Bitmap bitmap) {
        //resize and move to center
        setImageBitmap(bitmap);


    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        if (!mScaleDetector.isInProgress()) {
            final int action = MotionEventCompat.getActionMasked(ev);

            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                    final float x = MotionEventCompat.getX(ev, pointerIndex);
                    final float y = MotionEventCompat.getY(ev, pointerIndex);

                    // Remember where we started (for dragging)
                    mLastTouchX = x;
                    mLastTouchY = y;
                    // Save the ID of this pointer (for dragging)
                    mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    // Find the index of the active pointer and fetch its position
                    final int pointerIndex =
                            MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                    final float x = MotionEventCompat.getX(ev, pointerIndex);
                    final float y = MotionEventCompat.getY(ev, pointerIndex);

                    // Calculate the distance moved
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;
                    mMatrix.postTranslate(dx, dy);
                    setImageMatrix(mMatrix);
                    invalidate();

                    // Remember this touch position for the next move event
                    mLastTouchX = x;
                    mLastTouchY = y;

                    break;
                }

                case MotionEvent.ACTION_UP: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {

                    final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                    final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                        mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                        mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                    }
                    break;
                }
            }
        }
        return true;
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(SCALE_MIN, Math.min(mScaleFactor, SCALE_MAX));

            mMatrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
            setImageMatrix(mMatrix);
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }
}
