package com.example.signatureexample.ui.pdfview

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.signatureexample.R
import com.example.signatureexample.databinding.FragmentPdfViewBinding
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle

class PdfViewFragment : Fragment() {

    private var _binding: FragmentPdfViewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPdfViewBinding.inflate(layoutInflater)
        displayFromAsset()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sign,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.firmar -> {
                findNavController().navigate(R.id.action_pdfViewFragment_to_signatureFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun displayFromAsset() {
        binding.pdfView.fromAsset("git.pdf")
            .defaultPage(0)
            .enableAnnotationRendering(true)
            .scrollHandle(DefaultScrollHandle(requireContext()))
            .spacing(10)
            .load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}