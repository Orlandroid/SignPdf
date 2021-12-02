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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verifyStoragePermissions(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.signaturePad.setOnSignedListener(this)
        binding.btnClear.setOnClickListener {
            binding.signaturePad.clear()
        }
        binding.btnSave.setOnClickListener {
            saveSign()
        }
        setContentView(binding.root)
    }


    private fun saveSign() {
        val signatureBitmap: Bitmap = binding.signaturePad.signatureBitmap
        saveMediaToStorage(signatureBitmap)
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


    private fun saveMediaToStorage(bitmap: Bitmap) {
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Captured View and saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addJpgSignatureToGallery(signature: Bitmap): Boolean {
        var result = false
        try {
            val photo = File(
                getAlbumStorageDir("SignaturePad"),
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
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.exists()) {
            Log.e("SignaturePad", "Directory not created")
        }
        Log.w("Error", file.toString())
        return file
    }


    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, photo: File?) {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val stream: OutputStream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.size <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private fun verifyStoragePermissions(activity: Activity?) {
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }


    private fun saveToGallery(bitmap: Bitmap) {
        var outputStream: FileOutputStream? = null
        val file = Environment.getExternalStorageDirectory()
        val dir = File(file.absolutePath.toString() + "/MyPics")
        dir.mkdirs()
        val filename = String.format("%d.png", System.currentTimeMillis())
        val outFile = File(dir, filename)
        try {
            outputStream = FileOutputStream(outFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        try {
            outputStream!!.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            outputStream!!.close()
        } catch (e: Exception) {
            e.printStackTrace()
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