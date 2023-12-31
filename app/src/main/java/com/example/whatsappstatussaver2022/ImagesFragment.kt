package com.example.whatsappstatussaver2022

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.edit
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappstatussaver2022.adapters.StatusAdapter
import com.example.whatsappstatussaver2022.common.*
import com.example.whatsappstatussaver2022.models.Status
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_images.*
import kotlinx.android.synthetic.main.video_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.io.File
import java.net.URI
import java.util.ArrayList
import kotlin.math.log


class ImagesFragment : Fragment() {
    lateinit var dialog: Dialog
    lateinit var recyclerView: RecyclerView
    lateinit var documenturi: String
    lateinit var progressBar: ProgressBar
    lateinit var grantpermissionbutton:Button
    lateinit var permissiontext:TextView
    lateinit var search:ImageView
    var isReadPermissionGranted:Boolean=false
    var isWritePermissionGranted:Boolean=false
    lateinit var permissionlauncher: ActivityResultLauncher<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_images, container, false)
       grantpermissionbutton=view.findViewById(R.id.grantpermission)
        permissiontext=view.findViewById<TextView>(R.id.permissiontext)
        recyclerView=view.findViewById(R.id.recyclerView)
        permissionlauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions->
            //   isManageExternalstoragePermissionGranted=permissions[Manifest.permission.MANAGE_EXTERNAL_STORAGE] ?: isManageExternalstoragePermissionGranted
            isReadPermissionGranted=permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted=permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
            if(isReadPermissionGranted && isWritePermissionGranted){

      getstatuses()
            }else{
                requestOrUpdatePermissions()
            }

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPermissionDialog()
        setuprecyclerview()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility=View.VISIBLE
        progressBar=view.findViewById(R.id.progressBar)

        sdk29AndUp {


            checkIfPermissionGrantedForAndroid10AndAbove()
            grantpermissionbutton.setOnClickListener {

                getpermissionforfolder()

            }

     }?:belowsdk29()


    }

    fun setuprecyclerview(){
        recyclerView.apply {
            layoutManager= GridLayoutManager(activity,3)
        }
    }

    fun setUpPermissionDialog() {
        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.permission_dialog)
        val dialogButton: Button = dialog.findViewById(R.id.cancle) as Button
        dialogButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                dialog.dismiss()
                progressBar.visibility=View.GONE
