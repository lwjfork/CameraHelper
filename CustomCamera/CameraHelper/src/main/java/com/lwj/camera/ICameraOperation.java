package com.lwj.camera;

/**
 * Created by lwj on 2017/3/8.
 * lwjfork@gmail.com
 */

public interface ICameraOperation {


    /**
     * 获取最大焦距
     *
     * @return int
     */
    int getMaxZoom();

    /**
     * 是否支持焦距调整
     *
     * @return true 支持  false 不支持
     */
    boolean isSupportZoom();

    /**
     * 调整焦距
     *
     * @param zoomValue 焦距值
     */
    void setZoom(int zoomValue);

    /**
     * 打开相机
     */
    void start();

    /**
     * 关闭相机
     */
    void stop();

    /**
     * 设置前后置相机
     */
    void setFaceing(int face);

    /***
     * 设置是否自动聚焦
     *
     * @param autoFocus
     */
    void setAutoFocus(boolean autoFocus);

    boolean getAutoFocus();


    int getFaceing();

    /**
     * 设置闪光灯
     *
     * @param flash
     */
    void setFlash(String flash);

    String getFlash();

    boolean isSupportFlash(String flash);
    /**
     * 设置方向
     *
     * @param displayOrientation
     */
    void setDisplayOrientation(int displayOrientation);


    void takePicture();

    void switchCamera();


    /**
     * 相机是否打开
     *
     * @return
     */
    boolean isOpen();


    interface CameraCallback {

        void onOpen();

        void onClose();

        void onPictureTaken(byte[] data);

    }


    void onCameraAttached();

    void onCameraDetached();


    void setOnCameraOperationListener(CameraCallback callback);
}
