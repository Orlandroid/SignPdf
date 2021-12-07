package com.example.signatureexample.ui.pdfview

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.signatureexample.R
import com.example.signatureexample.databinding.FragmentPdfViewBinding
import com.example.signatureexample.ui.util.readXMLFileFromAsset
import java.lang.StringBuilder

class PdfViewFragment : Fragment() {

    private var _binding: FragmentPdfViewBinding? = null
    private val binding get() = _binding!!
    var xmlStr: StringBuilder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPdfViewBinding.inflate(layoutInflater)
        getDataForWebViewPge()
        return binding.root
    }


    private fun getDataForWebViewPge(){
        xmlStr = readXMLFileFromAsset(requireContext(),"html/Myfirma.html")
        xmlStr!!.replace(xmlStr!!.indexOf("*signPath"), xmlStr!!.indexOf("*signPath")+"*signPath".length,"/imagenes/android")
        Log.w("ERROR",xmlStr.toString())
        binding.webView.loadDataWithBaseURL("file:///android_asset/html/", xmlStr.toString(), "text/html", "utf-8", "")
        Log.w("CONTENT",xmlStr.toString())
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