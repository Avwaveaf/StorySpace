package com.avwaveaf.storyspace.data.repository.story


import android.content.Context
import com.avwaveaf.storyspace.R
import com.avwaveaf.storyspace.data.model.RegisterResponse
import com.avwaveaf.storyspace.data.model.StoryResponse
import com.avwaveaf.storyspace.network.ApiService
import com.google.gson.Gson
import retrofit2.HttpException
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val context: Context
) :
    StoryRepository {
    override suspend fun getStories(): Result<StoryResponse> {
        return try {
            val response = apiService.getStories()
            if (!response.error!!) {
                Result.success(response) // Return success
            } else {
                Result.failure(
                    Exception(
                        context.getString(
                            R.string.error_fetching_story,
                            response.message
                        )
                    )
                )
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            Result.failure(
                Exception(
                    context.getString(
                        R.string.error_fetching_story,
                        errorBody.message
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e) // Handle other exceptions
        }
    }
}