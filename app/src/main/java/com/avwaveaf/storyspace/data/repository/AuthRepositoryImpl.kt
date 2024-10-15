package com.avwaveaf.storyspace.data.repository

import android.util.Log
import com.avwaveaf.storyspace.data.model.LoginResponse
import com.avwaveaf.storyspace.data.model.RegisterResponse
import com.avwaveaf.storyspace.network.ApiService
import com.avwaveaf.storyspace.utils.SessionManager
import com.google.gson.Gson
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response: LoginResponse = apiService.login(email, password)

            if (response.error) {
                Result.failure(Exception(response.message))
            } else {
                // Save the token and login session
                sessionManager.saveLoginSession(response.loginResult.token)
                Result.success(response.loginResult.token)
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            Result.failure(Exception("Login Failed: ${errorBody.message}"))
        } catch (e: Exception) {
            Result.failure(e) // Handle other exceptions
        }
    }

    override suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Result<RegisterResponse> {
        return try {
            Log.d("Register", "from repo $name, email: $email, pwd:$password")
            val response = apiService.registerUser(name, email, password)
            if (!response.error) {
                Result.success(response) // Return success
            } else {
                Result.failure(Exception("Registration failed: ${response.message}")) // Handle failure
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            Result.failure(Exception("Registration Failed: ${errorBody.message}"))
        } catch (e: Exception) {
            Result.failure(e) // Handle other exceptions
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession() // Clear session using SessionManager
    }
}