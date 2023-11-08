package com.a2zsuvidhaa.`in`.util.ents

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*


val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024
val File.sizeInGb get() = sizeInMb / 1024
val File.sizeInTb get() = sizeInGb / 1024




fun File.reduceFileSize(): File? {
    return try { // BitmapFactory options to downsize the image
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        o.inSampleSize = 6
        // factor of downsizing the image
        var inputStream = FileInputStream(this)
        //Bitmap selectedBitmap = null;
        BitmapFactory.decodeStream(inputStream, null, o)
        inputStream.close()
        // The new size we want to scale to
        val REQUIRED_SIZE = 75
        // Find the correct scale value. It should be the power of 2.
        var scale = 1
        while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE) {
            scale *= 2
        }
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        inputStream = FileInputStream(this)
        val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
        inputStream.close()
        // here i override the original image file
        this.createNewFile()
        val outputStream = FileOutputStream(this)
        selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        this
    } catch (e: Exception) {
        this
    }
}
