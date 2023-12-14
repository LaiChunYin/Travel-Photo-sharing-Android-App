package com.example.travel_photo_sharing_app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.lang.Exception

class CameraImageHelper {
    private val tag = "CameraImageHelper"
    companion object {
//        var instance: CameraImageHelper? = null
//        fun getInstance(context: Context?): AuthenticationHelper{
//            return if(instance != null){
//                Log.d(tag, "getting existing instance")
//                instance!!
//            }
//            else{
//                if(context != null){
//                    Log.d(tag, "getting new camera image helper instance")
//                    instance = CameraImageHelper()
//                    instance!!
//                }
//                else{
//                    Log.e(tag, "Please provide a context to create a authenticationHelper instance")
//                    throw Exception("Please provide a context to create a authenticationHelper instance")
//                }
//            }
//        }
        fun base64ToBitmap(base64: String): Bitmap {
            Log.d(tag, "string to be decoded ${base64}")
            val decodedImg: ByteArray = Base64.decode(base64, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedImg, 0, decodedImg.size)
        }
        fun bitmapToBase64(bitmap: Bitmap): String {
            val byteOutStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteOutStream)
            return Base64.encodeToString(byteOutStream.toByteArray(), Base64.DEFAULT)
        }
    }
}