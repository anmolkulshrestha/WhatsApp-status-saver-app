package com.example.whatsappstatussaver2022

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import kotlinx.android.synthetic.main.fragment_settings.*


class settingsFragment : Fragment() {
    lateinit var whatsappbusinesss: Switch
    lateinit var photosingaller: Switch
    lateinit var contactus:TextView
    lateinit var privacypolicy:TextView
    lateinit var about:TextView
lateinit var aboutDialog:Dialog
    lateinit var privacypolicyDialog:Dialog
    var iswhatsappbusiness = false
    var isInGallery: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_settings, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        whatsappbusinesss = view.findViewById(R.id.whatsappbusiness)
        photosingaller = view.findViewById(R.id.photosingallery)
        contactus = view.findViewById(R.id.contactus)
        privacypolicy = view.findViewById(R.id.privacypolicy)
        about = view.findViewById(R.id.about)
        setUpAboutDialog()
       setUpPrivacyPolicyDialog()


privacypolicy.setOnClickListener { privacypolicyDialog.show() }
        about.setOnClickListener { aboutDialog.show() }
        requireContext().getSharedPreferences("PHOTOS_IN_GALLERY", Context.MODE_PRIVATE)
            .let { sharedPreferences ->
                if (sharedPreferences.contains("shouldInGallery")) {
                    isInGallery = sharedPreferences.getBoolean("shouldInGallery", true)
                    photosingaller.isChecked = !isInGallery
                } else {
                    sharedPreferences.edit().putBoolean("shouldInGallery", true)
                    isInGallery = true
                    photosingaller.isChecked = !isInGallery

                }
            }




        requireContext().getSharedPreferences("WHATSAPP_BUSINES", Context.MODE_PRIVATE)
            .let { sharedPreferences ->
                if (sharedPreferences.contains("isWhatsAppBusiness")) {
                    Log.d("hji", "1")
                    iswhatsappbusiness = sharedPreferences.getBoolean("isWhatsAppBusiness", false)
                    Log.d("hjji", iswhatsappbusiness.toString())
                    whatsappbusinesss.isChecked = iswhatsappbusiness
                } else {

                    sharedPreferences.edit().putBoolean("isWhatsAppBusiness", false)
                    iswhatsappbusiness = false
                    Log.d("hjjji", iswhatsappbusiness.toString())
                    whatsappbusinesss.isChecked = iswhatsappbusiness

                }
            }




        photosingaller.setOnCheckedChangeListener { compoundButton, b ->
            if (photosingaller.isChecked) {
                Toast.makeText(requireContext(),"Saved Images and Videoes will not be availabe in gallery", Toast.LENGTH_SHORT).show()
                requireContext().getSharedPreferences("PHOTOS_IN_GALLERY", Context.MODE_PRIVATE)
                    .edit {
                        putBoolean("shouldInGallery", false)
                        isInGallery = false
                    }

            } else {
                Toast.makeText(requireContext(),"Saved Images and Videoes will  be availabe in gallery", Toast.LENGTH_SHORT).show()
                requireContext().getSharedPreferences("PHOTOS_IN_GALLERY", Context.MODE_PRIVATE)
                    .edit {
                        putBoolean("shouldInGallery", true)
                        isInGallery = true
                    }
            }


        }
            whatsappbusinesss.setOnCheckedChangeListener { compoundButton, b ->
                if (whatsappbusinesss.isChecked) {
                    Toast.makeText(requireContext(),"Switched To WhatsApp Business", Toast.LENGTH_SHORT).show()
                    requireContext().getSharedPreferences("WHATSAPP_BUSINES", Context.MODE_PRIVATE)
                        .edit {
                            putBoolean("isWhatsAppBusiness", true)

                        }

                } else {
                    Toast.makeText(requireContext(),"Switched To WhatsApp", Toast.LENGTH_SHORT).show()
                    requireContext().getSharedPreferences("WHATSAPP_BUSINES", Context.MODE_PRIVATE)
                        .edit {
                            putBoolean("isWhatsAppBusiness", false)

                        }
                }


            }

        }

    fun setUpAboutDialog(){
        aboutDialog = Dialog(requireContext())
        aboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        aboutDialog.setContentView(R.layout.about_dialog)

//        about.setText("about"+"/n"+"Status Saver is an application which"+"/n"+"saves WhatsApp Status images and"+"/n"
//        +"videoes to yout device."+"/n"+"Status Saver is not affliated to WhatsApp in any manner")







    }
    fun setUpPrivacyPolicyDialog(){
        privacypolicyDialog = Dialog(requireContext())
        privacypolicyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        privacypolicy=privacypolicyDialog.findViewById(R.id.textView3)
        privacypolicyDialog.setContentView(R.layout.privacypolicy_dialog)
//
//        privacypolicy.setText("about"+"/n"+"Status Saver is an application which"+"/n"+"saves WhatsApp Status images and"+"/n"
//        +"videoes to yout device."+"/n"+"Status Saver is not affliated to WhatsApp in any manner")







    }
    }

