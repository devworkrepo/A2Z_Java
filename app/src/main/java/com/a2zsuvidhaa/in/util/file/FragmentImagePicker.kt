package com.a2zsuvidhaa.`in`.util.file

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.a2zsuvidhaa.`in`.util.*
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import java.io.File
import java.io.IOException


object FragmentImagePicker {


    var filePickPath: String? = null

    var mLocation: Location? = null

    private const val FILE_PICKER_CODE = 1
    private const val IMAGE_PICKER_CODE = 2
    private const val CAMERA_PICKER_CODE = 3


    fun pickFile(fragment: Fragment?) {
        val mimeTypes = arrayOf("image/*", "application/pdf")
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "image/*|application/pdf"
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        fragment?.startActivityForResult(chooseFile, FILE_PICKER_CODE)
    }


    private fun pickImage(fragment: Fragment?) {

        val chooseImage = Intent(Intent.ACTION_PICK)
        chooseImage.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        fragment?.startActivityForResult(chooseImage, IMAGE_PICKER_CODE);
    }

    private fun dispatchTakePictureIntent(fragment: Fragment) {

        val context = fragment.requireContext()

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

            takePictureIntent.resolveActivity(context.packageManager)?.also {
                val photoFile: File? = try {
                    AppFileUtil.createTempImageFileDirectory(context).apply {
                        filePickPath = absolutePath
                    }
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context, context.applicationContext?.packageName.toString() + ".provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    fragment.startActivityForResult(takePictureIntent, CAMERA_PICKER_CODE)
                }
            }
        }
    }

    fun pickMultipleWithLocation(fragment: Fragment?) {
        if (fragment == null) return
        val context = fragment.requireActivity()
        PermissionHandler2.checkCameraStorageAndLocationPermission(fragment) { ->
            fetchLocation(context) {
                Dialogs.imageAndCameraChooser(context,
                    onCamera = {
                        dispatchTakePictureIntent(fragment)
                    }, onGallery = {
                        pickImage(fragment)
                    })
            }
        }
    }


    private fun fetchLocation(context: Context, onFetch: () -> Unit) {
        if (mLocation != null) {
            onFetch()
            return
        }
        LocationService.fetchLocation(context) {
            mLocation = it
        }
    }


    fun pickMultiple(fragment: Fragment?) {
        if (fragment == null) return@pickMultiple
        val context = fragment.requireActivity()
        PermissionHandler2.checkStorageAndCameraPermission(context) { granted ->
            if (!granted) return@checkStorageAndCameraPermission
            Dialogs.imageAndCameraChooser(context,
                onCamera = {
                    dispatchTakePictureIntent(fragment)
                }, onGallery = {
                    pickImage(fragment)
                })
        }
    }


    fun onActivityResult(
        fragment: Fragment,
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ): File? {

        if (resultCode != Activity.RESULT_OK) return null

        val isPickIntent = IMAGE_PICKER_CODE == requestCode || FILE_PICKER_CODE == requestCode

        if (isPickIntent && data == null) return null
        if (isPickIntent && data!!.data == null) return null
        if (!isPickIntent && filePickPath == null) return null

        val context = fragment.requireContext()
        return if (isPickIntent) FileUtils.getFile(context, data!!.data) else File(filePickPath!!)

    }


}

