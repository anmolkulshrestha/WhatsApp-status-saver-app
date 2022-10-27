package com.example.whatsappstatussaver2022.models

import android.net.Uri
import android.os.Parcelable
import androidx.documentfile.provider.DocumentFile
import kotlinx.android.parcel.Parcelize
import java.io.File


data class Status(  var file:DocumentFile){



    var title:String=file.name!!


    var fileUri:String=file.uri.toString()


    var isVideo:Int=if(file.name!!.endsWith(".mp4")){  1}else{0}






        // if (file.name.endsWith(".mp4") ) { 1} else{ 0}
    }

