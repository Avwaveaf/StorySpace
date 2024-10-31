package com.avwaveaf.storyspace.view.home.ui.compose

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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
import androidx.fragment.app.viewModels
import com.avwaveaf.storyspace.R
import com.avwaveaf.storyspace.databinding.FragmentComposeStoryBinding
import com.avwaveaf.storyspace.helper.reduceFileImage
import com.avwaveaf.storyspace.helper.uriToFile
import com.avwaveaf.storyspace.view.home.ui.compose.CameraActivity.Companion.CAMERAX_RESULT
import com.avwaveaf.storyspace.view.home.ui.home.HomeViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.material.materialswitch.MaterialSwitch
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class ComposeStoryFragment : Fragment() {

    private var _binding: FragmentComposeStoryBinding? = null
    private val binding get() = _binding!!

    private var currentUri: Uri? = null
    private val viewModel: ComposeStoryViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var locationSwitch: MaterialSwitch
    private var latitude: Double? = null
    private var longitude: Double? = null

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
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private fun observeViewModel() {
        viewModel.isUploading.observe(viewLifecycleOwner) { isUploading ->
            showLoading(isUploading)
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showToast(it)
            }
        }
        viewModel.uploadSuccess.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                //update latest stories
                homeViewModel.requestRefresh()
                goBackToPreviousFragment()
            }
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
    ): View {
        _binding = FragmentComposeStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        // Initialize the Switch and set a listener
        locationSwitch = binding.switchIncludeLocation
        locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLocation()
            } else {
                latitude = null
                longitude = null // Clear location if switch is off
            }
        }

        observeViewModel()
        setupListeners()
        updateAddStoryButtonState()
    }

    private fun setupListeners() {
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCameraX() }

        binding.etStoryTitle.addTextChangedListener { updateAddStoryButtonState() }
        binding.etStoryDescription.addTextChangedListener { updateAddStoryButtonState() }

        binding.btnAddStory.setOnClickListener {
            showLoading(true)
            uploadImage()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        latitude = it.latitude
                        longitude = it.longitude

                    } ?: run {
                        Toast.makeText(
                            requireContext(),
                            "Unable to get location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun uploadImage() {
        currentUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            val description = binding.etStoryDescription.text.toString()
            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

            val multipartBody = MultipartBody.Part.createFormData(
                "photo", imageFile.name, requestImageFile
            )
            // Check if location is enabled and lat/lon are available
            if (locationSwitch.isChecked) {
                // Convert lat/lon to RequestBody
                val latBody = latitude.toString().toRequestBody("text/plain".toMediaType())
                val lonBody = longitude.toString().toRequestBody("text/plain".toMediaType())

                if (latitude != null && longitude != null) {
                    // Call uploadDataWithLocation
                    viewModel.uploadDataWithLocation(multipartBody, requestBody, latBody, lonBody)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(
                            R.string.location_are_not_found_lat_lon,
                            latitude.toString(),
                            longitude.toString()
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                // Call uploadImage without location
                viewModel.uploadImage(multipartBody, requestBody)
            }
        } ?: showToast(getString(R.string.image_file_cannot_be_null_warning))
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun showLoading(flag: Boolean) {
        if (flag) {
            with(binding) {
                pbLoading.visibility = View.VISIBLE
                laodingOverlay.visibility = View.VISIBLE
                pbBackgroundOverlay.visibility = View.VISIBLE
            }
        } else {
            with(binding) {
                pbLoading.visibility = View.GONE
                laodingOverlay.visibility = View.GONE
                pbBackgroundOverlay.visibility = View.GONE
            }
        }
    }


    private fun goBackToPreviousFragment() {
        parentFragmentManager.popBackStack()
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