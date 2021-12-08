package com.example.signatureexample.ui.signature


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.*


class SignatureHelper(private val context: Context) {

    companion object {
        const val SIGNATURE_IMAGE_NAME = "Signature.jpg"
        const val URL_IMAGE_BASE_64 = ""
    }

    fun saveSignature(signatureBitmap: Bitmap) {
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
                SIGNATURE_IMAGE_NAME
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


    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(photo)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }


}