package com.example.signatureexample

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.signatureexample.databinding.ActivityMainBinding
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.*
import java.lang.Exception
import java.util.*
import android.content.ContextWrapper
import android.os.Build
import android.provider.MediaStore


class MainActivity : AppCompatActivity(), SignaturePad.OnSignedListener {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var imagePath: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyStoragePermissions()
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.signaturePad.setOnSignedListener(this)
        binding.btnClear.setOnClickListener {
            binding.signaturePad.clear()
        }
        binding.btnSave.setOnClickListener {
            saveSignature()
        }
        setContentView(binding.root)
    }


    private fun saveSignature() {
        val signatureBitmap: Bitmap = binding.signaturePad.signatureBitmap
        if (addJpgSignatureToGallery(signatureBitmap)) {
            Toast.makeText(
                this@MainActivity,
                "Signature saved into the Gallery",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@MainActivity,
                "Unable to store the signature",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun addJpgSignatureToGallery(signature: Bitmap): Boolean {
        var result = false
        try {
            val directoryPictures = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            )
            val photo = File(
                directoryPictures,
                String.format("Signature_%d.jpg", System.currentTimeMillis())
            )
            Log.w(this.toString(), photo.toString())
            saveBitmapToJPG(signature, photo)
            scanMediaFile(photo)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
            Log.w("Error", e.message.toString())
        }
        Log.w("RESULT", result.toString())
        return result
    }


    private fun getAlbumStorageDir(albumName: String?): File {
        val directoryPictures = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        val file = File(directoryPictures, "imagenesPad")
        if (!file.exists()) {
            Log.w(this.toString(), "Directorio no creado")
        }
        return file
    }


    fun getAlbumStorageDihr(albumName: String?): File? {
        // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created")
        }
        return file
    }


    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, photo: File) {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val stream: OutputStream = FileOutputStream(photo)
        val isSaveImage = newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        Log.w("is save", isSaveImage.toString())
        stream.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Tienes permisos para write", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun verifyStoragePermissions() {
        val permission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }


    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(photo)
        mediaScanIntent.data = contentUri
        this@MainActivity.sendBroadcast(mediaScanIntent)
    }


    override fun onStartSigning() {
        Toast.makeText(this@MainActivity, "OnStartSigning", Toast.LENGTH_SHORT).show()
    }

    override fun onSigned() {
        binding.btnClear.isEnabled = true
        binding.btnSave.isEnabled = true
    }

    override fun onClear() {
        binding.btnClear.isEnabled = false
        binding.btnSave.isEnabled = false
    }
}