package com.example.signatureexample

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.signatureexample.MainActivity.Companion.PERMISSIONS_STORAGE
import com.example.signatureexample.MainActivity.Companion.REQUEST_EXTERNAL_STORAGE
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class SignatureHelper(private val context: Context) {

    private fun saveSignature(signatureBitmap: Bitmap) {
        if (addJpgSignatureToGallery(signatureBitmap)) {
            Toast.makeText(
                context,
                "Signature saved into the Gallery",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
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


    private fun verifyStoragePermissions() {
        val permission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
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

    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(photo)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }


}