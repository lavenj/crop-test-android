package com.croptest.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class ImageCropFragment extends Fragment {

    Matrix matrix;

    public ImageCropFragment() {
    }

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_image_crop, container, false);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.karate_karl);
        final ZoomableViewportImageView imageView = (ZoomableViewportImageView)v.findViewById(R.id.imageview);
        imageView.setImageBitmap(bitmap);
        if (matrix != null) {
            imageView.restoreMatrix(matrix);
        }

        v.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matrix = imageView.getImageMatrix();
                ((MainActivity)getActivity()).reloadCroppedImage(matrix);

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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
