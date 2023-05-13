package com.tiptop.dotsandboxes.imagepicker;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

/**
 * Created by agile-03 on 1/25/2016.
 */
public class MarshMallowHelper {


    public MarshMallowHelper() {

    }

    public boolean isPermissionGranted(Fragment fragment, int permissionCode, String... permissions) {
        List<String> revokedPermissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(fragment.getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
                    Timber.e("--"+"Permission is granted");
                } else {
                    Timber.e("--"+"Permission is revoked");
                    revokedPermissions.add(permission); //to ask only non-granted permissions
                }
            }
            if (revokedPermissions.isEmpty()) {
                return true;
            } else {
                fragment.requestPermissions(revokedPermissions.toArray(new String[revokedPermissions.size()]), permissionCode);
                return false;
            }
        } else {
            Timber.e("--"+"Permission is granted");
            return true;
        }
    }


    public boolean isPermissionGranted(Activity activity, int permissionCode, String... permissions) {
        List<String> revokedPermissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                    Timber.e("--"+"Permission is granted");
                } else {
                    Timber.e("--"+"Permission is revoked");
                    revokedPermissions.add(permission); //to ask only non-granted permissions
                }
            }
            if (revokedPermissions.isEmpty()) {
                return true;
            } else {
                activity.requestPermissions(revokedPermissions.toArray(new String[revokedPermissions.size()]), permissionCode);
                return false;
            }
        } else {
            Timber.e("--"+"Permission is granted");
            return true;
        }
    }


    public boolean isPermissionGranted(AppCompatActivity appCompatActivity, int permissionCode, String... permissions) {
        List<String> revokedPermissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(appCompatActivity, permission) == PackageManager.PERMISSION_GRANTED) {
                    Timber.e("--"+"Permission is granted");
                } else {
                    Timber.e("--"+"Permission is revoked");
                    revokedPermissions.add(permission); //to ask only non-granted permissions
                }
            }
            if (revokedPermissions.isEmpty()) {
                return true;
            } else {
                appCompatActivity.requestPermissions(revokedPermissions.toArray(new String[revokedPermissions.size()]), permissionCode);
                return false;
            }
        } else {
            Timber.e("--"+"Permission is granted");
            return true;
        }
    }


    public boolean checkGrantResults(@NonNull int[] grantResults) {
        boolean isGranted = true;
        for (int grantResult : grantResults) { //iterate though grant results for asked permissions
            if (grantResult != PackageManager.PERMISSION_GRANTED) { //if any asked permission is not granted
                isGranted = false;
                break;
            }
        }
        return isGranted;
    }

}
