package com.croptest.app;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class GestueImageView extends ImageView {

    private static final int INVALID_POINTER_ID = -1;

    private float mPosX;
    private float mPosY;

    private float mLastTouchX;
    private float mLastTouchY;
    private float mLastGestureX;
    private float mLastGestureY;
    private int mActivePointerId = INVALID_POINTER_ID;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float mXTranslate = 0f;
    private float mYTranslate = 0f;

    public GestueImageView(Context context) {
        super(context);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public GestueImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public GestueImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

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
        return true;
    }








//
//    private static final int INVALID_POINTER_ID = -1;
//
//    private float mPosX;
//    private float mPosY;
//
//    private float mLastTouchX;
//    private float mLastTouchY;
//    private float mLastGestureX;
//    private float mLastGestureY;
//    private int mActivePointerId = INVALID_POINTER_ID;
//
//    private ScaleGestureDetector mScaleDetector;
//    private float mScaleFactor = 1.f;
//    private float mXTranslate = 0f;
//    private float mYTranslate = 0f;
//
//    public GestueImageView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
//    }
//
//    public GestueImageView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        mScaleDetector.onTouchEvent(ev);
//
//        final int action = ev.getAction();
//        switch (action & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN: {
//                if (!mScaleDetector.isInProgress()) {
//                    final float x = ev.getX();
//                    final float y = ev.getY();
//
//                    mLastTouchX = x;
//                    mLastTouchY = y;
//                    mActivePointerId = ev.getPointerId(0);
//                }
//                break;
//            }
//            case MotionEvent.ACTION_POINTER_1_DOWN: {
//                if (mScaleDetector.isInProgress()) {
//                    final float gx = mScaleDetector.getFocusX();
//                    final float gy = mScaleDetector.getFocusY();
//                    mLastGestureX = gx;
//                    mLastGestureY = gy;
//                }
//                break;
//            }
//            case MotionEvent.ACTION_MOVE: {
//
//                // Only move if the ScaleGestureDetector isn't processing a gesture.
//                if (!mScaleDetector.isInProgress()) {
//                    final int pointerIndex = ev.findPointerIndex(mActivePointerId);
//                    final float x = ev.getX(pointerIndex);
//                    final float y = ev.getY(pointerIndex);
//
//                    final float dx = x - mLastTouchX;
//                    final float dy = y - mLastTouchY;
//
//                    mPosX += dx;
//                    mPosY += dy;
//
//                    invalidate();
//
//                    mLastTouchX = x;
//                    mLastTouchY = y;
//                }
//                else{
//                    final float gx = mScaleDetector.getFocusX();
//                    final float gy = mScaleDetector.getFocusY();
//
//                    final float gdx = gx - mLastGestureX;
//                    final float gdy = gy - mLastGestureY;
//
//                    mPosX += gdx;
//                    mPosY += gdy;
//
//                    invalidate();
//
//                    mLastGestureX = gx;
//                    mLastGestureY = gy;
//                }
//
//                break;
//            }
//            case MotionEvent.ACTION_UP: {
//                mActivePointerId = INVALID_POINTER_ID;
//                break;
//            }
//            case MotionEvent.ACTION_CANCEL: {
//                mActivePointerId = INVALID_POINTER_ID;
//                break;
//            }
//            case MotionEvent.ACTION_POINTER_UP: {
//
//                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
//                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//                final int pointerId = ev.getPointerId(pointerIndex);
//                if (pointerId == mActivePointerId) {
//                    // This was our active pointer going up. Choose a new
//                    // active pointer and adjust accordingly.
//                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//                    mLastTouchX = ev.getX(newPointerIndex);
//                    mLastTouchY = ev.getY(newPointerIndex);
//                    mActivePointerId = ev.getPointerId(newPointerIndex);
//                }
//                else{
//                    final int tempPointerIndex = ev.findPointerIndex(mActivePointerId);
//                    mLastTouchX = ev.getX(tempPointerIndex);
//                    mLastTouchY = ev.getY(tempPointerIndex);
//                }
//
//                break;
//            }
//        }
//
//        return true;
//    }

    @Override
    public void onDraw(Canvas canvas) {

        canvas.save();

        canvas.translate(mPosX, mPosY);

        if (mScaleDetector.isInProgress()) {
            canvas.scale(mScaleFactor, mScaleFactor, mScaleDetector.getFocusX(), mScaleDetector.getFocusY());
        } else{
            canvas.scale(mScaleFactor, mScaleFactor, mLastGestureX, mLastGestureY);
        }
        super.onDraw(canvas);
        canvas.restore();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            invalidate();
            return true;
        }
    }
}