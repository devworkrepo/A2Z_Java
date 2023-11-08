package com.a2zsuvidhaa.`in`.util

import android.content.Context
import android.graphics.*
import androidx.annotation.ColorInt
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


object BitmapUtil {


    fun Bitmap.rotatePortrait(): Bitmap {

        if (this.height > this.width) return this

        val matrix = Matrix()

        matrix.postRotate(270.toFloat())

        val scaledBitmap = Bitmap.createScaledBitmap(this, width, height, true)

        return Bitmap.createBitmap(
            scaledBitmap,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height,
            matrix,
            true
        ) ?: this
    }

    private fun Bitmap.scaleImageKeepAspectRatio()  : Bitmap {
        val imageWidth: Int = width
        val imageHeight: Int = height
        val newHeight = imageHeight * 500 / imageWidth
        return  Bitmap.createScaledBitmap(this, 500, newHeight, false)
    }


    fun Bitmap.reduceSize(minQuality: Int = 7,minSize : Int = 512): Bitmap {

       val bitmap  =  this.scaleImageKeepAspectRatio()

        val outputStream = ByteArrayOutputStream()
        val oneMB = 1024
        var quality = 100
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val imageSize = outputStream.size() / oneMB
        if (imageSize < 512) return bitmap



        outputStream.reset()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        quality = 70
        while ((outputStream.size() / 1024 * 1024) > minSize && quality > minQuality) {
            outputStream.reset()
            if (quality < minQuality) quality = minQuality
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 3
        }
        val isBm = ByteArrayInputStream(outputStream.toByteArray())
        return try {
            val file = BitmapFactory.decodeStream(isBm, null, null) ?: bitmap
            outputStream.close()
            file
        } catch (e: Exception) {
            bitmap
        }
    }


    fun Bitmap?.toFile(context: Context?): File? {
        if (context == null) return null;
        if (this == null) return null
        var file: File? = null
        return try {
            file = AppFileUtil.createTempImageFileDirectory(context)

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            this.compress(Bitmap.CompressFormat.PNG, 100, bos) // YOU can also save it in JPEG
            val bitmapData = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }


    fun Bitmap.addWatermark(
        vararg watermarkTexts: String,
        options: WatermarkOptions = WatermarkOptions()
    ): Bitmap {

        val result = this.copy(this.config, true)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        paint.textAlign = when (options.corner) {
            Corner.TOP_LEFT,
            Corner.BOTTOM_LEFT -> Paint.Align.LEFT
            Corner.TOP_RIGHT,
            Corner.BOTTOM_RIGHT -> Paint.Align.RIGHT
        }
        val textSize = result.width * options.textSizeToWidthRatio
        paint.textSize = textSize
        paint.color = options.textColor
        if (options.shadowColor != null) {
            paint.setShadowLayer(textSize / 2, 0f, 0f, options.shadowColor)
        }
        if (options.typeface != null) {
            paint.typeface = options.typeface
        }
        val padding = result.width * options.paddingToWidthRatio
        val coordinates =
            calculateCoordinates(
                watermarkTexts[0],
                paint,
                options,
                canvas.width,
                canvas.height,
                padding
            )

        val textHeight = paint.descent() - paint.ascent()

        var mCount = watermarkTexts.count()
        watermarkTexts.forEach {
            canvas.drawText(it, coordinates.x, coordinates.y - (textHeight * mCount), paint)
            mCount -= 1
        }


        return result
    }

    private fun calculateCoordinates(
        watermarkText: String,
        paint: Paint,
        options: WatermarkOptions,
        width: Int,
        height: Int,
        padding: Float
    ): PointF {
        val x = when (options.corner) {
            Corner.TOP_LEFT,
            Corner.BOTTOM_LEFT -> {
                padding
            }
            Corner.TOP_RIGHT,
            Corner.BOTTOM_RIGHT -> {
                width - padding
            }
        }
        val y = when (options.corner) {
            Corner.BOTTOM_LEFT,
            Corner.BOTTOM_RIGHT -> {
                height - padding
            }
            Corner.TOP_LEFT,
            Corner.TOP_RIGHT -> {
                val bounds = Rect()
                paint.getTextBounds(watermarkText, 0, watermarkText.length, bounds)
                val textHeight = bounds.height()
                textHeight + padding

            }
        }
        return PointF(x, y)
    }

    enum class Corner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
    }

    data class WatermarkOptions(
        val corner: Corner = Corner.BOTTOM_RIGHT,
        val textSizeToWidthRatio: Float = 0.04f,
        val paddingToWidthRatio: Float = 0.03f,
        @ColorInt val textColor: Int = Color.WHITE,
        @ColorInt val shadowColor: Int? = Color.BLACK,
        val typeface: Typeface? = null
    )


}