package com.example.signatureexample.ui.pdfview

import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.*
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.signatureexample.R
import com.example.signatureexample.databinding.FragmentPdfViewBinding
import com.example.signatureexample.ui.signature.SignatureFragment.Companion.IMAGE_BASE_64
import com.example.signatureexample.ui.signature.SignatureHelper.Companion.SIGNATURE_IMAGE_NAME
import com.example.signatureexample.ui.util.getDirectoryPictures
import com.example.signatureexample.ui.util.readXMLFileFromAsset
import java.lang.StringBuilder

class PdfViewFragment : Fragment() {

    private var _binding: FragmentPdfViewBinding? = null
    private val binding get() = _binding!!
    private var xmlStr: StringBuilder? = null
    private var urlImage = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPdfViewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(IMAGE_BASE_64)?.observe(viewLifecycleOwner) { data ->
            Log.w("data",data)
        }
    }

    private fun getDataForWebViewPge(imageBase64: String) {
        if (imageBase64.isEmpty() or (imageBase64 == "")){
            return
        }
        Log.w("Android", Environment.getExternalStorageDirectory().absolutePath)
        urlImage = "${getDirectoryPictures()}/$SIGNATURE_IMAGE_NAME"
        Log.w("DIR", urlImage)
        xmlStr = readXMLFileFromAsset(requireContext())
        xmlStr!!.replace(xmlStr!!.indexOf("*signPath"), xmlStr!!.indexOf("*signPath")+"*signPath".length,imageBase64)
        binding.webView.loadDataWithBaseURL(
            "file:///android_asset/html/",
            xmlStr.toString(),
            "text/html",
            "utf-8",
            ""
        )
    }

    private fun initWebView() {
        with(binding) {
            webView.settings.javaScriptEnabled = true
            webView.settings.blockNetworkImage = false
            webView.settings.domStorageEnabled = true
            webView.setInitialScale(1)
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            webView.isScrollbarFadingEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sign, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.firmar -> {
                findNavController().navigate(R.id.action_pdfViewFragment_to_signatureFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}