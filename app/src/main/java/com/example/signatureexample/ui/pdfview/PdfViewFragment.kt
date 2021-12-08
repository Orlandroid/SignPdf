package com.example.signatureexample.ui.pdfview

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.signatureexample.R
import com.example.signatureexample.databinding.FragmentPdfViewBinding
import com.example.signatureexample.ui.util.Works
import com.example.signatureexample.ui.util.readXMLFileFromAsset
import java.lang.StringBuilder

class PdfViewFragment : Fragment() {

    private var _binding: FragmentPdfViewBinding? = null
    private val binding get() = _binding!!
    private var xmlStr: StringBuilder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPdfViewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("firmado")
            ?.observe(viewLifecycleOwner) { data ->
                if (data) {
                    getDataForWebViewPge()
                }
            }
    }

    private fun getDataForWebViewPge() {
        val base64Image = "data:image/jpeg;base64,${Works.ImageToBase64(requireContext())}"
        xmlStr = readXMLFileFromAsset(requireContext())
        xmlStr!!.replace(
            xmlStr!!.indexOf("MyImagen"),
            xmlStr!!.indexOf("MyImagen") + "MyImagen".length,
            base64Image
        )
        binding.webView.loadDataWithBaseURL(
            "file:///android_asset/html/",
            xmlStr.toString(),
            "text/html",
            "utf-8",
            ""
        )
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