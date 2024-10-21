package com.example.mediamaster.helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionHelper {


    private fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun hasContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }


    fun requestPermissions(permissionLauncher: ActivityResultLauncher<Array<String>>, mediaType: String) {
        when (mediaType) {
            "IMAGES", "VIDEOS", "AUDIOS", "DOCUMENTS" -> requestStoragePermissions(permissionLauncher, mediaType)
            "CONTACTS" -> requestContactsPermission(permissionLauncher)
        }
    }


    private fun requestStoragePermissions(permissionLauncher: ActivityResultLauncher<Array<String>>, mediaType: String) {
        when (mediaType) {
            "IMAGES", "VIDEOS" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    ))
                } else {
                    permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                }
            }
            "AUDIOS" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_AUDIO))
                } else {
                    permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                }
            }
            "DOCUMENTS" -> {
                permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }
    }


    private fun requestContactsPermission(permissionLauncher: ActivityResultLauncher<Array<String>>) {
        permissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
    }

    fun isPermissionGranted(context: Context, mediaType: String): Boolean {
        return when (mediaType) {
            "IMAGES", "VIDEOS", "AUDIOS", "DOCUMENTS" -> hasStoragePermission(context)
            "CONTACTS" -> hasContactsPermission(context)
            else -> false
        }
    }
}
