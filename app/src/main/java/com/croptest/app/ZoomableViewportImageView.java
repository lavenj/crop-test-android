package com.croptest.app;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class ZoomableViewportImageView extends ImageView {
	private static final String TAG = ZoomableViewportImageView.class.getSimpleName();
	RectF mViewport = new RectF();
	Matrix matrix = new Matrix();
	Matrix matrixArgs = null;

	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	//    static final int CLICK = 3;
	int mode = NONE;

	PointF last = new PointF();
	PointF start = new PointF();
	float minScale = 1f;
	float maxScale = 4f;
	float[] m;

	float redundantXSpace, redundantYSpace;
	float width, height;
	float saveScale = 1f;
	float right, bottom, origWidth, origHeight, bmWidth, bmHeight;

	ScaleGestureDetector mScaleDetector;
	Context context;

	public ZoomableViewportImageView(Context context, AttributeSet attr) {
		super(context, attr);
//        super.setClickable(true);
		this.context = context;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		matrix.setTranslate(1f, 1f);
		m = new float[9];
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mScaleDetector.onTouchEvent(event);

				matrix.getValues(m);
				float x = m[Matrix.MTRANS_X];
				float y = m[Matrix.MTRANS_Y];
				PointF curr = new PointF(event.getX(), event.getY());

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						last.set(event.getX(), event.getY());
						start.set(last);
						mode = DRAG;
						break;
					case MotionEvent.ACTION_POINTER_DOWN:
						last.set(event.getX(), event.getY());
						start.set(last);
						mode = ZOOM;
						break;
					case MotionEvent.ACTION_MOVE:
						if (mode == ZOOM || (mode == DRAG
//                                && saveScale > minScale
						)) {
							float deltaX = curr.x - last.x;
							float deltaY = curr.y - last.y;
							float scaleWidth = Math.round(origWidth * saveScale);
							float scaleHeight = Math.round(origHeight * saveScale);
//                            if (scaleWidth < mViewport.width()) {
//                                deltaX = 0;
//                                if (y + deltaY > 0)
//                                    deltaY = -y;
//                                else if (y + deltaY < -bottom)
//                                    deltaY = -(y + bottom);
//                            } else if (scaleHeight < mViewport.height()) {
//                                deltaY = 0;
//                                if (x + deltaX > 0)
//                                    deltaX = -x;
//                                else if (x + deltaX < -right)
//                                    deltaX = -(x + right);
//                            } else {
//                                if (x + deltaX > 0)
//                                    deltaX = -x;
//                                else if (x + deltaX < -right)
//                                    deltaX = -(x + right);
//
//                                if (y + deltaY > 0)
//                                    deltaY = -y;
//                                else if (y + deltaY < -bottom)
//                                    deltaY = -(y + bottom);
//                            }
							matrix.postTranslate(deltaX, deltaY);
							snapMatrixToBounds(matrix);
							last.set(curr.x, curr.y);
						}
						break;

					case MotionEvent.ACTION_UP:
						mode = NONE;
						break;

					case MotionEvent.ACTION_POINTER_UP:
						mode = NONE;
						break;
				}
				setImageMatrix(matrix);
				invalidate();
				return true;
			}

		});
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		bmWidth = bm.getWidth();
		bmHeight = bm.getHeight();
	}

	public void setMaxZoom(float x) {
		maxScale = x;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mode = ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float mScaleFactor = detector.getScaleFactor();
			float origScale = saveScale;
			saveScale *= mScaleFactor;
			if (saveScale > maxScale) {
				saveScale = maxScale;
				mScaleFactor = maxScale / origScale;
			} else if (saveScale < minScale) {
				saveScale = minScale;
				mScaleFactor = minScale / origScale;
			}
			right = width * saveScale - width - (2 * redundantXSpace * saveScale);
			bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
			if (origWidth * saveScale <= mViewport.width() || origHeight * saveScale <= mViewport.height()) {
//                Log.d(TAG, "");
				matrix.postScale(mScaleFactor, mScaleFactor, width / 2, height / 2);
//                if (mScaleFactor < 1) {
//                    matrix.getValues(m);
//                    float x = m[Matrix.MTRANS_X];
//                    float y = m[Matrix.MTRANS_Y];
//                    if (mScaleFactor < 1) {
//                        if (Math.round(origWidth * saveScale) < width) {
//                            if (y < -bottom)
//                                matrix.postTranslate(0, -(y + bottom));
//                            else if (y > 0)
//                                matrix.postTranslate(0, -y);
//                        } else {
//                            if (x < -right)
//                                matrix.postTranslate(-(x + right), 0);
//                            else if (x > 0)
//                                matrix.postTranslate(-x, 0);
//                        }
//                    }
//                }
			} else {
				matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
//                matrix.getValues(m);
//                float x = m[Matrix.MTRANS_X];
//                float y = m[Matrix.MTRANS_Y];
//                if (mScaleFactor < 1) {
//                    if (x < -right)
//                        matrix.postTranslate(-(x + right), 0);
//                    else if (x > 0)
//                        matrix.postTranslate(-x, 0);
//                    if (y < -bottom)
//                        matrix.postTranslate(0, -(y + bottom));
//                    else if (y > 0)
//                        matrix.postTranslate(0, -y);
//                }
			}
			return true;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);

		calculateViewport(width, height);

		//center crop
		float scale;
		float scaleX = mViewport.width() / bmWidth;
		float scaleY = mViewport.height() / bmHeight;
		scale = Math.max(scaleX, scaleY);
		matrix.setScale(scale, scale);
		setImageMatrix(matrix);
		saveScale = 1f;


		// Center the image
		redundantYSpace = height - getPaddingTop() - getPaddingBottom() - (scale * bmHeight);
		redundantXSpace = width - getPaddingLeft() - getPaddingRight() - (scale * bmWidth);
		redundantYSpace /= 2;
		redundantXSpace /= 2;

		matrix.postTranslate(redundantXSpace, redundantYSpace);

		origWidth = width - getPaddingLeft() - getPaddingRight() - 2 * redundantXSpace;
		origHeight = height - getPaddingTop() - getPaddingBottom() - 2 * redundantYSpace;
