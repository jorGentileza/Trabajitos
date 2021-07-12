package com.trabajitos.apptrabajitos

    import android.app.Activity
    import android.content.Intent

    object ImageController {
        fun selectPhotoFromGallery(activity: Activity,code: Int){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type ="image/*"
            activity.startActivityForResult(intent,code)
        }
    }

