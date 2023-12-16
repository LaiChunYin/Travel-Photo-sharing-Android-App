package com.example.travel_photo_sharing_app.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.travel_photo_sharing_app.screens.CameraActivity
import java.io.ByteArrayOutputStream

class CameraImageHelper {
    private val tag = "CameraImageHelper"
    companion object {
        fun hasCameraPermissions(applicationContext: Context):Boolean {
            return CameraActivity.CAMERAX_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
            }
        }
        fun base64ToBitmap(base64: String): Bitmap {
            Log.d(tag, "string to be decoded ${base64}")
            val decodedImg: ByteArray = Base64.decode(base64, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedImg, 0, decodedImg.size)
        }
        fun bitmapToBase64(bitmap: Bitmap?): String? {
            if(bitmap == null){
                return null
            }
            val byteOutStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteOutStream)
            return Base64.encodeToString(byteOutStream.toByteArray(), Base64.DEFAULT)
        }
    }
}