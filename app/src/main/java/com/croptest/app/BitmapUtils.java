package com.croptest.app;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 *
 * @author victorsima
 */
public class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();

    public static Bitmap downSampleBitmap(Context context, Uri imageUri) throws FileNotFoundException {
        InputStream is = null;
        is = context.getContentResolver().openInputStream(imageUri);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);

        int inSampleSize = 1;
        if( options.outWidth > 1224*8 || options.outHeight > 1632*8 ) {
            inSampleSize = 16;
        }
        else if( options.outWidth > 1224*4 || options.outHeight > 1632*4 ) {
            inSampleSize = 8;
        }
        else if( options.outWidth > 1224*2 || options.outHeight > 1632*2 ) {
            inSampleSize = 4;
        }
        else if( options.outWidth > 1224 || options.outHeight > 1632 ) {
            inSampleSize = 2;
        }

//        //Log.d(TAG, "uri: " + imageUri + ", results: " + "size: " + options.outWidth + " x " + options.outHeight);
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        //reopen the stream.
        is = context.getContentResolver().openInputStream(imageUri);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (OutOfMemoryError oome) {
            System.gc();
            return null;
        }

        int orientation = getOrientation(context, imageUri);
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }


    private static int getOrientation(Context context, Uri imageUri) {
        if( imageUri.getScheme().equals(ContentResolver.SCHEME_FILE) ) {
            try {
                File f = new File(new URI(imageUri.toString()));
                ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                if( orientation == ExifInterface.ORIENTATION_NORMAL ) {
                    return 0;
                }
                else if( orientation == ExifInterface.ORIENTATION_ROTATE_90 ) {
                    return 90;
                }
                else if( orientation == ExifInterface.ORIENTATION_ROTATE_180 ) {
                    return 180;
                }
                else if( orientation == ExifInterface.ORIENTATION_ROTATE_270 ) {
                    return 270;
                }
                return 0;
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
                return 0;
            }
            catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }


    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(imageUri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor == null ) {
            return 0;
        }
        if( cursor.getCount() != 1) {
            cursor.close();
            return 0;
        }

        cursor.moveToFirst();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }

}
