package com.avwaveaf.storyspace.view.home.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.avwaveaf.storyspace.data.model.ListStoryItem
import com.avwaveaf.storyspace.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide


class DetailActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Get the passed story data
        val story: ListStoryItem? = intent.getParcelableExtra("story_data")
        story?.let {
            // Bind the data to your views, for example:
            binding.tvStoryTitle.text = it.name
            binding.tvStoryDescription.text = it.description
            Glide.with(this)
                .load(story.photoUrl)
                .into(binding.ivStoryImage)
        }
    }
}