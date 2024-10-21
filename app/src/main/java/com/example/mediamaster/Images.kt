package com.example.mediamaster

import android.net.Uri
import android.os.Bundle
import android.Manifest
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.annotation.SuppressLint
import android.provider.ContactsContract
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediamaster.adaptor.MediaAdapter.MediaAdapter
import com.example.mediamaster.adaptor.MediaAdapter.MediaItem
import com.example.mediamaster.adaptor.MediaAdapter.MediaRow
import com.example.mediamaster.adaptor.MediaAdapter.MediaType
import com.example.mediamaster.databinding.ActivityImagesBinding
import com.example.mediamaster.helper.PermissionHelper
import com.tashila.pleasewait.PleaseWaitDialog
import androidx.appcompat.app.AlertDialog

class Images : AppCompatActivity() {
    private lateinit var binding: ActivityImagesBinding
    private lateinit var adapter: MediaAdapter
    private val mediaRows = mutableListOf<MediaRow>()
    private lateinit var progressDialog: PleaseWaitDialog

    private var permissionRequestCount = 0
    private val maxPermissionRequests = 3

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val mediaType = intent.getStringExtra("MEDIA_TYPE") ?: return@registerForActivityResult
            val allPermissionsGranted = isPermissionGranted(mediaType, permissions)

            if (allPermissionsGranted) {
                loadMedia()
            } else {
                permissionRequestCount++
                if (permissionRequestCount <= maxPermissionRequests) {
                    checkAndRequestPermissions(mediaType)
                } else {
                    showSettingsDialog()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.recyclerViewImagesAndVideo.layoutManager = GridLayoutManager(this, 1)
        adapter = MediaAdapter(mediaRows)
        binding.recyclerViewImagesAndVideo.adapter = adapter
        val mediaType = intent.getStringExtra("MEDIA_TYPE")
        binding.heading.text = mediaType

        checkAndRequestPermissions(mediaType)
    }

    private fun checkAndRequestPermissions(mediaType: String?) {
        if (mediaType != null) {
            if (PermissionHelper.isPermissionGranted(this, mediaType)) {
                loadMedia()
            } else {
                if (shouldShowRequestPermissionRationale(mediaType)) {
                    showRationaleDialog(mediaType)
                } else {
                    PermissionHelper.requestPermissions(permissionLauncher, mediaType)
                }
            }
        }
    }

    private fun showRationaleDialog(mediaType: String) {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This app needs access to your $mediaType to function properly.")
            .setPositiveButton("OK") { _, _ ->
                PermissionHelper.requestPermissions(permissionLauncher, mediaType)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("You have denied the permission multiple times. Please go to settings to enable it.")
            .setPositiveButton("Go to Settings") { _, _ ->
                startActivity(android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun isPermissionGranted(mediaType: String, permissions: Map<String, Boolean>): Boolean {
        return when (mediaType) {
            "IMAGES" -> permissions[Manifest.permission.READ_MEDIA_IMAGES] == true || permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
            "VIDEOS" -> permissions[Manifest.permission.READ_MEDIA_VIDEO] == true || permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
            "AUDIOS" -> permissions[Manifest.permission.READ_MEDIA_AUDIO] == true || permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
            "CONTACTS" -> permissions[Manifest.permission.READ_CONTACTS] == true
            "DOCUMENTS" -> permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
            else -> false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMedia() {
        progressDialog = PleaseWaitDialog(context = this)
        progressDialog.show()

        when (intent.getStringExtra("MEDIA_TYPE")) {
            "IMAGES" -> loadImages()
            "VIDEOS" -> loadVideos()
            "CONTACTS" -> loadContacts()
            "AUDIOS" -> loadAudio()
            "DOCUMENTS" -> loadDocuments()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadImages() {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val tempList = mutableListOf<MediaItem>()

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                tempList.add(MediaItem(contentUri, MediaType.IMAGE))

                if (tempList.size == 4) {
                    mediaRows.add(MediaRow(tempList.toList()))
                    tempList.clear()
                }
            }

            if (tempList.isNotEmpty()) {
                mediaRows.add(MediaRow(tempList.toList()))
            }

            adapter.notifyDataSetChanged()
        }

        progressDialog.dismiss()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadVideos() {
        val projection = arrayOf(MediaStore.Video.Media._ID)
        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, MediaStore.Video.Media.DATE_ADDED + " DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val tempList = mutableListOf<MediaItem>()

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                tempList.add(MediaItem(contentUri, MediaType.VIDEO))

                if (tempList.size == 4) {
                    mediaRows.add(MediaRow(tempList.toList()))
                    tempList.clear()
                }
            }

            if (tempList.isNotEmpty()) {
                mediaRows.add(MediaRow(tempList.toList()))
            }

            adapter.notifyDataSetChanged()
        }

        progressDialog.dismiss()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadContacts() {
        val projection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameColumn = it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
            val numberColumn = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameColumn)
                val number = it.getString(numberColumn)
                mediaRows.add(MediaRow(listOf(MediaItem(type = MediaType.CONTACT, contactName = name, contactNumber = number))))
            }
            adapter.notifyDataSetChanged()
        }

        progressDialog.dismiss()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadAudio() {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE
        )

        val audioMimeTypes = listOf("audio/mpeg", "audio/x-wav", "audio/ogg", "audio/mp4")
        val selection = "${MediaStore.Audio.Media.MIME_TYPE} IN (${audioMimeTypes.joinToString { "?" }})"
        val selectionArgs = audioMimeTypes.toTypedArray()

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            MediaStore.Audio.Media.DATE_ADDED + " DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            val tempList = mutableListOf<MediaItem>()

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                val name = it.getString(nameColumn)
                val size = it.getLong(sizeColumn)

                tempList.add(MediaItem(uri = contentUri, type = MediaType.AUDIO, audioFileName = name, audioFileSize = size))

                if (tempList.size == 4) {
                    mediaRows.add(MediaRow(tempList.toList()))
                    tempList.clear()
                }
            }

            if (tempList.isNotEmpty()) {
                mediaRows.add(MediaRow(tempList.toList()))
            }

            adapter.notifyDataSetChanged()
        }

        progressDialog.dismiss()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadDocuments() {
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE
        )
        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            null,
            null,
            MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            val tempList = mutableListOf<MediaItem>()

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Files.getContentUri("external"),
                    id.toString()
                )
                val name = it.getString(nameColumn)
                val size = it.getLong(sizeColumn)

                tempList.add(MediaItem(uri = contentUri, type = MediaType.DOCUMENT, documentFileName = name, documentFileSize = size))

                if (tempList.size == 4) {
                    mediaRows.add(MediaRow(tempList.toList()))
                    tempList.clear()
                }
            }

            if (tempList.isNotEmpty()) {
                mediaRows.add(MediaRow(tempList.toList()))
            }

            adapter.notifyDataSetChanged()
        }

        progressDialog.dismiss()
    }
}
