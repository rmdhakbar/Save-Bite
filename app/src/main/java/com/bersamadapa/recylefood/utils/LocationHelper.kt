package com.bersamadapa.recylefood.utils


import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // This method will return a Location object or null if the location is not available
    suspend fun getUserLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            // Check if permission is granted
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val location = task.result
                        if (location != null) {
                            continuation.resume(location) // Return location
                        } else {
                            continuation.resumeWithException(Exception("Location not found"))
                        }
                    } else {
                        continuation.resumeWithException(Exception("Failed to get location"))
                    }
                }
            } else {
                continuation.resumeWithException(Exception("Location permission not granted"))
            }
        }
    }

    fun calculateDistance(userLatitude: Double, userLongitude: Double, restaurantLatitude:Double, restaurantLongitude:Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(userLatitude, userLongitude, restaurantLatitude, restaurantLongitude, results)
        return results[0] // Distance in meters
    }
}
