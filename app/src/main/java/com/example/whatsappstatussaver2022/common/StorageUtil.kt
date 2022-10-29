package com.example.whatsappstatussaver2022.common

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

inline fun <T> sdk29AndUp(onSdk29:()->T):T?{
    return  if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
        onSdk29()
    }else {null}
}

inline fun <T> sdk30AndUp(onSdk30:()->T):T?{
    return  if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
        onSdk30()
    }else {null}
}

//inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
//    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
//    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
//}

var WHATS_APP_STATUS_DIRECTORY: File = File("/storage/emulated/0/WhatsApp/Media/.Statuses")
var WHATS_APP_STATUS_DIRECTORY_NEW: File = File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/.Statuses")



 suspend fun savePhotoGalley(context: Context,displayName: String, bmp: Bitmap): Boolean {
    return withContext(Dispatchers.IO) {
        val imageCollection = sdk29AndUp {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }
        try {
              context.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                  context.contentResolver.openOutputStream(uri).use { outputStream ->
                    if(!bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        } catch(e: IOException) {
            e.printStackTrace()
            false
        }
    }
}

suspend fun saveVideoGalley(context: Context,displayName: String, videoUri: Uri): Boolean {
    return withContext(Dispatchers.IO) {
        val videoCollection = sdk29AndUp {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                   val inputstream=context.contentResolver.openInputStream(videoUri)
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "$displayName.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")

        }
        try {
            context.contentResolver.insert(videoCollection, contentValues)?.also { uri ->
                context.contentResolver.openOutputStream(uri).use {
it?.write(inputstream?.readBytes())
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        } catch(e: IOException) {
            e.printStackTrace()
            false
        }
    }
}








