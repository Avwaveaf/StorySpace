package com.avwaveaf.storyspace.data.repository

import com.avwaveaf.storyspace.data.model.RegisterResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<String>
    suspend fun registerUser(name: String, email: String, password: String): Result<RegisterResponse>
    suspend fun logout()
}