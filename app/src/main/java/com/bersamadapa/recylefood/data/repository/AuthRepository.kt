package com.bersamadapa.recylefood.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.bersamadapa.recylefood.utils.PasswordUtils
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class AuthRepository(private val firestore: FirebaseFirestore) {

    // Login function using Firestore (no FirebaseAuth)
    suspend fun login(email: String, password: String): Result<String> { // Change the return type to Result<String> for user ID
        return try {
            // Fetch the user data from Firestore based on the email
            val userSnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (userSnapshot.isEmpty) {
                // If no user is found, return an error
                return Result.failure(Exception("User not found"))
            }

            // Get the stored password hash and document ID from Firestore
            val user = userSnapshot.documents.first()
            val storedHash = user.getString("password") ?: throw Exception("Password not found")
            val userId = user.id // Firestore document ID

            // Verify the entered password against the stored hash
            if (PasswordUtils.verifyPassword(storedHash, password)) {
                Result.success(userId) // Return the document ID as the result
            } else {
                Result.failure(Exception("Invalid password"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun register(username: String, email: String, noHandphone: String, password: String, retypedPassword: String): Result<Unit> {
        return try {
            // Validate password and retypedPassword match
            if (password != retypedPassword) {
                return Result.failure(Exception("Passwords do not match"))
            }

            // Check if email already exists in Firestore
            val existingUserSnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!existingUserSnapshot.isEmpty) {
                // If an email is found, return an error
                return Result.failure(Exception("Email already exists"))
            }

            // Hash the password using bcrypt
            val hashedPassword = PasswordUtils.hashPassword(password)

            // Prepare user data
            val user = hashMapOf(
                "email" to email,
                "username" to username,
                "noHandphone" to noHandphone,
                "password" to hashedPassword, // Store the hashed password
                "points" to 0,
            )

            // Add the user to Firestore
            firestore.collection("users")
                .add(user)
                .await() // Await the result of the Firestore add operation

            // Return success
            Result.success(Unit)
        } catch (e: FirebaseFirestoreException) {
            // Specific exception for Firestore errors
            Result.failure(Exception("Failed to register user: ${e.message}", e))
        } catch (e: Exception) {
            // Generic exception catch for other errors
            Result.failure(Exception("An unexpected error occurred: ${e.message}", e))
        }
    }

}
