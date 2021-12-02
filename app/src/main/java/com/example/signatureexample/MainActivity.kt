package com.example.signatureexample

import android.Manifest
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
import java.util.*


class MainActivity : AppCompatActivity(), SignaturePad.OnSignedListener {

    private lateinit var binding: ActivityMainBinding

    companion object {
        val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyStoragePermissions()
        binding = ActivityMainBinding.inflate(layoutInflater)
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

    }

    override fun onSigned() {
        binding.btnSave.isEnabled = true
        binding.btnClear.isEnabled = true
    }

    override fun onClear() {
        binding.btnSave.isEnabled = true
        binding.btnClear.isEnabled = true
    }


}