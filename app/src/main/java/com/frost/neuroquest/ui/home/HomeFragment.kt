package com.frost.neuroquest.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.frost.neuroquest.CurrentUser
import com.frost.neuroquest.databinding.FragmentHomeBinding
import com.frost.neuroquest.model.Places

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter : HomeAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMembers()
        setupRecycler()
    }

    private fun setupRecycler() {
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycler.adapter = adapter
    }

    private fun initMembers() {
        adapter = HomeAdapter()
        adapter.onPlaceClickCallback = { openWebView(it) }
        adapter.setList(CurrentUser.disponibles, requireContext())
    }

    override fun onResume() {
        super.onResume()
        adapter.setList(CurrentUser.disponibles, requireContext())
    }

    private fun openWebView(it: Places) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.url))
        startActivity(browserIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}