//            right = mViewport.width() * saveScale - width - (2 * redundantXSpace * saveScale);-96
//            bottom = mViewport.height() * saveScale - height - (2 * redundantYSpace * saveScale); -836.1306

		setImageMatrix(matrix);


		if (matrixArgs != null) {
			float[] values = new float[9];
			matrixArgs.getValues(values);
			saveScale = values[Matrix.MSCALE_X];
			matrix.postConcat(matrixArgs);
			setImageMatrix(matrixArgs);
			matrixArgs = null;
		}
	}

	private void calculateViewport(float viewWidth, float viewHeight) {
		//assume portrait
		float height = viewHeight - getPaddingBottom() - getPaddingTop();
		float width = viewWidth - getPaddingLeft() - getPaddingRight();

		if (height > width) {
			//calc height and padding
			float cropHeight = width * 3f / 4f;
			float vertPadding = (height - cropHeight) / 2f;
			mViewport = new RectF(getPaddingLeft(),
				getPaddingTop() + vertPadding,
				getPaddingLeft() + width,
				getPaddingTop() + vertPadding + cropHeight);

		} else {
			//calc width and padding
			float cropWidth = height * 4f / 3f;
			float horizPadding = (width - cropWidth) / 2f;
			mViewport = new RectF(getPaddingLeft() + horizPadding,
				getPaddingTop(),
				getPaddingLeft() + horizPadding + cropWidth,
				getPaddingTop() + height);
		}
	}

	public void snapMatrixToBounds(Matrix m) {
		ImageView mSinglePageView = this;
		RectF snapBox = mViewport;

//		float snapBoxWidth = mViewport.width();
//		float snapBoxHeight = mViewport.height();
		float[] v = new float[9];
		m.getValues(v);
		float originalTranslateX = v[Matrix.MTRANS_X];
		float originalTranslateY = v[Matrix.MTRANS_Y];
		float finalLeftX = v[Matrix.MTRANS_X] + mSinglePageView.getPaddingLeft(); // remove padding from our calculations
		float finalTopY = v[Matrix.MTRANS_Y] + mSinglePageView.getPaddingTop() ;
		float finalScaleFactor = v[Matrix.MSCALE_X];
		float pixelWidth = bmWidth * finalScaleFactor;
		float pixelHeight = bmHeight * finalScaleFactor;
		float finalRightX = finalLeftX + pixelWidth;
		float finalBottomY = finalTopY + pixelHeight;
		float adjustmentX = 0;
		float adjustmentY = 0;

		float slopX = 0;
		float slopY = 0;
		// no need for slop to be bigger than # of extra pixels
//		if (pixelWidth - snapBox.width() < slopX) {
//			slopX = pixelWidth - snapBox.width();
//		}
//		if (pixelHeight - snapBox.height() < slopY) {
//			slopY = pixelHeight - snapBox.height();
//		}

		RectF imageBox = new RectF(finalLeftX, finalTopY, finalRightX, finalBottomY);
		snapBox = new RectF(snapBox.left+slopX, snapBox.top+slopY, snapBox.right-slopX, snapBox.bottom-slopY);

//		Log.v(TAG, "---");
//		Log.v(TAG, "snapBox: " + snapBox);
//		Log.v(TAG, "original ")
//		Log.v(TAG, "slop: " + slopX + ", " + slopY);
//		Log.v(TAG, "imageBox: " + imageBox);

		if( imageBox.contains(snapBox)) {
//			Log.v(TAG, "Entirely contained; No adjustment needed.");
			return;
		}


		//if top and bottom are both in bounds (ie the image is smaller than the box, center us vertically
		if( imageBox.top > snapBox.top && imageBox.bottom < snapBox.bottom ) {
			adjustmentY = snapBox.centerY() - imageBox.centerY();
//			Log.v(TAG, "Centering on Y axis.");
		}
		else if( imageBox.top > snapBox.top ) {
			adjustmentY = snapBox.top - imageBox.top;
		}
		else if( imageBox.bottom < snapBox.bottom ) {
			adjustmentY = snapBox.bottom - imageBox.bottom;
		}

		//if left and right are both in bounds (ie the image is smaller than the box), center us horizontally
		if( imageBox.left > snapBox.left && imageBox.right < snapBox.right ) {
			adjustmentX = snapBox.centerX() - imageBox.centerX();
//			Log.v(TAG, "Centering on X axis.");
		}
		else if( imageBox.left > snapBox.left ) {
			//if we are too far to the right, move us left
			//but keep us snapped to the right edge (don't move by farther than the right overflow)
			float rightOverflow = imageBox.right - snapBox.right;
			adjustmentX = Math.max(snapBox.left - imageBox.left, -rightOverflow);
		}
		else if( imageBox.right < snapBox.right ) {
			float leftOverflow = snapBox.left - imageBox.left;
			adjustmentX = Math.min(snapBox.right - imageBox.right, leftOverflow);
		}
//		Log.v(TAG, "adjustment: " + adjustmentX + ", " + adjustmentY);
		m.setTranslate(originalTranslateX+adjustmentX, originalTranslateY + adjustmentY);
		m.preScale(finalScaleFactor, finalScaleFactor, 0.0f, 0.0f);

	}

	public Rect getCropRect() {
		float[] v = new float[9];
		matrix.getValues(v);
		float originalTranslateX = v[Matrix.MTRANS_X];
		float originalTranslateY = v[Matrix.MTRANS_Y];
		float finalLeftX = v[Matrix.MTRANS_X];
		float finalTopY = v[Matrix.MTRANS_Y];
		float finalScaleFactor = v[Matrix.MSCALE_X];
		float pixelWidth = bmWidth * finalScaleFactor;
		float pixelHeight = bmHeight * finalScaleFactor;
		float finalRightX = finalLeftX + pixelWidth;
		float finalBottomY = finalTopY + pixelHeight;

		RectF onscreenImageBox = new RectF(finalLeftX, finalTopY, finalRightX, finalBottomY);
		RectF onscreenCropBox = mViewport;


		Log.v(TAG, "translate: " + originalTranslateX + ", " + originalTranslateY + "; scale " + finalScaleFactor);

		RectF cropRect = new RectF(onscreenCropBox);
		cropRect.offset(-originalTranslateX, -originalTranslateY);//translate
		float scale = 1/finalScaleFactor;
		cropRect = new RectF(cropRect.left*scale, cropRect.top*scale, cropRect.right*scale, cropRect.bottom*scale);//then scale

		Log.v(TAG, "onscreenCropBox: " + onscreenCropBox);
		Log.v(TAG, "imageBox: " + onscreenImageBox);

		//round inward
		return roundRectIn(cropRect);
	}
	private Rect roundRectIn(RectF cropRect) {
		Rect result = new Rect((int)Math.ceil(cropRect.left), (int)Math.ceil(cropRect.top), (int)Math.floor(cropRect.right), (int)Math.floor(cropRect.bottom));
		return result;
	}
	public void restoreMatrix(Matrix matrix) {
		this.matrixArgs = matrix;
	}
}