package com.example.signatureexample.ui.util

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder

@Throws(IOException::class)
fun readXMLFileFromAsset(context: Context, filePath: String?): StringBuilder {
    val reader = BufferedReader(InputStreamReader(context.assets.open("MyFirma.html")))
    val sb = StringBuilder()
    var mLine = reader.readLine()
    while (mLine != null) {
        sb.append(mLine) // process line
        mLine = reader.readLine()
    }
    reader.close()
    return sb
}