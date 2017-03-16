package com.lwj.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by lwj on 2017/3/8.
 * lwjfork@gmail.com
 */

public abstract class CameraHelper implements ICameraOperation {

    public Camera mCamera;

    public Camera.Parameters mCameraParameters;

    public final Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();

    public boolean mAutoFocus;

    public int mFacing = CameraConstants.FACING_BACK;

    public String mFlash;

    public int mDisplayOrientation;

    public int mCameraId;
    public boolean mShowingPreview;


    public SurfaceView mSurfaceView;
    public SurfaceHolder mSurfaceholder;
    public DisplayOrientationListener displayOrientationListener;
    public Context context;

    public CameraHelper(Context context, SurfaceView surfaceView) {
        this.context = context;
        this.mSurfaceView = surfaceView;
        mSurfaceholder = mSurfaceView.getHolder();
        mSurfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceholder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder h) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder h, int format, int width, int height) {
                setHeight(height);
                setWidth(width);
                if (mCamera != null) {
                    setUpPreview();
                    adjustCameraParameters();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder h) {
                setWidth(0);
                setHeight(0);
            }
        });
        displayOrientationListener = new DisplayOrientationListener(context) {
            @Override
            public void onUpdateOrientation() {
                mDisplayOrientation = displayOrientationListener.orientation;
                if (isOpen()) {
                    adjustCameraParameters();
                }
            }
        };
    }

    public int height;
    public int width;

    public boolean isReady() {
        return height > 0 && width > 0;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    @Override
    public int getMaxZoom() {
        if (isSupportZoom()) {
            return mCameraParameters.getMaxZoom();
        }
        return 0;
    }

    @Override
    public boolean isSupportZoom() {

        return mCameraParameters == null && mCameraParameters.isZoomSupported();
    }

    @Override
    public void setZoom(int zoomValue) {
        if (isSupportZoom()) {
            mCameraParameters.setZoom(zoomValue);
        } else {
            Log.w("CameraHelper", "The camera isn't support zoom");
        }
    }


    /**
     * 设置预览
     */
    private void setUpPreview() {
        try {
            if (mShowingPreview) {
                mCamera.stopPreview();
            }
            mCamera.setPreviewDisplay(mSurfaceholder);
            if (mShowingPreview) {
                mCamera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start() {
        chooseCamera();
        openCamera();
        if (isReady()) {
            setUpPreview();
        }
        mShowingPreview = true;
        mCamera.startPreview();
    }

    private void openCamera() {
        if (mCamera != null) {
            releaseCamera();
        }
        mCamera = Camera.open(mCameraId);
        mCameraParameters = mCamera.getParameters();
        adjustCameraParameters();
        mCamera.setDisplayOrientation(90);
        if (callback != null) {
            callback.onOpen();
        }

    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            if (callback != null) {
                callback.onClose();
            }
        }
    }

    public abstract Camera.Size choosePreViewSize();

    public abstract Camera.Size choosePictureSize();

    private void adjustCameraParameters() {
        Camera.Size preSize = choosePreViewSize();
        Camera.Size pictureSize = choosePictureSize();
        if (preSize != null) {
            final Camera.Size currentSize = mCameraParameters.getPictureSize();
            if (currentSize.width != preSize.width || currentSize.height != preSize.height) {
                if (mShowingPreview) {
                    mCamera.stopPreview();
                }
                mCameraParameters.setPreviewSize(preSize.width, preSize.height);
                if (pictureSize != null) {
                    mCameraParameters.setPictureSize(pictureSize.width, pictureSize.height);
                }
                mCameraParameters.setPictureFormat(ImageFormat.JPEG);
                mCameraParameters.setJpegQuality(100);
                mCameraParameters.setJpegThumbnailQuality(100);

                setCameraFocus(mAutoFocus);
                setFlash(mFlash);


            }
        }
        updateCameraOrientation();
        mCameraParameters.setRotation(mRotation);
        mCamera.setParameters(mCameraParameters);
        if (mShowingPreview) {
            mCamera.startPreview();
        }
    }


    @Override
    public void stop() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
        mShowingPreview = false;
        releaseCamera();
    }

    @Override
    public void setFaceing(int facing) {
        if (mFacing == facing) {
            return;
        }
        mFacing = facing;
        if (isOpen()) {
            stop();
            start();
        }
    }

    @Override
    public void setAutoFocus(boolean autoFocus) {
        if (mAutoFocus == autoFocus) {
            return;
        }
        if (setCameraFocus(autoFocus)) {
            mCamera.setParameters(mCameraParameters);
        }

    }

    @Override
    public boolean getAutoFocus() {
        if (!isOpen()) {
            return mAutoFocus;
        }
        String focusMode = mCameraParameters.getFocusMode();
        return focusMode != null && focusMode.contains("continuous");
    }

    @Override
    public int getFaceing() {
        return mFacing;
    }

    @Override
    public void setFlash(String flash) {
        if (mFlash != null && mFlash.equals(flash)) {
            return;
        }
        this.mFlash = flash;
        if (setCameraFlash(flash)) {
            mCamera.setParameters(mCameraParameters);
        }
    }

    @Override
    public String getFlash() {
        return mFlash;
    }

    @Override
    public void setDisplayOrientation(int displayOrientation) {

        if (mDisplayOrientation == displayOrientation) {
            return;
        }
        mDisplayOrientation = displayOrientation;

        if (isOpen()) {
            int cameraRotation = calcCameraRotation(displayOrientation);
            mCameraParameters.setRotation(cameraRotation);
            mCamera.setParameters(mCameraParameters);
            if (mShowingPreview) {
                mCamera.stopPreview();
            }
            mCamera.setDisplayOrientation(90);
            if (mShowingPreview) {
                mCamera.startPreview();
            }
        }
    }

    @Override
    public void takePicture() {
        if (!isOpen()) {
            Log.e("CameraHelper", "Camera is not ready. Call start() before takePicture().");
            return;
        }
        if (getAutoFocus()) {
            mCamera.cancelAutoFocus();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    takePictureInternal();
                }
            });
        } else {
            takePictureInternal();
        }
    }

    private void takePictureInternal() {
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (callback != null) {
                    callback.onPictureTaken(data);
                }
                camera.startPreview();
            }
        });
    }

    private void chooseCamera() {
        for (int i = 0, count = Camera.getNumberOfCameras(); i < count; i++) {
            Camera.getCameraInfo(i, mCameraInfo);
            if (mCameraInfo.facing == mFacing) {
                mCameraId = i;
                return;
            }
        }
        mCameraId = -1;
    }

    /**
     * 计算方向
     *
     * @param rotation
     * @return
     */
    private int calcCameraRotation(int rotation) {
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (360 - (mCameraInfo.orientation + rotation) % 360) % 360;
        } else {  // back-facing
            return (mCameraInfo.orientation - rotation + 360) % 360;
        }
    }


    private boolean setCameraFocus(boolean autoFocus) {
        mAutoFocus = autoFocus;
        if (isOpen()) {
            final List<String> modes = mCameraParameters.getSupportedFocusModes();
            if (modes == null && modes.size() <= 0) {
                return false;
            }
            if (autoFocus && modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                mCameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            } else {
                mCameraParameters.setFocusMode(modes.get(0));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isOpen() {
        return mCamera != null;
    }


    private boolean setCameraFlash(String flash) {
        if (!isOpen()) {
            mFlash = flash;
            return false;
        } else {
            if (flash == null) {
                return false;
            }
            List<String> modes = mCameraParameters.getSupportedFlashModes();

            if (modes != null && modes.contains(flash)) {
                mCameraParameters.setFlashMode(flash);
                mFlash = flash;
                return true;
            }
            if (modes == null || !modes.contains(mFlash)) {
                mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mFlash = CameraConstants.FLASH_OFF;
                return true;
            }
            return false;
        }
    }


    CameraCallback callback;

    @Override
    public void setOnCameraOperationListener(CameraCallback callback) {
        this.callback = callback;
    }

    @Override
    public void switchCamera() {
        if (mFacing == CameraConstants.FACING_BACK) {
            setFaceing(CameraConstants.FACING_FRONT);
        } else if (mFacing == CameraConstants.FACING_FRONT) {
            setFaceing(CameraConstants.FACING_BACK);
        }
    }


    private int mRotation;

    private void updateCameraOrientation() {
        if (mCamera != null) {
            // rotation参数为 0、90、180、270。水平方向为0。
            mRotation = 90 + mDisplayOrientation == 360 ? 0 : 90 + mDisplayOrientation;
            if (mFacing == CameraConstants.FACING_FRONT) {
                if (mRotation == 90) {
                    mRotation = 270;
                } else if (mRotation == 270) {
                    mRotation = 90;
                }
            }
        }
    }

    @Override
    public void onCameraAttached() {
        displayOrientationListener.onAttachedToWindow();
    }

    @Override
    public void onCameraDetached() {
        displayOrientationListener.onDetachedFromWindow();
    }
}
