package com.bersamadapa.recylefood.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.external.UiKitApi

class MidtransHelper(private val context: Context) {

    // Initialize Midtrans SDK
    fun initialize(clientKey: String, baseUrl: String) {
        UiKitApi.Builder()
            .withMerchantClientKey(clientKey) // Set your client key
            .withContext(context) // Set context
            .withMerchantUrl(baseUrl) // Set base URL
            .enableLog(true) // Enable logs for debugging (optional)
            .withColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // Optional color theme
            .build()
    }

    // Function to start payment using Snap Token
    fun startPaymentWithSnapToken(activity: Activity, snapToken: String, launcher: ActivityResultLauncher<Intent>) {
        UiKitApi.getDefaultInstance().startPaymentUiFlow(activity, launcher, snapToken)
    }
}
