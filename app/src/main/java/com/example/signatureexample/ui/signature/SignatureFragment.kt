package com.example.signatureexample.ui.signature

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.signatureexample.databinding.FragmentSignatureHelperBinding
import com.github.gcacace.signaturepad.views.SignaturePad

class SignatureFragment : Fragment(), SignaturePad.OnSignedListener {

    private var _binding: FragmentSignatureHelperBinding? = null
    private val binding get() = _binding!!
    private var signatureHelper: SignatureHelper? = null

    companion object {
        const val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignatureHelperBinding.inflate(layoutInflater, container, false)
        binding.signaturePad.setOnSignedListener(this)
        verifyStoragePermissions()
        signatureHelper = SignatureHelper(requireActivity())
        binding.btnClear.setOnClickListener {
            binding.signaturePad.clear()
        }
        binding.btnSave.setOnClickListener {
            signatureHelper?.saveSignature(signatureBitmap = binding.signaturePad.signatureBitmap)
            findNavController().popBackStack()
        }
        return binding.root
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        requireContext(),
                        "Tienes permisos para write",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun verifyStoragePermissions() {
        val permission = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
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