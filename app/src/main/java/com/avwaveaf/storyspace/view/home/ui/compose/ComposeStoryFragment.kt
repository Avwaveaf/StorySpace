package com.avwaveaf.storyspace.view.home.ui.compose

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.avwaveaf.storyspace.databinding.FragmentComposeStoryBinding


class ComposeStoryFragment : Fragment() {

    private var _binding: FragmentComposeStoryBinding? = null
    private val binding get() = _binding!!

    private var currentUri: Uri? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // insert the current uri
            currentUri = uri
            // after we get the uri we can call showImage to inflate imageview with selected image
            showImage()
        } else {
            Log.d("main_activity/photo_picker", "No Media Selected!")
            Toast.makeText(requireContext(), "No Media Selected", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showImage() {
        currentUri?.let {
            binding.ivImagePreview.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.ivImagePreview.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComposeStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}