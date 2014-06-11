package com.croptest.app;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
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
		float mScreenWidth = 1080;
		float mScreenHeight = 1920;

		float[] v = new float[9];
		m.getValues(v);
		float finalX = v[Matrix.MTRANS_X] + mSinglePageView.getPaddingLeft(); // remove padding from our calculations
		float finalY = v[Matrix.MTRANS_Y] + mSinglePageView.getPaddingTop();
		float finalScaleFactor = v[Matrix.MSCALE_X];
		float pixelWidth = bmWidth * finalScaleFactor;
		float pixelHeight = bmHeight * finalScaleFactor;
		float finalRightX = finalX + pixelWidth;
		float finalBottomY = finalY + pixelHeight;

		// adjusted x coordinate
		float slopX = 125f;
		float slopY = 125f;

		// no need for slop to be bigger than # of extra pixels
		if (pixelWidth - mScreenWidth < slopX) {
			slopX = pixelWidth - mScreenWidth;
		}
		if (pixelHeight - mScreenHeight < slopY) {
			slopY = pixelHeight - mScreenHeight;
		}

		float leftMargin = finalX;
		float topMargin = finalY;
		float rightMargin = mScreenWidth - finalRightX;
		float bottomMargin = mScreenHeight - finalBottomY;

		float newTopMargin = topMargin;
		float newBottomMargin = bottomMargin;
		float newLeftMargin = leftMargin;
		float newRightMargin = rightMargin;

		// if topMargin is too big, snap it to slop
		if (topMargin > slopY) {
			newTopMargin = slopY;
			// adjust our newBottomMargin too
			newBottomMargin = bottomMargin + (topMargin - slopY);
			// if the newBottomMargin is now too far, split the difference
			if (newBottomMargin > slopY) {
				newTopMargin = (topMargin + bottomMargin) / 2;
				newBottomMargin = (topMargin + bottomMargin) / 2;
			}
		}
		else if (bottomMargin > slopY) {
			// if bottomMargin is too big, move things down by how much it is too big by
			newTopMargin = topMargin + (bottomMargin - slopY);
			newBottomMargin = slopY;
			// if that makes topMargin too big, split the difference
			if (newTopMargin > slopY) {
				newTopMargin = (topMargin + bottomMargin) / 2;
				newBottomMargin = (topMargin + bottomMargin) / 2;
			}
		}

		// if leftMargin is too big, snap it to slop
		if (leftMargin > slopX) {
			newLeftMargin = slopX;
			// adjust our newRightMargin too
			newRightMargin = rightMargin + (leftMargin - slopX);
			// if the newRightMargin is now too far, split the difference
			if (newRightMargin > slopX) {
				newLeftMargin = (leftMargin + rightMargin) / 2;
				newRightMargin = (leftMargin + rightMargin) / 2;
			}
		}
		else if (rightMargin > slopX) {
			// if rightMargin is too big, move things down by how much it is too big by
			newLeftMargin = leftMargin + (rightMargin - slopX);
			newRightMargin = slopX;
			// if that makes leftMargin too big, split the difference
			if (newLeftMargin > slopX) {
				newLeftMargin = (leftMargin + rightMargin) / 2;
				newRightMargin = (leftMargin + rightMargin) / 2;
			}
		}

		finalY = newTopMargin;
		finalX = newLeftMargin;

		finalX -= mSinglePageView.getPaddingLeft();
		finalY -= mSinglePageView.getPaddingTop();
		m.setTranslate(finalX, finalY);
		m.preScale(finalScaleFactor, finalScaleFactor, 0.0f, 0.0f);
	}

	public void restoreMatrix(Matrix matrix) {
		this.matrixArgs = matrix;
	}
}