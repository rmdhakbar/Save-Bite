package com.bersamadapa.recylefood.data.repository

import android.util.Log
import com.bersamadapa.recylefood.data.model.User
import com.bersamadapa.recylefood.network.api.ApiService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.wait
import retrofit2.awaitResponse
import java.lang.Exception

class UserRepository(private val firestore: FirebaseFirestore, private val apiService: ApiService) {

    suspend fun getUserById(userId: String): Result<User> {
        return try {
            // Fetch user data from Firestore in a background thread
            val documentSnapshot = withContext(Dispatchers.IO) {
                firestore.collection("users").document(userId).get().await()
            }

            if (!documentSnapshot.exists()) {
                return Result.failure(Exception("User not found"))
            }

            Log.d("UserRepository", "User data: ${documentSnapshot.data}")

            // Convert Firestore document to a User object
            val user = documentSnapshot.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Error parsing user data"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user data", e)
            Result.failure(e)
        }
    }

    suspend fun updateUser(
        userId: String,
        username: String,
        profilePicture: MultipartBody.Part?
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                // Convert username to a RequestBody
                val usernameRequestBody = username.toRequestBody(MultipartBody.FORM)

                // Send update request using Retrofit's suspend function
                val response = apiService.updateUser(
                    userId = userId,
                    username = usernameRequestBody,
                    profile_picture = profilePicture
                )

                // Assuming `ApiResponse<User>` is the return type
                if (response.statusCode == 200) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception("Failed with message: ${response.message}"))
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Error updating user data", e)
                Result.failure(e)
            }
        }
    }

}
