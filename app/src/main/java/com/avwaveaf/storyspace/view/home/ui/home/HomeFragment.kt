package com.avwaveaf.storyspace.view.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.avwaveaf.storyspace.databinding.FragmentHomeBinding
import com.avwaveaf.storyspace.view.home.ui.detail.DetailActivity
import com.avwaveaf.storyspace.view.home.ui.home.adapter.LoadingStateAdapter
import com.avwaveaf.storyspace.view.home.ui.home.adapter.StoryAdapter
import com.avwaveaf.storyspace.view.home.ui.settings.SettingsFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        applyThemFromPreference()
        showLoading(true)


        // Initialize RecyclerView
        setupRecyclerView()

        observeData()
        // Observe error messages
        homeViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(root, it, Snackbar.LENGTH_LONG).show()
            }

            showLoading(false)
        }


        return root
    }


    private fun observeData() {
        // Observe paged stories
            homeViewModel.pagedStories.observe(viewLifecycleOwner){ pagingData ->
                lifecycleScope.launch {
                    storyAdapter.submitData(pagingData)
                }
            }
    }



    private fun setupRecyclerView() {
        // Initialize the StoryAdapter with LoadStateFooter
        storyAdapter = StoryAdapter { story, itemBinding ->
            // Create intent to start DetailActivity
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("story_data", story) // Pass story data to DetailActivity

            // Set up shared element transition options
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                androidx.core.util.Pair(itemBinding.imageStory, "sharedImage"),
                androidx.core.util.Pair(itemBinding.textStoryName, "sharedTitle"),
                androidx.core.util.Pair(itemBinding.textStoryDescription, "sharedDescription")
            )

            // Start the activity with shared element transition
            startActivity(intent, options.toBundle())
        }


        storyAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Error) {
                val error = (loadState.refresh as LoadState.Error).error
                Snackbar.make(binding.root, "Error: ${error.message}", Snackbar.LENGTH_LONG).show()
            }
            showLoading(loadState.refresh is LoadState.Loading)
        }

        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storyAdapter.withLoadStateHeaderAndFooter(
                header = LoadingStateAdapter { storyAdapter.retry() },
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
        }

    }

    override fun onResume() {
        super.onResume()
        // This will trigger a refresh every time the fragment resumes
        homeViewModel.requestRefresh()
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