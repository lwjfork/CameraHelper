package com.lwj.camera.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.lwj.camera.DefaultCameraHelper;
import com.lwj.camera.ICameraOperation;

/**
 * Created by lwj on 2017/3/9.
 * lwjfork@gmail.com
 */

public class MyCameraView extends SurfaceView implements ICameraOperation {

    Context mContext;
    ICameraOperation cameraHelper;

    public MyCameraView(Context context) {
        this(context, null);
    }

    public MyCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        cameraHelper = new DefaultCameraHelper(context, this);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onCameraAttached();
    }

    @Override
    protected void onDetachedFromWindow() {
        onCameraDetached();
        super.onDetachedFromWindow();
    }


    @Override
    public int getMaxZoom() {
        return cameraHelper.getMaxZoom();
    }

    @Override
    public boolean isSupportZoom() {
        return cameraHelper.isSupportZoom();
    }

    @Override
    public void setZoom(int zoomValue) {
        cameraHelper.setZoom(zoomValue);
    }

    @Override
    public void start() {
        cameraHelper.start();
    }

    @Override
    public void stop() {
        cameraHelper.stop();
    }

    @Override
    public void setFaceing(int face) {
        cameraHelper.setFaceing(face);
    }

    @Override
    public void setAutoFocus(boolean autoFocus) {
        cameraHelper.setAutoFocus(autoFocus);
    }

    @Override
    public boolean getAutoFocus() {
        return cameraHelper.getAutoFocus();
    }

    @Override
    public int getFaceing() {
        return cameraHelper.getFaceing();
    }

    @Override
    public void setFlash(String flash) {
        cameraHelper.setFlash(flash);
    }

    @Override
    public String getFlash() {
        return cameraHelper.getFlash();
    }

    @Override
    public void setDisplayOrientation(int displayOrientation) {
        cameraHelper.setDisplayOrientation(displayOrientation);
    }

    @Override
    public void takePicture() {
        cameraHelper.takePicture();
    }

    @Override
    public boolean isOpen() {
        return cameraHelper.isOpen();
    }

    @Override
    public void setOnCameraOperationListener(CameraCallback callback) {
        cameraHelper.setOnCameraOperationListener(callback);
    }

    @Override
    public void switchCamera() {
        cameraHelper.switchCamera();
    }


    @Override
    public void onCameraAttached() {
        cameraHelper.onCameraAttached();
    }

    @Override
    public void onCameraDetached() {
        cameraHelper.onCameraDetached();
    }

    @Override
    public boolean isSupportFlash(String flash) {
        return cameraHelper.isSupportFlash(flash);
    }
}
