package com.croptest.app;

/**
 * Created by victorsima on 1/25/14.
 */
public class ExternalStorageNotWritableException extends Exception {
    public ExternalStorageNotWritableException() {
        super("External Storage is not writable.");
    }

}
