package com.example.whatsappstatussaver2022.models

import android.graphics.Bitmap
import android.net.Uri

data class InternalStoragePhoto(
    val name: String,
    val photoUri: Uri,
    var isVideo:Int=0
)