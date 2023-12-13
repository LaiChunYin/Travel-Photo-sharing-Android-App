package com.example.travel_photo_sharing_app.screens

import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.travel_photo_sharing_app.R
import com.example.travel_photo_sharing_app.databinding.ActivityCameraBinding
import com.example.travel_photo_sharing_app.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class CameraActivity : AppCompatActivity() {
    private val TAG:String = "PHOTO_SHARING_APP"
    lateinit var binding: ActivityCameraBinding

    lateinit var cameraController: LifecycleCameraController

    private fun initializeCameraController() {
        cameraController = LifecycleCameraController(applicationContext)
        cameraController.bindToLifecycle(this)
        binding.previewView.controller = cameraController
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (hasPermissions() == false) {
            ActivityCompat.requestPermissions(this, CAMERAX_PERMISSIONS, 0)
        } else {
        }
        initializeCameraController()

        binding.btnFlipCamera.setOnClickListener {
            flipCamera()
        }

        binding.btnTakePhoto.setOnClickListener {
            savePhotoToDeviceMemory()
        }
    }


    private fun flipCamera() {

        if (cameraController.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        } else if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
            cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        }
    }


    private fun savePhotoToDeviceMemory()  {

        val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PhotoSharingApp")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()


        // capture and save the photo
        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Snackbar.make(binding.root, "Saving photo failed, see console for error", Snackbar.LENGTH_LONG).show()
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    var msg = "Photo capture succeeded: ${output.savedUri}"
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun hasPermissions():Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    companion object {

        private val CAMERAX_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA
        )
    }

}