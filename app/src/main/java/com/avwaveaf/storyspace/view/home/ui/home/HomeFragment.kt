package com.avwaveaf.storyspace.view.home.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.avwaveaf.storyspace.databinding.FragmentHomeBinding
import com.avwaveaf.storyspace.view.home.ui.home.adapter.StoryAdapter
import com.avwaveaf.storyspace.view.home.ui.settings.SettingsFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        applyThemFromPreference()
        showLoading(true)

        fetchStories()

        // Initialize RecyclerView
        setupRecyclerView()

        // Observe the stories
        homeViewModel.stories.observe(viewLifecycleOwner) { stories ->
            storyAdapter.submitList(stories)
            showLoading(false)
        }

        // Observe error messages
        homeViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(root, it, Snackbar.LENGTH_LONG).show()
            }

            showLoading(false)
        }

        return root
    }

    private fun fetchStories() {
        homeViewModel.fetchStories()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.adapter = storyAdapter
    }


    private fun showLoading(flag: Boolean) {
        if (flag) {
            with(binding) {
                pbLoading.visibility = View.VISIBLE
                laodingOverlay.visibility = View.VISIBLE
            }
        } else {
            with(binding) {
                pbLoading.visibility = View.GONE
                laodingOverlay.visibility = View.GONE
            }
        }
    }


    private fun applyThemFromPreference() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isDarkMode = sharedPreferences.getBoolean(SettingsFragment.THEME_KEY, false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}