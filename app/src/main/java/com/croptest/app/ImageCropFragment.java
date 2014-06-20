package com.croptest.app;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageCropFragment extends Fragment {

	private static final String TAG = "ImageCropFragment" ;
	Matrix matrix;
    private Uri imageUri;

    public ImageCropFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey("MATRIX")) {
            matrix = new Matrix();
            matrix.setValues(savedInstanceState.getFloatArray("MATRIX"));
        } else if (getArguments() != null && getArguments().containsKey("MATRIX")) {
            matrix = new Matrix();
            matrix.setValues(getArguments().getFloatArray("MATRIX"));
        }

        try {
            imageUri = getArguments().getParcelable("URI");
        } catch (Exception ex) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_image_crop, container, false);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.karate_karl);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtils.downSampleBitmap(getActivity(), imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final ZoomableViewportImageView imageView = (ZoomableViewportImageView)v.findViewById(R.id.imageview);
        imageView.setImageBitmap(bitmap);
        if (matrix != null) {
            imageView.restoreMatrix(matrix);
        }

        final ImageView resultImageView = (ImageView) v.findViewById(R.id.resultImageView);
        Log.v(TAG, "resultImageView " + resultImageView);

        v.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rect cropBox = imageView.getCropRect();
                Log.v(TAG, "cropBox " + cropBox);

                InputStream is = null;
                try {
                    is = getActivity().getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap croppedBitmap = decodeRegionCrop(is, cropBox);
							Log.v(TAG, "croppedBitmap " + croppedBitmap + "; " + croppedBitmap.getWidth() + ", " + croppedBitmap.getHeight());
							resultImageView.setImageBitmap(croppedBitmap);
							resultImageView.setVisibility(View.VISIBLE);
							final Handler handler = new Handler();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									resultImageView.setImageBitmap(null);
									resultImageView.setVisibility(View.GONE);
								}
							}, 5000);
						}
        });

//        ImageViewTouch imageViewTouch = (ImageViewTouch)v.findViewById(R.id.imageview);
//        imageViewTouch.setImageBitmap(
//                BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_launcher),
//                new Matrix(), 0.5f, 5.0f);
//        new ImageView(getActivity()).setImageMatrix();
        return v;
    }

    private void displayImage() {

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

	private Bitmap decodeRegionCrop(InputStream is, Rect rect) {
		Bitmap croppedImage = null;
//		RotateBitmap mRotateBitmap;
		int mExifRotation = 0;
//		if (mSourceUri != null) {
//			mExifRotation = CropUtil.getExifRotation(CropUtil.getFromMediaUri(getActivity().getContentResolver(), mSourceUri));

//			InputStream is = null;
//			try {
//				is = getActivity().getContentResolver().openInputStream(mSourceUri);
//				mRotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is), mExifRotation);
//			} catch (IOException e) {
//				Log.e(TAG, "Error reading picture: " + e.getMessage(), e);
////				setResultException(e);
//			} catch (OutOfMemoryError e) {
//				Log.e(TAG, "OOM while reading picture: " + e.getMessage(), e);
////				setResultException(e);
//			} finally{
//				CropUtil.closeSilently(is);
//			}
//		}
//		InputStream is = null;
		try {
//			is = getActivity().getContentResolver().openInputStream(mSourceUri);
//			getActivity().getContentResolver().open

			BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
			final int width  = decoder.getWidth();
			final int height = decoder.getHeight();

			if (mExifRotation != 0) {
				// Adjust crop area to account for image rotation
				Matrix matrix = new Matrix();
				matrix.setRotate(-mExifRotation);

				RectF adjusted = new RectF();
				matrix.mapRect(adjusted, new RectF(rect));

				// Adjust to account for origin at 0,0
				adjusted.offset(adjusted.left < 0 ? width : 0, adjusted.top < 0 ? height : 0);
				rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
			}

			try {
				croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());

			} catch (IllegalArgumentException e) {
				// Rethrow with some extra information
				throw new IllegalArgumentException("Rectangle " + rect + " is outside of the image ("
					+ width + "," + height + "," + mExifRotation + ")", e);
			}

		} catch (IOException e) {
			Log.e(TAG, "Error cropping picture: " + e.getMessage(), e);
		} finally {
			CropUtil.closeSilently(is);
		}
		return croppedImage;
	}

}
