package com.frost.neuroquest.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.frost.neuroquest.R
import kotlinx.android.synthetic.main.dialog_loading.*

class LoadingDialog(@StringRes private val stringId: Int = R.string.loading_message): DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.dialog_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogLoadingTextView.setText(stringId)
    }

    fun show(fragmentManager: FragmentManager){
        show(fragmentManager, null)
    }


}