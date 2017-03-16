package com.lwj.camera;

import android.content.Context;
import android.util.Log;
import android.view.OrientationEventListener;

/**
 * Created by lwj on 2017/3/12.
 * lwjfork@gmail.com
 */

public abstract class DisplayOrientationListener extends OrientationEventListener {

    public static String TAG = "DisplayOrientation";
    public int orientation = 0;

    public DisplayOrientationListener(Context context) {
        super(context);
    }

    @Override
    public void onOrientationChanged(int rotation) {

        if (((rotation >= 0) && (rotation <= 45)) || (rotation > 315)) {
            rotation = 0;
        } else if ((rotation > 45) && (rotation <= 135)) {
            rotation = 90;
        } else if ((rotation > 135) && (rotation <= 225)) {
            rotation = 180;
        } else if ((rotation > 225) && (rotation <= 315)) {
            rotation = 270;
        } else {
            rotation = 0;
        }
        if (rotation == orientation)
            return;
        orientation = rotation;
        onUpdateOrientation();
    }


    public abstract void onUpdateOrientation();

    public void onAttachedToWindow() {
        Log.i(TAG, "enable");
        this.enable();
    }

    protected void onDetachedFromWindow() {
        Log.i(TAG, "disable");
        this.disable();
    }
}
