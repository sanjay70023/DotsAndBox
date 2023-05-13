package com.tiptop.dotsandboxes.imagepicker;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * Created by agile-01 on 10/14/2016.
 */

public class Cropper {
    public static final int CROP_TYPE_OVAL = 100;
    public static final int CROP_TYPE_RECTANGLE = 101;
    @CropperType
    public int cropperType = CROP_TYPE_RECTANGLE;
    public int cropperWidth = 1;
    public int cropperHeight = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CROP_TYPE_OVAL, CROP_TYPE_RECTANGLE})
    public @interface CropperType {
    }
}


