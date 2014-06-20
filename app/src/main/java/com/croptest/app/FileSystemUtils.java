package com.croptest.app;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by victorsima on 11/10/13.
 *
 * @author victorsima
 */
public class FileSystemUtils {

	private static final String TAG = FileSystemUtils.class.getSimpleName();

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/** Create a file Uri for saving an image or video */
	public static Uri getPublicOutputMediaFileUri(Context context, int type) throws ExternalStorageNotWritableException{
		return Uri.fromFile(getOutputMediaFile(context, type));
	}

	public static Uri getPrivateOutputFileUri(Context context) {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File file = new File(context.getExternalFilesDir(null), "IMG_tmp_"+ timeStamp + ".jpg");

//		File f = context.getFileStreamPath("IMG_tmp_"+ timeStamp + ".jpg");
//		File f = new File(path);
		return Uri.fromFile(file);
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(Context context, int type) throws ExternalStorageNotWritableException {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

        if (!isExternalStorageWritable()) {
            throw new ExternalStorageNotWritableException();
        }

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
			Environment.DIRECTORY_PICTURES), context.getResources().getString(R.string.app_name));
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()) {
			if (! mediaStorageDir.mkdirs()) {
				//Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
				"IMG_"+ timeStamp + ".jpg");
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
				"VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