//                permissiontext.visibility=View.VISIBLE
//                permissiontext.text="Pls Grant Permission"
//                search.visibility=View.GONE
            }
        })

        val grantpermissionbutton: Button = dialog.findViewById(R.id.grantpermission) as Button
        grantpermissionbutton.setOnClickListener(object : View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onClick(p0: View?) {
                getpermissionforfolder()
            }
        })
    }


    fun getPermissionDialog() {
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkIfPermissionGrantedForAndroid10AndAbove() {
        var should=false
        requireContext().getSharedPreferences("WHATSAPP_BUSINES",Context.MODE_PRIVATE).let { sharedPreferences ->
            if(sharedPreferences.contains("isWhatsAppBusiness")){
                should= sharedPreferences.getBoolean("isWhatsAppBusiness",false)
                Log.d("fgh", should.toString())

            }else{
                sharedPreferences.edit().putBoolean("isWhatsAppBusiness",false)
                Log.d("fg1", should.toString())
                should=false
                Log.d("fg2", should.toString())
            }
        }
        if(!should){
            requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE).let { sharedPreferences ->


                if (sharedPreferences.contains(LAST_OPENED_URI_KEY)) {
                    val documentUri = sharedPreferences.getString(LAST_OPENED_URI_KEY, null)?.toUri()
                        ?: return getPermissionDialog()

                    grantpermissionbutton.visibility=View.GONE
                    permissiontext.visibility=View.GONE
                    CoroutineScope(Dispatchers.IO).launch {  showStatuses(documentUri) }
                } else {

                    getPermissionDialog()
                }
            }


        }else{
            requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE).let { sharedPreferences ->


                if (sharedPreferences.contains("WhatsAppBusinessURIkey")) {
                    val documentUri = sharedPreferences.getString("WhatsAppBusinessURIkey", null)?.toUri()
                        ?: return getPermissionDialog()

                    grantpermissionbutton.visibility=View.GONE
                    permissiontext.visibility=View.GONE
                    CoroutineScope(Dispatchers.IO).launch {  showStatuses(documentUri) }
                } else {

                    getPermissionDialog()
                }
            }



        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getpermissionforfolder() {
        //   %2FWhatsApp%2FMedia%2F.Statuses
        val storagemanager =
            requireContext().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = storagemanager!!.primaryStorageVolume.createOpenDocumentTreeIntent()
        var should=false
       requireContext().getSharedPreferences("WHATSAPP_BUSINES",Context.MODE_PRIVATE).let { sharedPreferences ->
            if(sharedPreferences.contains("isWhatsAppBusiness")){
                should= sharedPreferences.getBoolean("isWhatsAppBusiness",false)
                Log.d("hh", should.toString())
            }else{
                sharedPreferences.edit().putBoolean("isWhatsAppBusiness",false)
                Log.d("hh1", should.toString())
                should=false
                Log.d("hh2", should.toString())
            }
        }
        Log.d("lol", should.toString())
if(!should){
    Log.d("lol", should.toString())
    intent.putExtra(
    "android.provider.extra.INITIAL_URI",
    "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses".toUri()
)}else{

    intent.putExtra(
        "android.provider.extra.INITIAL_URI",
        "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses".toUri())
}




      intent.putExtra("android.content.extra.SHOW_ADVANCED",true)
        startActivityForResult(intent, 9090)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 9090) {
           var treeUri=data?.data
     if(treeUri!=null){
         var should=false
         requireContext().getSharedPreferences("WHATSAPP_BUSINES",Context.MODE_PRIVATE).let { sharedPreferences ->
             if(sharedPreferences.contains("isWhatsAppBusiness")){
                 should= sharedPreferences.getBoolean("isWhatsAppBusiness",false)

             }else{
                 sharedPreferences.edit().putBoolean("isWhatsAppBusiness",false)

                 should=false

             }
         }
       requireContext().contentResolver.takePersistableUriPermission(treeUri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
         if(!should){
             requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE).edit {
                 putString(LAST_OPENED_URI_KEY,treeUri.toString() )
             }

         }else{ requireContext().getSharedPreferences(TAG, Context.MODE_PRIVATE).edit {
             putString("WhatsAppBusinessURIkey",treeUri.toString() )
         }}



     dialog.dismiss()
         progressBar.visibility=View.VISIBLE
         grantpermissionbutton.visibility=View.GONE
         permissiontext.visibility=View.GONE
         CoroutineScope(Dispatchers.IO).launch {  showStatuses(treeUri) }

     }




        }


    }


   suspend fun showStatuses(treeUri:Uri){
           withContext(Dispatchers.Main){
               progressBar.visibility=View.VISIBLE
           }

       var fileDoc= mutableListOf<DocumentFile>()
       var job=  CoroutineScope(Dispatchers.IO).launch {
           fileDoc=DocumentFile.fromTreeUri(requireContext(),treeUri)?.listFiles()!!.toMutableList()
       }
       job.join()
       var statusList= mutableListOf<Status>()
       if(fileDoc!=null && fileDoc.size>0){
           for(file:DocumentFile in fileDoc!!){
               if(!file.name!!.endsWith(".nomedia")){
                   statusList.add(Status(file.name.toString(),file.uri.toString(),file.lastModified()))
               }


           }

           if(statusList.size!=0){
               withContext(Dispatchers.Main){

                   var statusadapter=StatusAdapter()
                   recyclerView.adapter=statusadapter
                   recyclerView.visibility=View.VISIBLE
                   for (item in statusList){
                       Log.d("lala", item.fileUri)
                   }
                   statusadapter.bindlist(statusList)
                   progressBar.visibility=View.GONE
               }
           }else{
               withContext(Dispatchers.Main){  permissiontext.visibility=View.VISIBLE
                   permissiontext.textSize=(20).toFloat()
                   permissiontext.text="No Status Currently Available"
                   grantpermissionbutton.visibility=View.VISIBLE
                   grantpermissionbutton.text="View Status "
                   progressBar.visibility=View.GONE
                   grantpermissionbutton.setOnClickListener {
                       Log.d("mnb", "lafda")
                   }



               }
           }



       }else if (fileDoc.size==0){
           withContext(Dispatchers.Main){
               progressBar.visibility=View.GONE
               permissiontext.visibility=View.VISIBLE
               permissiontext.text=" Hey Pls Install WhatsApp"
           }}else{
               withContext(Dispatchers.Main){
                   permissiontext.visibility=View.VISIBLE
                   permissiontext.text="Somethinf Went Wrong"
                   progressBar.visibility=View.GONE
               }

       }





    }

    fun belowsdk29(){
grantpermissionbutton.visibility=View.GONE
progressBar.visibility=View.GONE
checkIfPermissionGrantedForBelowSdk29()
        Log.d("anmol", "belowsdk29: ")
    }

    fun checkIfPermissionGrantedForBelowSdk29(){
        requestOrUpdatePermissions()
    }

    private fun requestOrUpdatePermissions(){

        isReadPermissionGranted= ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED


        isWritePermissionGranted= ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED


//        isManageExternalstoragePermissionGranted= ContextCompat.checkSelfPermission(this,Manifest.permission.MANAGE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
//        Log.d("anmol", isManageExternalstoragePermissionGranted.toString())
        val permissionrequest:MutableList<String> = ArrayList()


        if(!isReadPermissionGranted) {
            permissionrequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(!isWritePermissionGranted) {
            permissionrequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }


        if(permissionrequest.isNotEmpty()){
            permissionlauncher.launch(permissionrequest.toTypedArray())

        }

    }

   fun getstatuses(){
       if(WHATS_APP_STATUS_DIRECTORY.exists()){
           progressBar.visibility=View.VISIBLE
           execute(WHATS_APP_STATUS_DIRECTORY)
       }
       else if(WHATS_APP_STATUS_DIRECTORY_NEW.exists()){
           progressBar.visibility=View.VISIBLE
           execute(WHATS_APP_STATUS_DIRECTORY_NEW)
       }
       else{
           progressBar.visibility=View.GONE
           permissiontext.text="Hey pls Install WhatsApp in you phone"
           permissiontext.visibility=View.VISIBLE

       }


   }


    fun execute(dir: File){
        var files= mutableListOf<File>()
        var statuslist= mutableListOf<Status>()
        CoroutineScope(Dispatchers.IO).launch {


            var job= launch {files= dir.listFiles().toMutableList()
            }
            job.join()
            statuslist.clear()
            if(files!=null && files.size >0 ){

                for (file in files){

                    var status=Status(file.name.toString(),file.absolutePath.toString(),file.lastModified())
                    if (  status.title.endsWith(".jpg")|| status.title.endsWith(".mp4")) {
                        statuslist.add(status)
                    }

                }
                statuslist.sortByDescending { it.lastModified}


                withContext(Dispatchers.Main){
                    if(statuslist.size<=0){
                        permissiontext!!.visibility = View.VISIBLE
                        permissiontext!!.setText("Currently No status is Available")
                        grantpermissionbutton.visibility=View.VISIBLE
                        grantpermissionbutton.text="View Status"
                        grantpermissionbutton.setOnClickListener {

                        }
                    }else{
                        permissiontext!!.visibility = View.GONE
                        permissiontext!!.text = ""
                    }

                    var statusAdapter = StatusAdapter()
                    recyclerView!!.adapter = statusAdapter
                    statusAdapter!!.bindlist(statuslist)

                    progressBar!!.visibility=View.GONE

                }
            }else{
                withContext(Dispatchers.Main){
                    progressBar!!.visibility = View.GONE
                    permissiontext!!.visibility = View.VISIBLE
                    permissiontext!!.setText("Hey Pls Install WHatsApp in Your Phone")
                    Toast.makeText(activity, "no files found", Toast.LENGTH_SHORT)
                        .show()
                }
            }







        }
    }








}
private const val OPEN_DOCUMENT_REQUEST_CODE = 0x33
 const val TAG = "MainActivity"
private const val LAST_OPENED_URI_KEY =
    "com.example.android.ionopendocument.pef.LAS_OPENED_URI_KEY"