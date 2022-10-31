package com.example.whatsappstatussaver2022

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.edit


class settingsFragment : Fragment() {
      lateinit var whatsappbusiness:Switch
      lateinit var photosingaller:Switch
      lateinit var contactus:TextView
      lateinit var privacypolicy:TextView
      lateinit var about:TextView
 var toggel:Boolean=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_settings, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        whatsappbusiness=view.findViewById(R.id.whatsappbusiness)
        photosingaller=view.findViewById(R.id.photosingallery)
        contactus=view.findViewById(R.id.contactus)
        privacypolicy=view.findViewById(R.id.privacypolicy)
        about=view.findViewById(R.id.about)
       requireContext().getSharedPreferences("PHOTOS_IN_GALLERY",Context.MODE_PRIVATE).let { sharedPreferences ->
          if(sharedPreferences.contains("shouldInGallery")){
          toggel=   sharedPreferences.getBoolean("shouldInGallery",true)
             photosingaller.isChecked=!toggel
          }else{
              sharedPreferences.edit().putBoolean("shouldInGallery",true)
              photosingaller.isChecked=false

          }
      }
        requireContext().getSharedPreferences("PHOTOS_IN_GALLERY",Context.MODE_PRIVATE).let { sharedPreferences ->
            if(sharedPreferences.contains("shouldInGallery")){
                toggel=   sharedPreferences.getBoolean("shouldInGallery",true)
                photosingaller.isChecked=!toggel
            }else{
                sharedPreferences.edit().putBoolean("shouldInGallery",true)
                photosingaller.isChecked=false

            }
        }
        requireContext().getSharedPreferences("WHATSAPP_BUSINESS",Context.MODE_PRIVATE).let { sharedPreferences ->
            if(sharedPreferences.contains("isWhatsAppBusiness")){
                toggel=   sharedPreferences.getBoolean("isWhatsAppBusiness",true)
                whatsappbusiness.isChecked=!toggel
            }else{
                sharedPreferences.edit().putBoolean("isWhatsAppBusiness",true)
                whatsappbusiness.isChecked=false

            }
        }

       whatsappbusiness.setOnCheckedChangeListener { compoundButton, b ->
            if(whatsappbusiness.isChecked){
                requireContext().getSharedPreferences("WHATSAPP_BUSINESS", Context.MODE_PRIVATE).edit {
                    putBoolean("isWhatsAppBusiness",false)

                }

            }else{
                requireContext().getSharedPreferences("WHATSAPP_BUSINESS", Context.MODE_PRIVATE).edit {
                    putBoolean("isWhatsAppBusiness",true)
                }
            }
        }

    }


}

private const val photosingallery="PHOTOS_IN_GALLERY"
private const val whatsappbusiness="WHATSAPP_BUSINESS"