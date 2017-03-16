package com.lwj.camera;

import android.hardware.Camera;

/**
 * Created by lwj on 2017/3/8.
 * lwjfork@gmail.com
 */

public class CameraConstants {

    /**
     * 前置
     */
    public static int FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    /**
     * 后置
     */
    public static int FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;


    public static String FLASH_OFF = Camera.Parameters.FLASH_MODE_OFF;
    public static String FLASH_ON = Camera.Parameters.FLASH_MODE_ON;
    public static String FLASH_TORCH = Camera.Parameters.FLASH_MODE_TORCH;
    public static String FLASH_AUTO = Camera.Parameters.FLASH_MODE_AUTO;
    public static String FLASH_RED_EYE = Camera.Parameters.FLASH_MODE_RED_EYE;

}
