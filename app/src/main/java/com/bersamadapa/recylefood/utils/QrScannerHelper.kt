package com.bersamadapa.recylefood.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity

object QrScannerHelper {

    // Function to launch the QR scanner
    fun launchQrScanner(activity: Activity) {
        val integrator = IntentIntegrator(activity)
        integrator.setPrompt("Scan a QR code") // Set prompt message
        integrator.setOrientationLocked(true) // Lock the orientation
        integrator.setBeepEnabled(true) // Enable beep sound after scanning
        integrator.captureActivity = CaptureActivity::class.java

        // Lock orientation to portrait (vertical)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        integrator.initiateScan() // Launch the scanner
    }

    // Function to handle the QR scan result
    fun handleQrResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?, navController: NavController) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents != null) {
                    // Successfully scanned QR code
                    Toast.makeText(activity, "Scanned: ${result.contents}", Toast.LENGTH_LONG).show()
                    // Pass the scanned result to the activity (for navigation)
                    val restaurantId = extractRestaurantId(result.contents)
                    navigateToRestaurantDetail(navController, restaurantId)
                } else {
                    // QR scan canceled
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Extract the restaurantId from QR code data (adjust this based on your QR format)
    private fun extractRestaurantId(qrData: String): String {
        Log.d("QrScannerHelper", "Raw QR Data: $qrData") // Log the raw QR data

        return qrData
    }

    // Navigate to the RestaurantDetail screen
    private fun navigateToRestaurantDetail(navController: NavController, restaurantId: String) {
        navController.navigate("restaurant_detail/$restaurantId") // Pass restaurantId to the route
    }
}
