package com.a2zsuvidhaa.in;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.a2zsuvidhaa.in.listener.PermissionGrantedListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class PermissionHandler {

    public static void checkAllPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,

                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();
    }

    public static void checkPhoneReadStatePermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                boolean isAllGranted = report.areAllPermissionsGranted();
                if (report.isAnyPermissionPermanentlyDenied()) {
                    showSettingsDialog(activity, "Read Phone State ");
                }
                listener.isGranted(isAllGranted);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    public static void checkPhoneStateAndLocationPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                boolean isAllGranted = report.areAllPermissionsGranted();
                if (report.isAnyPermissionPermanentlyDenied()) {
                    showSettingsDialog(activity, "Read Phone State, ", "and ", "Location ");
                }
                listener.isGranted(isAllGranted);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Log.d("PermissionTesting", "onPermissionRationaleShouldBeShown");
                token.continuePermissionRequest();
            }
        }).check();
    }

    public static void checkStorageAndCameraPermission(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                boolean isAllGranted = report.areAllPermissionsGranted();
                if (report.isAnyPermissionPermanentlyDenied()) {
                    showSettingsDialog(activity, "Storage, ", "and ", "Camera ");
                }
                listener.isGranted(isAllGranted);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    public static void checkStoragePermission(Activity activity) {
        boolean minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;


        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                boolean isAllGranted = report.areAllPermissionsGranted();
                if (report.isAnyPermissionPermanentlyDenied()) {
                    showSettingsDialog(activity, "Storage");
                }
                if (listener != null)
                    listener.isGranted(isAllGranted);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private static void showSettingsDialog(Activity activity, String... permissions) {

        StringBuilder sb = new StringBuilder();
        for (String s :
                permissions) {
            sb.append(s);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs " + sb.toString() + " permissions to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private static PermissionGrantedListener listener;

    public static void setPermissionGrantedListener(PermissionGrantedListener listener) {
        PermissionHandler.listener = listener;
    }
}
