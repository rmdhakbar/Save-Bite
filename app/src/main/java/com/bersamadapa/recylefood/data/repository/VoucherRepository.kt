package com.bersamadapa.recylefood.data.repository

import android.util.Log
import com.bersamadapa.recylefood.data.model.Voucher
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class VoucherRepository(private val firestore: FirebaseFirestore) {

    companion object {
        private const val TAG = "VoucherRepository"
        private const val VOUCHERS_COLLECTION = "vouchers"
        private const val USERS_COLLECTION = "users"
        private const val CLAIMED_VOUCHERS_COLLECTION = "voucher"
    }

    // Fetch all available vouchers
    suspend fun getAllVouchers(): Result<List<Voucher>> {
        Log.d(TAG, "Fetching all vouchers")
        return try {
            val snapshot = firestore.collection(VOUCHERS_COLLECTION).get().await()

            Log.d(TAG, "Found ${snapshot.documents.size} vouchers")

            val vouchers = snapshot.documents.mapNotNull { document ->
                try {
                    val voucher = document.toObject(Voucher::class.java)?.apply {
                        id = document.id
                    }
                    Log.d(TAG, "Mapped voucher: $voucher")
                    voucher
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping voucher document ${document.id}: ${e.message}", e)
                    null
                }
            }

            Log.d(TAG, "Successfully fetched ${vouchers.size} vouchers")
            Result.success(vouchers)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching vouchers: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Claim a voucher for a user
    suspend fun claimVoucher(userId: String, voucherId: String): Result<Unit> {
        Log.d(TAG, "User $userId attempting to claim voucher $voucherId")
        return try {
            val userVouchersRef = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CLAIMED_VOUCHERS_COLLECTION)

            // Check if the voucher has already been claimed
            val querySnapshot = userVouchersRef.whereEqualTo("voucherId", voucherId).get().await()

            if (!querySnapshot.isEmpty) {
                Log.d(TAG, "Voucher $voucherId already claimed by user $userId")
                return Result.failure(Exception("Voucher already claimed"))
            }

            // Add the voucher to the claimed vouchers collection
            val claimedVoucherData = mapOf(
                "voucherId" to voucherId,
                "isAvailable" to true,
                "claimedAt" to System.currentTimeMillis()
            )

            userVouchersRef.add(claimedVoucherData).await()
            Log.d(TAG, "Successfully claimed voucher $voucherId for user $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error claiming voucher $voucherId for user $userId: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Fetch all claimed vouchers for a user
    suspend fun getUserVouchers(userId: String): Result<List<Voucher>> {
        Log.d(TAG, "Fetching claimed vouchers for user $userId")
        return try {
            val userVouchersRef = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(CLAIMED_VOUCHERS_COLLECTION).whereEqualTo("isAvailable", true)

            val snapshot = userVouchersRef.get().await()
            Log.d(TAG, "Found ${snapshot.documents.size} claimed vouchers for user $userId")

            val claimedVouchers = snapshot.documents.mapNotNull { document ->
                try {
                    val voucherId = document.getString("voucherId")
                    voucherId?.let {
                        val voucher = firestore.collection(VOUCHERS_COLLECTION).document(it).get().await()
                            .toObject(Voucher::class.java)?.apply { id = it }
                        Log.d(TAG, "Mapped claimed voucher: $voucher")
                        voucher
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping claimed voucher document ${document.id}: ${e.message}", e)
                    null
                }
            }

            Log.d(TAG, "Successfully fetched ${claimedVouchers.size} claimed vouchers for user $userId")
            Result.success(claimedVouchers)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching claimed vouchers for user $userId: ${e.message}", e)
            Result.failure(e)
        }
    }
}
