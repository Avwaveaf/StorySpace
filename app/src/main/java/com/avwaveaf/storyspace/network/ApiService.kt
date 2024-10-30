package com.avwaveaf.storyspace.network

import com.avwaveaf.storyspace.data.model.LoginResponse
import com.avwaveaf.storyspace.data.model.NewStoryResponse
import com.avwaveaf.storyspace.data.model.RegisterResponse
import com.avwaveaf.storyspace.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): NewStoryResponse

    @Multipart
    @POST("stories")
    suspend fun uploadDataWithLocation(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): NewStoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
    ): StoryResponse
}