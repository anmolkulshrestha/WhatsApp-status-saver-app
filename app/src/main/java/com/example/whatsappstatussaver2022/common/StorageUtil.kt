package com.example.whatsappstatussaver2022.common

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.example.whatsappstatussaver2022.models.Status
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
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName")


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

suspend fun saveVideoGallery(context: Context,displayName: String, videoUri: Uri): Boolean {
    return withContext(Dispatchers.IO) {
        val videoCollection = sdk29AndUp {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } ?: MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                   val inputstream=context.contentResolver.openInputStream(videoUri)
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "$displayName")
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


 suspend fun loadFilesFromInternalStorage(context: Context): List<Status> {
    return withContext(Dispatchers.IO) {

        val files = context.filesDir.listFiles()

        files?.filter { it.canRead() && it.isFile && (it.name.endsWith(".jpg") || it.name.endsWith(".mp4")) }?.map {


            Status(it.name,it.toUri().toString(),it.lastModified())
        } ?: listOf()
    }
}

 suspend fun savePhotoToInternalStorage(filename: String, bmp: Bitmap,context: Context): Boolean {
    return withContext(Dispatchers.IO) {

        try {
            context.openFileOutput("$filename", MODE_PRIVATE).use { stream ->
                if(!bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {

                    throw IOException("Couldn't save bitmap.")
                }

            }

            true
        } catch(e: IOException) {

            e.printStackTrace()

            false
        }
    }
}

suspend fun saveVideoToInternalStorage(filename: String, videoUri: Uri,context: Context): Boolean {
    return withContext(Dispatchers.IO) {
        val inputstream=context.contentResolver.openInputStream(videoUri)
        try {
            context.openFileOutput("$filename", MODE_PRIVATE).use { stream ->

                stream?.write(inputstream?.readBytes())

            }?:throw IOException("Couldn't be saved")

            true
        } catch(e: IOException) {

            e.printStackTrace()

            false
        }
    }
}

suspend fun loadallFilesFromInternalStorage(context: Context):List<String> {
    val files= mutableListOf<String>()
    return withContext(Dispatchers.IO) {

        val files = context.filesDir.listFiles().toMutableList()
         files?.filter { it.canRead() && it.isFile && (it.name.endsWith(".jpg") || it.name.endsWith(".mp4")) }?.map {

            it.name.split(".")[0]
         } ?: listOf()

    }

}
suspend fun deletePhotoFromInternalStorage(filename: String,context: Context): Boolean {
    return try {
        context.deleteFile(filename)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}