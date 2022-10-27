package com.example.whatsappstatussaver2022.models

import android.net.Uri
import android.os.Parcelable
import androidx.documentfile.provider.DocumentFile
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class Status(  var filetitle:String,var fileUri:String,var lastModified:Long):Parcelable{



    var title:String=filetitle


    var Uri:String=fileUri
    var lastmodified:Long=lastModified

    var isVideo:Int=if(title.endsWith(".mp4")){  1}else{0}






        // if (file.name.endsWith(".mp4") ) { 1} else{ 0}
    }

