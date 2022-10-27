package com.example.whatsappstatussaver2022.common

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import java.io.File

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

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

var WHATS_APP_STATUS_DIRECTORY: File = File("/storage/emulated/0/WhatsApp/Media/.Statuses")
var WHATS_APP_STATUS_DIRECTORY_NEW: File = File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/.Statuses")