package com.avwaveaf.storyspace.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem> = emptyList(),

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

@Parcelize
@Entity(tableName = "story")
data class ListStoryItem(
	@PrimaryKey
	@SerializedName("id")
	val id: String,

	@SerializedName("photoUrl")
	val photoUrl: String? = null,

	@SerializedName("createdAt")
	val createdAt: String? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("description")
	val description: String? = null,

	@SerializedName("lon")
	val lon: Double? = null,

	@SerializedName("lat")
	val lat: Double? = null
): Parcelable
