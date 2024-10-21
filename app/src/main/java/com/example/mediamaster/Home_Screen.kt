package com.example.mediamaster

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import com.example.mediamaster.databinding.ActivityHomeScreenBinding

class Home_Screen : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.images.setOnClickListener {
            val intent = Intent(this, Images::class.java)
            intent.putExtra("MEDIA_TYPE", "IMAGES")
            startActivity(intent)
        }

        binding.videos.setOnClickListener {
            val intent = Intent(this, Images::class.java)
            intent.putExtra("MEDIA_TYPE", "VIDEOS")
            startActivity(intent)
        }

        binding.contact.setOnClickListener {
            val intent = Intent(this, Images::class.java)
            intent.putExtra("MEDIA_TYPE", "CONTACTS")
            startActivity(intent)
        }

        binding.audio.setOnClickListener {
            val intent = Intent(this, Images::class.java)
            intent.putExtra("MEDIA_TYPE", "AUDIOS")
            startActivity(intent)
        }


        binding.files.setOnClickListener {
            val intent = Intent(this, Images::class.java)
            intent.putExtra("MEDIA_TYPE", "DOCUMENTS")
            startActivity(intent)
        }

        displayStorageInfo()
    }

    private fun displayStorageInfo() {
        val stat = StatFs(Environment.getExternalStorageDirectory().absolutePath)
        val totalBytes = stat.totalBytes
        val availableBytes = stat.availableBytes

        val totalGB = totalBytes / (1024 * 1024 * 1024)
        val availableGB = availableBytes / (1024 * 1024 * 1024)
        val usedGB = totalGB - availableGB

        val progress = ((usedGB.toDouble() / totalGB) * 100).toInt()

        binding.filledStorageTextView.text = getString(R.string.Full_Space, usedGB)
        binding.remainingStorageTextView.text = getString(R.string.Free_Space, availableGB)
        binding.storageProgressBar.progress = progress

        val usedPercentage = ((usedGB.toDouble() / totalGB) * 100).toInt()

        binding.UsedInPercentage.text = getString(R.string.Used_In_Percentage, usedPercentage)
    }
}
