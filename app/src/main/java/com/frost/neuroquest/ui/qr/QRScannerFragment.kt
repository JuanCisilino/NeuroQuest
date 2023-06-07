package com.frost.neuroquest.ui.qr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.frost.neuroquest.R
import com.frost.neuroquest.UserPrefs
import com.frost.neuroquest.databinding.FragmentQRScannerBinding
import com.google.zxing.integration.android.IntentIntegrator

class QRScannerFragment : Fragment() {

    private lateinit var binding: FragmentQRScannerBinding
    private lateinit var viewModel: QRScannerViewModel
    private lateinit var userPrefs : UserPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[QRScannerViewModel::class.java]
        binding = FragmentQRScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPrefs = UserPrefs(requireContext())
        checkAndExecute()
    }

    private fun checkAndExecute(){
        val url = userPrefs.getString("url")
        if (url != "") {
            binding.webView.loadUrl(url!!)
            userPrefs.save("url", "")
        } else {
            initScanner()
        }
    }

    override fun onResume() {
        super.onResume()
        checkAndExecute()
    }

    private fun initScanner(){
        val integrator = IntentIntegrator(requireActivity())
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt(getString(R.string.qr_scanner))
        integrator.setTorchEnabled(true)
        integrator.setBeepEnabled(true)
        integrator.initiateScan()
    }


}