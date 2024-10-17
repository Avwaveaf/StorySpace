package com.avwaveaf.storyspace.view.home.ui.compose

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.avwaveaf.storyspace.databinding.FragmentComposeStoryBinding
import com.avwaveaf.storyspace.view.home.ui.compose.CameraActivity.Companion.CAMERAX_RESULT


class ComposeStoryFragment : Fragment() {

    private var _binding: FragmentComposeStoryBinding? = null
    private val binding get() = _binding!!

    private var currentUri: Uri? = null

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // insert the current uri
            currentUri = uri
            // after we get the uri we can call showImage to inflate imageview with selected image
            showImage()
        } else {
            Toast.makeText(requireContext(), "No Media Selected", Toast.LENGTH_SHORT).show()
        }

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
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
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        setupListeners()
        updateAddStoryButtonState()
    }

    private fun setupListeners() {
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCameraX() }

        binding.etStoryTitle.addTextChangedListener { updateAddStoryButtonState() }
        binding.etStoryDescription.addTextChangedListener { updateAddStoryButtonState() }

        binding.btnAddStory.setOnClickListener {
            // Implement your logic to add the story here
            Toast.makeText(requireContext(), "Adding story...", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateAddStoryButtonState() {
        val isTitleEmpty = binding.etStoryTitle.text.isNullOrBlank()
        val isDescriptionEmpty = binding.etStoryDescription.text.isNullOrBlank()
        val isImageSelected = currentUri != null

        binding.btnAddStory.isEnabled = !isTitleEmpty && !isDescriptionEmpty && isImageSelected
    }


    private fun startCameraX() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }


}