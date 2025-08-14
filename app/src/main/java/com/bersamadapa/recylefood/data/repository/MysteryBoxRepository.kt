package com.bersamadapa.recylefood.data.repository

import android.location.Location
import android.util.Log
import com.bersamadapa.recylefood.data.model.MysteryBox
import com.bersamadapa.recylefood.data.model.Product
import com.bersamadapa.recylefood.data.model.Restaurant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MysteryBoxRepository(private val firestore: FirebaseFirestore) {

    companion object {
        private const val TAG = "MysteryBoxRepository"
        private const val MYSTERY_BOX_COLLECTION = "mysterybox"
        private const val RESTAURANTS_COLLECTION = "restaurants"
        private const val PRODUCTS_SUBCOLLECTION = "products"
    }

    // Fetch all mystery boxes
    suspend fun getAllMysteryBoxes(userLocation: Location): Result<List<MysteryBox>> {
        Log.d(TAG, "Fetching all mystery boxes...")
        return try {
            // Fetch all mystery boxes from the Firestore collection
            val snapshot = firestore.collection(MYSTERY_BOX_COLLECTION).get().await()
            Log.d(TAG, "Fetched ${snapshot.documents.size} mystery boxes from Firestore.")

            // Map each document to a MysteryBox object
// Map each document to a MysteryBox object
            val mysteryBoxes = snapshot.documents.mapNotNull { document ->
                document.toObject(MysteryBox::class.java)?.apply {
                    id = document.id
                    Log.d(TAG, "Mapped mystery box with ID: $id" + document.data.toString())

                    // Manually fetch the restaurant details if the restaurant ID/path is present
                    restaurantData = document.getString("restaurant")?.let { restaurantPath ->
                        val restaurantId = restaurantPath.split("/").last()
                        Log.d(TAG, "Fetching restaurant with ID: $restaurantId for mystery box: $id")

                        val restaurantDocument = firestore.collection(RESTAURANTS_COLLECTION)
                            .document(restaurantId)
                            .get()
                            .await()
                        restaurantDocument.toObject(Restaurant::class.java)?.apply {
                            this@apply.id = restaurantDocument.id
                            Log.d(TAG, "Mapped restaurant with ID: $id for mystery box: $id")


                        }

                    }


                    Log.d("User Location", "Lat: ${userLocation.latitude}, Lon: ${userLocation.longitude}")
                    Log.d("Restaurant Location", "Lat: ${restaurantData?.location?.latitude}, Lon: ${restaurantData?.location?.longitude}")




                    val distance = restaurantData?.location?.longitude?.let {
                        restaurantData?.location?.latitude?.let { it1 ->
                            calculateDistance(
                                userLocation.latitude,
                                userLocation.longitude,
                                it1,
                                it,)
                        }
                    }
                    Log.d("distance restaurant", distance.toString())

                    restaurantData?.distance = distance

                    // Fetch product details for the product IDs in the array under the specific restaurant
                    val productIds = document.get("products") as? List<*> // Ensure it's a list of product IDs
                    if (!productIds.isNullOrEmpty()) {
                        Log.d(TAG, "Fetching products for mystery box: $id, Product IDs: $productIds")
                        val productDocuments = productIds.mapNotNull { productId ->
                            val productIdString = productId.toString().split("/").last()


                            val productRef = firestore.collection(RESTAURANTS_COLLECTION)
                                .document(restaurantData?.id ?: "")
                                .collection(PRODUCTS_SUBCOLLECTION)
                                .document(productIdString)
                            productRef.get().await()
                        }

                        // Map the product documents to Product objects
                        productsData = productDocuments.mapNotNull { productDoc ->
                            productDoc.toObject(Product::class.java)?.apply {
                                Log.d(TAG, "Mapped product with ID: $id for mystery box: $id")
                            }
                        }
                    }
                }
            }

            Log.d(TAG, "Successfully fetched and mapped all mystery boxes.")
            Result.success(mysteryBoxes)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching mystery boxes: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Fetch mystery box details, including restaurant and products
    suspend fun getMysteryBoxDetails(mysteryBoxId: String, userLocation: Location): Result<MysteryBox?> {
        Log.d(TAG, "Fetching mystery box details for ID: $mysteryBoxId")
        return try {
            val document = firestore.collection(MYSTERY_BOX_COLLECTION)
                .document(mysteryBoxId)
                .get()
                .await()



            if (!document.exists()) {
                Log.e(TAG, "Mystery box with ID $mysteryBoxId does not exist")
                return Result.failure(Exception("Mystery box not found"))
            }

            val mysteryBox = document.toObject(MysteryBox::class.java)?.apply {
                id = document.id
                Log.d(TAG, "Mapped mystery box details with ID: $id")

                // Fetch restaurant details
                restaurantData = document.getString("restaurant")?.let { restaurantPath ->
                    val restaurantId = restaurantPath.split("/").last()
                    Log.d(TAG, "Fetching restaurant with ID: $restaurantId for mystery box: $id")

                    val restaurantDocument = firestore.collection(RESTAURANTS_COLLECTION)
                        .document(restaurantId)
                        .get()
                        .await()

                    if (!restaurantDocument.exists()) {
                        Log.e(TAG, "Restaurant with ID $restaurantId not found")
                        null
                    } else {
                        restaurantDocument.toObject(Restaurant::class.java)?.apply {
                            this@apply.id = restaurantDocument.id
                            Log.d(TAG, "Mapped restaurant with ID: $id for mystery box: $id")
                        }
                    }
                }

                if (restaurantData == null) {
                    Log.e(TAG, "Failed to fetch restaurant data for mystery box ID: $id")
                }

                // Distance calculation
                val distance = restaurantData?.location?.latitude?.let { latitude ->
                    restaurantData?.location?.longitude?.let { longitude ->
                        calculateDistance(userLocation.latitude, userLocation.longitude, latitude, longitude)
                    }
                }

                restaurantData?.distance = distance
                Log.d(TAG, "Calculated distance: $distance")

                // Fetch product details
                val productIds = document.get("products") as? List<*>
                if (!productIds.isNullOrEmpty()) {
                    Log.d(TAG, "Fetching products for mystery box: $id, Product IDs: $productIds")
                    val productDocuments = productIds.mapNotNull { productId ->
                        val productIdString = productId.toString().split("/").last()
                        firestore.collection(RESTAURANTS_COLLECTION)
                            .document(restaurantData?.id ?: "")
                            .collection(PRODUCTS_SUBCOLLECTION)
                            .document(productIdString)
                            .get()
                            .await()
                    }

                    productsData = productDocuments.mapNotNull { productDoc ->
                        productDoc.toObject(Product::class.java)?.apply {
                            Log.d(TAG, "Mapped product with ID: $id for mystery box: $id")
                        }
                    }
                } else {
                    Log.d(TAG, "No products found for mystery box: $id")
                }
            }

            Log.d(TAG, "Successfully fetched mystery box details for ID: $mysteryBoxId")
            Result.success(mysteryBox)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching mystery box details: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun calculateDistance(userLatitude: Double, userLongitude: Double, restaurantLatitude:Double, restaurantLongitude:Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(userLatitude, userLongitude, restaurantLatitude, restaurantLongitude, results)
        return results[0] // Distance in meters
    }
}
