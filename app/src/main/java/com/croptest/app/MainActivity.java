package com.croptest.app;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;



public class MainActivity extends Activity {

    Handler handler = new Handler();
    ImageCropFragment frag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            frag = new ImageCropFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, frag)
                    .commit();
        }
    }

    public void reloadCroppedImage(final Matrix matrix) {

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

}
