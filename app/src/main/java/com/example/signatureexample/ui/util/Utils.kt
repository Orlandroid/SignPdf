package com.example.signatureexample.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.util.Log
import com.example.signatureexample.R
import java.io.*
import java.util.Base64.getEncoder


@Throws(IOException::class)
fun readXMLFileFromAsset(context: Context): StringBuilder {
    val reader = BufferedReader(InputStreamReader(context.assets.open("MyFirma.html")))
    val sb = StringBuilder()
    var mLine = reader.readLine()
    while (mLine != null) {
        sb.append(mLine)
        mLine = reader.readLine()
    }
    reader.close()
    return sb
}

fun getDirectoryPictures(): File =
    Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
    )

fun bitMapToBase64(bitmap: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val b = baos.toByteArray()
    Log.w("Imagen", b.toString())
    return Base64.encodeToString(b, Base64.DEFAULT)
}


