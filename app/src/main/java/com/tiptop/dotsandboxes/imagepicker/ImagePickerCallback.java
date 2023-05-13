package com.tiptop.dotsandboxes.imagepicker;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by agile-01 on 10/14/2016.
 */

public interface ImagePickerCallback {

    void onCompleteTakingImage(@Nullable Uri uri, boolean isCropped);

    void onCancelTakingImage();

    void onErrorTakingImage(@NonNull String messageToShow, @NonNull Throwable t);

}
