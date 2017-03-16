package com.lwj.camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by lwj on 2017/3/9.
 * lwjfork@gmail.com
 */

public class DefaultCameraHelper extends CameraHelper {

    public DefaultCameraHelper(Context context, SurfaceView surfaceView) {
        super(context,surfaceView);
    }


    @Override
    public Camera.Size choosePreViewSize() {
        // 选择合适的预览尺寸
        List<Camera.Size> sizeList = mCameraParameters.getSupportedPreviewSizes();
        if (sizeList.size() > 0) {
            //一般第一个都是最优的
            int height = sizeList.get(0).height;
            return getOptimalPreviewSize(sizeList, height * 4 / 3, height);

        }
        return null;
    }

    @Override
    public Camera.Size choosePictureSize() {
        //设置生成的图片大小
        List<Camera.Size> sizeList = mCameraParameters.getSupportedPictureSizes();
        if (sizeList.size() > 0) {
            Camera.Size cameraSize = sizeList.get(0);
            for (Camera.Size size : sizeList) {
                //小于100W像素
                if (size.width * size.height < 100 * 10000) {
                    cameraSize = size;
                    break;
                }
            }
            return cameraSize;
        }
        return null;
    }


    /**
     * @param sizes
     * @param w
     * @param h
     * @return 取最接近比例的
     */
    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) <
                        minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
