package com.a2zsuvidhaa.`in`.util.file

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Base64
import android.view.View
import android.widget.ScrollView
import androidx.core.content.FileProvider
import com.a2zsuvidhaa.`in`.util.Callback
import com.a2zsuvidhaa.`in`.util.ents.showToast
import java.io.*


object StorageHelper {

    fun saveImageToExternalStorage(
            context: Context,
            fileName: String,
            bitmap: Bitmap?): Uri? {

        val uri: Uri?
        val imageOutStream: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val value = ContentValues()
            value.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            value.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            value.put(MediaStore.Images.Media.WIDTH, bitmap?.width)
            value.put(MediaStore.Images.Media.HEIGHT, bitmap?.height)
            value.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES.toString() + "/A2Z")

            uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)
            uri?.let { context.contentResolver.openOutputStream(it) }

        } else {
            val imagesDir: String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/A2Z"

            val dir = File(imagesDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val file = File(imagesDir, fileName)
            uri = FileProvider.getUriForFile(context, context.applicationContext.packageName.toString() + ".provider", file)
            FileOutputStream(file)
        }

        imageOutStream.use { stream ->
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        }

        return uri


    }

    fun saveImageToCacheDirectory(
            context: Context,
            bitmap: Bitmap?,
            fileName: String
    ): Uri {


        val cachePath = File(context.externalCacheDir, "images/")
        cachePath.mkdirs()

        val file = File(cachePath, fileName)
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return FileProvider.getUriForFile(context, context.applicationContext.packageName.toString() + ".provider", file)
    }


    fun savePdfToCacheDirectory(
            context: Context,
            bitmap: Bitmap,
            fileName: String
    ): Uri {


        val cachePath = File(context.externalCacheDir, "document/")
        cachePath.mkdirs()

        val filePath = File(cachePath, fileName)

        val document = PdfDocument()
        val pageInfo = PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        paint.color = Color.parseColor("#ffffff")
        canvas.drawPaint(paint)
        val mBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, true)
        paint.color = Color.BLUE
        canvas.drawBitmap(mBitmap, 0f, 0f, null)
        document.finishPage(page)

        try {
            document.writeTo(FileOutputStream(filePath))
        } catch (e: IOException) {
            e.printStackTrace()
            context.showToast("error occurred")
        }

        document.close()


        return FileProvider.getUriForFile(context, context.applicationContext.packageName.toString() + ".provider", filePath)

    }



    fun shareImage(uri: Uri, context: Context, isWhatsAppOnly: Boolean) {

        val intent = Intent(Intent.ACTION_SEND)
        val whatsAppPackageName = "com.whatsapp"
        if (isWhatsAppOnly) intent.setPackage(whatsAppPackageName)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/jpg"
        context.startActivity(Intent.createChooser(intent, "A2Z Suvidhaa Transaction"))
    }

    fun getBitmapFromView(
            scrollView: ScrollView, 
            onAfterConvert: Callback = {}
    ): Bitmap? {
        val height = scrollView.getChildAt(0).height + 8
        val width = scrollView.getChildAt(0).width + 8

        val view: View = scrollView

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        onAfterConvert()
        return bitmap
    }


    fun fileToString(filePath: String): String? {
        val fis: FileInputStream?
        return try {
            fis = FileInputStream(filePath)
            val bos = ByteArrayOutputStream()
            val b = ByteArray(1024)
            var readNum: Int
            while (fis.read(b).also { readNum = it } != -1) {
                bos.write(b, 0, readNum)
            }
            val fileByteArray = bos.toByteArray()
            Base64.encodeToString(fileByteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }


    fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(context: Context, uri: Uri?): String? {
        val cursor: Cursor = context.contentResolver.query(uri!!, null, null, null, null)!!
        cursor.moveToFirst()
        val idx: Int = cursor.getColumnIndex(Images.ImageColumns.DATA)
        val a = cursor.getString(idx)
        cursor.close()
        return a
    }




}


