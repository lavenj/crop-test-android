package com.croptest.app;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    protected boolean mImageIsFromCamera;
    protected Uri mCameraOutputFileUri;
    protected Uri mSelectedImageUri;

    Handler handler = new Handler();
    ImageCropFragment frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Crop it!");
        setContentView(R.layout.activity_main);
//        if (savedInstanceState == null) {
            frag = new ImageCropFragment();
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, frag)
//                    .commit();
//        }

        startActivityForResult(createImageCaptureIntent(), 30000);
    }

    public void reloadCroppedImage(final Matrix matrix, final Uri uri) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction()
                        .remove(frag)
                        .commit();
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                float[] values = new float[9];
                matrix.getValues(values);
                frag = new ImageCropFragment();
                Bundle b = new Bundle();
                b.putFloatArray("MATRIX", values);
                b.putParcelable("URI", uri);
                frag.setArguments(b);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, frag)
                        .commit();
            }
        }, 2000);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 30000 && resultCode == RESULT_OK) {
            Uri imageUri = locateImageUri(data);
            reloadCroppedImage(new Matrix(), imageUri);
        }
    }

    /**
     * Finds the uri of the target image from either camera or filesystem based on the returned intent from the chooser
     * @param data
     * @return uri of the image
     */
    public Uri locateImageUri(Intent data) {
        if(data == null) {
            mImageIsFromCamera = true;
        }
        else {
            final String action = data.getAction();
            if(action == null) {
                mImageIsFromCamera = false;
            }
            else {
                mImageIsFromCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }

        if(mImageIsFromCamera) {
            mSelectedImageUri = mCameraOutputFileUri;
        }
        else {
            mSelectedImageUri = (data == null) ? null : data.getData();
        }
        mCameraOutputFileUri = null;
        //Log.v(TAG, "onChooseImageComplete: " + mSelectedImageUri);
        return mSelectedImageUri;
    }


    public Intent createImageCaptureIntent() {

        boolean displayCameraIntent = true;

            mCameraOutputFileUri = FileSystemUtils.getPrivateOutputFileUri(this);


        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        if (displayCameraIntent) {
            final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for(ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraOutputFileUri);
                cameraIntents.add(intent);
            }
        }

        // Filesystem
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        final Intent chooserIntent = Intent.createChooser(galleryIntent, "open_image_chooser_title");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        return chooserIntent;
    }
}
