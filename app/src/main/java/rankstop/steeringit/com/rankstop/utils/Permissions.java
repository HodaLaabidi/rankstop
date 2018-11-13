package rankstop.steeringit.com.rankstop.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Permissions {

    public static void checkPermission (Activity activity, String permissionString, int permissionCode) {
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) return;
        int existingPermissionStatus = ContextCompat.checkSelfPermission(activity,
                permissionString);
        if (existingPermissionStatus == PackageManager.PERMISSION_GRANTED) return;
        ActivityCompat.requestPermissions(activity, new String[]{permissionString}, permissionCode);
    }

    public static boolean isStorageGranted (Context context) {
        int storagePermissionGranted = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return storagePermissionGranted == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isCameraGranted (Context context) {
        int cameraPermissionGranted = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.CAMERA);
        return cameraPermissionGranted == PackageManager.PERMISSION_GRANTED;
    }

}
