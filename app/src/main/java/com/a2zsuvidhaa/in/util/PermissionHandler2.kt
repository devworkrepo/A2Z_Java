package com.a2zsuvidhaa.`in`.util

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.a2zsuvidhaa.`in`.util.ents.showToast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

object PermissionHandler2 {
    fun checkStoragePermission(activity: Activity?, onPermissionGranted: (Boolean) -> Unit) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    val isAllGranted = report.areAllPermissionsGranted()
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog(activity, "Storage")
                    }
                    onPermissionGranted(isAllGranted)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    fun checkCameraStorageAndLocationPermission(fragment: Fragment,onAllPermissionGranted : ()->Unit) {
        val permissionList by lazy {
            hashMapOf(
                Manifest.permission.READ_EXTERNAL_STORAGE to "Storage Read",
                Manifest.permission.WRITE_EXTERNAL_STORAGE to "Storage Write",
                Manifest.permission.CAMERA to "Camera",
                Manifest.permission.ACCESS_FINE_LOCATION to "Access Fine Location",
                Manifest.permission.ACCESS_COARSE_LOCATION to "Access Coarse Location",
            )
        }
        requestDexterMultiplePermission(fragment.requireActivity(), permissionList, onAllPermissionGranted)
    }


    fun checkStorageAndCameraPermission(
        activity: Activity?,
        onPermissionGranted: (Boolean) -> Unit
    ) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    val isAllGranted = report.areAllPermissionsGranted()
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog(activity, "Storage And Camera")
                    }
                    onPermissionGranted(isAllGranted)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }


    fun checkCameraPermission(activity: Activity?, onPermissionGranted: () -> Unit) {
        Dexter.withActivity(activity)
            .withPermission(
                Manifest.permission.CAMERA,
            ).withListener(object : PermissionListener {


                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    onPermissionGranted()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    showSettingsDialog(activity, "Storage And Camera")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }


    fun requestReadPhoneState(activity: Activity?, onPermissionGranted: () -> Unit) {
        if (activity == null) return
        Dexter.withActivity(activity)
            .withPermission(
                Manifest.permission.READ_PHONE_STATE,
            ).withListener(object : PermissionListener {


                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    onPermissionGranted()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    showSettingsDialog(activity, "Read Phone State")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }


    val locationReadPhoneStatePermissions by lazy {
        arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    val locationReadPhoneStateBluetoothAdminPermissions by lazy {
        arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_PHONE_STATE,
        )
    }


    fun showSettingsDialog(activity: Activity?, vararg permissions: String) {
        val sb = StringBuilder()
        for (s in permissions) {
            sb.append(s)
        }

        activity?.let {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Need Permissions")
            builder.setMessage("This app needs $sb permissions to use this feature. You can grant them in app settings.")
            builder.setPositiveButton("GOTO SETTINGS") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivityForResult(intent, 101)
            }
            builder.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.cancel() }
            builder.show()
        }
    }


}


fun Activity?.requestPhoneLocationBluetoothPermissions(onAllPermissionGranted: () -> Unit) {

    if (this == null) return
    val permissionList by lazy {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            hashMapOf(
                Manifest.permission.ACCESS_FINE_LOCATION to "Fine Location",
                Manifest.permission.ACCESS_COARSE_LOCATION to "Coarse Location",
                Manifest.permission.READ_PHONE_STATE to "Read Phone State",
                Manifest.permission.BLUETOOTH_ADMIN to "Bluetooth Admin",
                Manifest.permission.BLUETOOTH_CONNECT to "Bluetooth Connect",
                Manifest.permission.BLUETOOTH_SCAN to "Bluetooth Scan",
            )
        }
        else{
            hashMapOf(
                Manifest.permission.ACCESS_FINE_LOCATION to "Fine Location",
                Manifest.permission.ACCESS_COARSE_LOCATION to "Coarse Location",
                Manifest.permission.READ_PHONE_STATE to "Read Phone State",
                Manifest.permission.BLUETOOTH_ADMIN to "Bluetooth Admin",

            )
        }


    }

    requestDexterMultiplePermission(this, permissionList, onAllPermissionGranted)

}

private fun requestDexterMultiplePermission(
    activity: Activity,
    permissionList: HashMap<String, String>,
    onAllPermissionGranted: () -> Unit
) {
    val deniedList = HashMap<String, String>()

    Dexter.withActivity(activity)
        .withPermissions(permissionList.keys)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {


                val deniedPermissionResponses = report.deniedPermissionResponses
                deniedPermissionResponses.forEach {
                    if (it.isPermanentlyDenied) {
                        val permissionName = permissionList[it.permissionName]
                        val permissionValue = it.permissionName
                        deniedList[permissionValue] = permissionName!!
                    }
                }
                if (report.areAllPermissionsGranted())
                    if (deniedList.isEmpty()) onAllPermissionGranted()
                showSettingsDialog(activity, deniedList)
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>,
                token: PermissionToken
            ) {
                token.continuePermissionRequest()
            }
        }).withErrorListener {
            activity.showToast("Error occurred while permission handling! ")
        }
        .onSameThread()
        .check()
}


private fun showSettingsDialog(activity: Activity?, deniedList: HashMap<String, String>) {

    if (deniedList.isEmpty()) return
    var mCount = 0
    val stringBuilder = StringBuilder()
    deniedList.forEach {
        mCount++
        if (mCount < deniedList.size)
            stringBuilder.append(it.value + ", ")
        else stringBuilder.append(it.value)
    }
    val message =
        if (mCount > 1) "$stringBuilder permissions are required to access further feature.  You can grant them in app settings."
        else "$stringBuilder permission is required to access further feature.  You can grant them in app settings."

    activity?.let {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Need Permissions")
        builder.setMessage(message)
        builder.setPositiveButton("GOTO SETTINGS") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivityForResult(intent, 101)
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.cancel() }
        builder.show()
    }
}


fun Activity.requestForLocationPhoneReadState(onAllPermissionGranted: () -> Unit) {

    val deniedList = ArrayList<String>()

    Dexter.withActivity(this)
        .withPermissions(PermissionHandler2.locationReadPhoneStatePermissions)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) onAllPermissionGranted()

                val deniedPermissionResponses = report.deniedPermissionResponses
                deniedPermissionResponses.forEach {
                    if (it.isPermanentlyDenied)

                        when (it.permissionName) {
                            "android.permission.ACCESS_FINE_LOCATION" -> deniedList.add("Location")
                        }
                }
                if (deniedList.size > 0) {
                    val message: String = if (deniedList.size == 1)
                        deniedList[0] + " permission need this app to use further feature"
                    else deniedList[0] + " and " + deniedList[1] + " permissions need this app to use further feature"
                    PermissionHandler2.showSettingsDialog(
                        this@requestForLocationPhoneReadState,
                        message
                    )
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>,
                token: PermissionToken
            ) {
                token.continuePermissionRequest()
            }
        }).withErrorListener {
            showToast("Error occurred while permission handling! ")
        }
        .onSameThread()
        .check()
}


