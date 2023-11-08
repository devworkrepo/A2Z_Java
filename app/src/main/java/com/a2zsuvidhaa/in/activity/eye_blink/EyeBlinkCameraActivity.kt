package com.a2zsuvidhaa.`in`.activity.eye_blink

import android.Manifest.permission
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.a2zsuvidhaa.`in`.R
import com.a2zsuvidhaa.`in`.util.AppUtil
import com.a2zsuvidhaa.`in`.util.BitmapUtil.toFile
import com.a2zsuvidhaa.`in`.util.dialogs.Dialogs
import com.a2zsuvidhaa.`in`.util.file.AppFileUtil
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor
import java.io.IOException

class EyeBlinkCameraActivity : AppCompatActivity(), SurfaceHolder.Callback,
    CameraSource.PictureCallback {
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var surfaceView: SurfaceView
    private lateinit var detector: FaceDetector
    private  var cameraSource: CameraSource ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eye_blink_camera)

        surfaceView = findViewById(R.id.surfaceView)

        detector = FaceDetector.Builder(this)
            .setProminentFaceOnly(true) // optimize for single, relatively large face
            .setTrackingEnabled(true) // enable face tracking
            .setClassificationType( /* eyes open and smile */FaceDetector.ALL_CLASSIFICATIONS)
            .setMode(FaceDetector.FAST_MODE) // for one face this is OK
            .build()

        if (!detector.isOperational) {
            Log.w("MainActivity", "Detector Dependencies are not yet available")
        } else {
            setViewVisibility(R.id.tv_capture)
            setViewVisibility(R.id.surfaceView)
            setupSurfaceHolder()
        }
    }


    private fun setViewVisibility(id: Int) {
        val view = findViewById<View>(id)
        if (view != null) {
            view.visibility = View.VISIBLE
        }
    }

    private fun setupSurfaceHolder() {
        cameraSource = CameraSource.Builder(this, detector)
            .setFacing(CameraSource.CAMERA_FACING_FRONT)
            .setRequestedFps(2.0f)
            .setAutoFocusEnabled(true)
            .build()
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)
    }

    fun captureImage() {
        Handler(Looper.getMainLooper()).postDelayed({ runOnUiThread { clickImage() } }, 200)
    }

    private fun clickImage() {
        cameraSource?.takePicture(null, this)
    }

    private fun startCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            cameraSource?.start(surfaceHolder)
            detector.setProcessor(
                LargestFaceFocusingProcessor(
                    detector,
                    GraphicFaceTracker(
                        this
                    )
                )
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startCamera()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        cameraSource?.stop()
    }

    override fun onPictureTaken(bytes: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        bitmap.toFile(this)?.let {
            intent.putExtra("file_path", it.absolutePath)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}