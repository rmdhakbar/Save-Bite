package com.bersamadapa.recylefood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bersamadapa.recylefood.utils.MidtransHelper
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_FAILED
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_INVALID
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_PENDING
import com.midtrans.sdk.corekit.models.snap.TransactionResult.STATUS_SUCCESS
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import com.midtrans.sdk.uikit.internal.util.UiKitConstants.STATUS_CANCELED

class PaymentActivity : ComponentActivity()  {
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var midtransHelper: MidtransHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Activity Result Launcher
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val transactionResult =
                        it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                    Toast.makeText(this, "${transactionResult?.transactionId}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        // Retrieve the token from the Intent
        val paymentToken = intent.getStringExtra("PAYMENT_TOKEN") ?: return

        // Initialize MidtransHelper
        midtransHelper = MidtransHelper(this)
        midtransHelper.initialize(
            BuildConfig.MIDTRANS_CLIENT_KEY,
            BuildConfig.MIDTRANS_BASE_URL
        )

        // Start the payment flow with the provided token
        midtransHelper.startPaymentWithSnapToken(this, paymentToken, launcher)
    }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (resultCode == RESULT_OK) {
                val transactionResult = data?.getParcelableExtra<TransactionResult>(
                    UiKitConstants.KEY_TRANSACTION_RESULT
                )
                if (transactionResult != null) {
                    when (transactionResult.status) {
                        STATUS_SUCCESS -> {
                            navigateToMainActivity("orderHistory")
                        }
                        STATUS_PENDING -> {
                            navigateToMainActivity("orderHistory")
                        }
                        STATUS_FAILED -> {
                            Toast.makeText(this, "Transaction Failed. ID: " + transactionResult.transactionId, Toast.LENGTH_LONG).show()
                        }
                        STATUS_CANCELED -> {
                            navigateToMainActivity("dashboard")
                        }
                        STATUS_INVALID -> {
                            Toast.makeText(this, "Transaction Invalid. ID: " + transactionResult.transactionId, Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(this, "Transaction ID: " + transactionResult.transactionId + ". Message: " + transactionResult.status, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
                }
            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    override fun onBackPressed() {
        super.onBackPressed()

        // Handle back press to ensure user returns to the previous screen
        finish()
    }

    private fun navigateToMainActivity(screenNavigation : String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigateTo", screenNavigation)
        }
        startActivity(intent)
        finish() // Ensure PaymentActivity is removed from the back stack
    }

}
