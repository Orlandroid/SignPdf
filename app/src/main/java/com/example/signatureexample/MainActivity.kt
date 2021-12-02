package com.example.signatureexample

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.signatureexample.databinding.ActivityMainBinding
import com.github.gcacace.signaturepad.views.SignaturePad

class MainActivity : AppCompatActivity(), SignaturePad.OnSignedListener {

    private lateinit var binding: ActivityMainBinding
    private val signatureHelper = SignatureHelper(this)

    companion object {
        val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signatureHelper.verifyStoragePermissions()
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.signaturePad.setOnSignedListener(this)
        binding.btnClear.setOnClickListener {
            binding.signaturePad.clear()
        }
        binding.btnSave.setOnClickListener {
            signatureHelper.saveSignature(signatureBitmap = binding.signaturePad.signatureBitmap)
        }
        setContentView(binding.root)
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


    override fun onStartSigning() {

    }

    override fun onSigned() {
        binding.btnSave.isEnabled = true
        binding.btnClear.isEnabled = true
    }

    override fun onClear() {
        binding.btnSave.isEnabled = false
        binding.btnClear.isEnabled = false
    }


}