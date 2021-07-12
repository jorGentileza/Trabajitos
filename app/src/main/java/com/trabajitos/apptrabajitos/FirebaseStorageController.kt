package com.trabajitos.apptrabajitos

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage

class FirebaseStorageController {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val TAG = "FirebaseStorageManager"

    fun uploadProfile(
        imageURI: Uri,
        user: String
    ) {
        val uploadTask = storageRef.child("users/profilePicture/$user").putFile(imageURI)
        uploadTask.addOnSuccessListener {
            Log.e(TAG, "successful upload")
        }.addOnFailureListener {
            Log.e(TAG, "upload fail because ${it.printStackTrace()}")
        }
    }

    fun uploadProofPhotos(
        imageURI: Uri,
        user: String,
        number: String
    ) {
        val uploadTask = storageRef.child("users/proofPhotos/$user/$number").putFile(imageURI)
        uploadTask.addOnSuccessListener {
            Log.e(TAG,"successfull upload")
        }.addOnFailureListener{
            Log.e(TAG,"updaload fail because ${it.printStackTrace()}")
        }
    }

    fun deleteAccount(user: String){
        val deleteProofs = storageRef.child("users/proofPhotos/$user/").delete()
        deleteProofs.addOnSuccessListener {
            Log.e(TAG,"photos were deleted")
        }.addOnFailureListener{
            Log.e(TAG,"error deleting photos because ${it.printStackTrace()}")
        }
        val deleteProfile = storageRef.child("users/profilePicture/$user").delete()
        deleteProfile.addOnSuccessListener {
            Log.e(TAG,"photo were deleted")
        }.addOnFailureListener{
            Log.e(TAG,"error deleting photo because ${it.printStackTrace()}")
        }
    }


}