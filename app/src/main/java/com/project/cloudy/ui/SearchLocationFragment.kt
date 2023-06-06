package com.project.cloudy.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.cloudy.databinding.FragmentSearchLocationBinding
import com.project.cloudy.ui.adapters.LocationAdapter
import com.project.cloudy.utils.Resource

class SearchLocationFragment : Fragment() {
    private var _binding: FragmentSearchLocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WeatherViewModel by activityViewModels()
    private val rvAdapter = LocationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.updateSearchedLocation(query.trim())
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        setUpUi()

    }

    private fun setUpUi() {
        binding.locationRv.layoutManager = LinearLayoutManager(requireContext())
        binding.locationRv.adapter = rvAdapter
        viewModel.searchLocationResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Successful -> {
                    binding.progressBar.isVisible = false
                    binding.errorText.isVisible = false
                    rvAdapter.submitList(response.data)
                }
                is Resource.Failure -> {
                    binding.locationRv.isVisible= false
                    binding.progressBar.isVisible = false
                    binding.errorText.isVisible = true
                    binding.errorText.text = response.msg
                }
                is Resource.Empty -> {
                    binding.progressBar.isVisible = false
                    binding.errorText.isVisible = true
                    binding.errorText.text = "No result Found"
                }
                is Resource.Loading->{
                    binding.errorText.isVisible = false
                    binding.progressBar.isVisible = true
                }
                else -> Unit
            }
        }

        rvAdapter.adapterClick {
            val navigate =
                SearchLocationFragmentDirections.actionSearchLocationFragmentToWeatherDetailsFragment(
                    it
                )
            findNavController().navigate(navigate)
        }
    }
}