package com.bersamadapa.recylefood.data.repository

import android.location.Location
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.bersamadapa.recylefood.data.model.MysteryBox
import com.bersamadapa.recylefood.data.model.Product
import com.bersamadapa.recylefood.data.model.Restaurant
import com.bersamadapa.recylefood.data.repository.MysteryBoxRepository.Companion
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.math.BigDecimal
import java.math.RoundingMode

class RestaurantRepository(private val firestore: FirebaseFirestore) {


    companion object {
        private const val TAG = "RestaurantRepository"
        private const val RESTAURANTS_COLLECTION = "restaurants"
        private const val PRODUCTS_SUBCOLLECTION = "products"
    }

    // Fetch all restaurants with products
//    suspend fun getRestaurantsWithProducts(): Result<List<Restaurant>> = try {
//        val restaurantSnapshot = fetchCollection(RESTAURANTS_COLLECTION)
//
//        val restaurants = restaurantSnapshot?.documents?.mapNotNull { document ->
//            document.toObject(Restaurant::class.java)?.apply {
//                id = document.id // Assign the document ID
//                products = fetchSubCollection(document.id, PRODUCTS_SUBCOLLECTION) // Fetch products for each restaurant
//            }
//        }.orEmpty()
//
//        Result.success(restaurants)
//    } catch (e: Exception) {
//        Log.e(TAG, "Error fetching restaurants with products: ${e.message}", e)
//        Result.failure(e)
//    }

    suspend fun updateRestaurantRating(restaurantId: String, userRating: Float, selling: Int): Result<Unit> {
        return try {
            // Get a reference to the restaurant document
            val restaurantRef = firestore.collection("restaurants").document(restaurantId)

            // Fetch the current rating from the database
            val snapshot = restaurantRef.get().await()
            val currentRating = snapshot.getDouble("rating")?.toFloat() ?: 3f  // Default to 3 if no rating found

            // Calculate the new rating using the selling count to weight the rating
            val weightedRating = (currentRating * selling + userRating) / (selling + 1)

            // Round the weightedRating to one decimal place
            val roundedRating = Math.round(weightedRating * 10f) / 10f

            // Update the rating in Firestore
            restaurantRef.update("rating", roundedRating.toInt()).await()

            Log.d("TAG", "Successfully updated rating for restaurant $restaurantId to $roundedRating")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TAG", "Error updating restaurant rating: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Fetch restaurant details with products
    suspend fun getRestaurantDetailsWithProducts(idRestaurant: String): Result<Restaurant?> {
        return try {
            val restaurant = fetchDocument(RESTAURANTS_COLLECTION, idRestaurant)?.toObject(Restaurant::class.java)
            val products = fetchSubCollection(idRestaurant, PRODUCTS_SUBCOLLECTION)

            // Map the documents to Product objects
            val productList = products.mapNotNull { document ->
                val product = document.toObject(Product::class.java)
                // Log if the product mapping fails
                if (product == null) {
                    Log.w(TAG, "Product mapping failed for document: ${document.id}")
                }
                product
            }
            if (restaurant != null) {
                restaurant.products = productList
            }

            if (restaurant == null) {
                val message = "Restaurant not found: $idRestaurant"
                Log.w(TAG, message)
                return Result.failure(Exception(message))
            }

            val snapshot = firestore.collection("mysterybox").whereEqualTo("restaurant","/restaurants/$idRestaurant").get().await()
            Log.d("MysteryBox Resto", "Fetched ${snapshot.documents.size} mystery boxes from Firestore.")

            // Map each document to a MysteryBox object
// Map each document to a MysteryBox object
            val mysteryBoxes = snapshot.documents.mapNotNull { document ->
                document.toObject(MysteryBox::class.java)?.apply {
                    id = document.id
                    Log.d("MysteryBox Resto", "Mapped mystery box with ID: $id" + document.data.toString())

                    // Manually fetch the restaurant details if the restaurant ID/path is present
                    restaurantData = document.getString("restaurant")?.let { restaurantPath ->
                        val restaurantId = restaurantPath.split("/").last()
                        Log.d("MysteryBox Resto", "Fetching restaurant with ID: $restaurantId for mystery box: $id")

                        val restaurantDocument = firestore.collection("restaurants")
                            .document(restaurantId)
                            .get()
                            .await()
                        restaurantDocument.toObject(Restaurant::class.java)?.apply {
                            this@apply.id = restaurantDocument.id
                            Log.d("MysteryBox Resto", "Mapped restaurant with ID: $id for mystery box: $id")


                        }

                    }


                    // Fetch product details for the product IDs in the array under the specific restaurant
                    val productIds = document.get("products") as? List<*> // Ensure it's a list of product IDs
                    if (!productIds.isNullOrEmpty()) {
                        Log.d("MysteryBox Resto", "Fetching products for mystery box: $id, Product IDs: $productIds")
                        val productDocuments = productIds.mapNotNull { productId ->
                            val productIdString = productId.toString().split("/").last()


                            val productRef = firestore.collection("restaurants")
                                .document(restaurantData?.id ?: "")
                                .collection("products")
                                .document(productIdString)
                            productRef.get().await()
                        }

                        // Map the product documents to Product objects
                        productsData = productDocuments.mapNotNull { productDoc ->
                            productDoc.toObject(Product::class.java)?.apply {
                                Log.d("MysteryBox Resto", "Mapped product with ID: $id for mystery box: $id")
                            }
                        }
                    }
                }
            }

            restaurant.mysteryBox = mysteryBoxes


            Log.d("isi restaurant", restaurant.toString())

            Result.success(restaurant)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching restaurant details with products: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Fetch restaurants ordered by rating
    suspend fun getRestaurantsOrderByRating(): Result<List<Restaurant>> = try {
        val restaurantSnapshot = firestore.collection(RESTAURANTS_COLLECTION)
            .orderBy("rating", Query.Direction.DESCENDING)
            .get()
            .await()

        val restaurants = restaurantSnapshot.documents.mapNotNull { document ->
            document.toObject(Restaurant::class.java)?.apply { id = document.id
            }

        }

        Result.success(restaurants)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching restaurants ordered by rating: ${e.message}", e)
        Result.failure(e)
    }

    suspend fun getRestaurantsWithDistance(userLocation: Location): Result<List<Restaurant>> = try {
        // Fetch the restaurant data
        val restaurantSnapshot = firestore.collection(RESTAURANTS_COLLECTION).get().await()

        // Map the documents to Restaurant objects and calculate the distance from user
        val restaurants = restaurantSnapshot.documents.mapNotNull { document ->
            val restaurant = document.toObject(Restaurant::class.java)?.apply {
                id = document.id
                products = fetchSubCollection(id, PRODUCTS_SUBCOLLECTION).mapNotNull {
                    product -> product.toObject(Product::class.java)
                } // Fetch products for each restaurant

            }

            if (restaurant != null) {

                Log.d("User Location", "Lat: ${userLocation.latitude}, Lon: ${userLocation.longitude}")
                Log.d("Restaurant Location", "Lat: ${restaurant.location?.latitude}, Lon: ${restaurant.location?.longitude}")




                val distance = restaurant.location?.longitude?.let {
                    restaurant.location?.latitude?.let { it1 ->
                        calculateDistance(
                            userLocation.latitude,
                            userLocation.longitude,
                            it1,
                            it,)
                    }
                }
                Log.d("distance restaurant", distance.toString())

                restaurant.distance = distance
            }
            restaurant
        }.orEmpty()

        Result.success(restaurants)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching restaurants with distance: ${e.message}", e)
        Result.failure(e)
    }

    private fun calculateDistance(userLatitude: Double, userLongitude: Double, restaurantLatitude:Double, restaurantLongitude:Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(userLatitude, userLongitude, restaurantLatitude, restaurantLongitude, results)
        return results[0] // Distance in meters
    }

    // Utility function to fetch a collection
    private suspend fun fetchCollection(collectionName: String) = try {
        firestore.collection(collectionName).get().await()
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching collection $collectionName: ${e.message}", e)
        null
    }

    // Utility function to fetch a single document
    private suspend fun fetchDocument(collectionName: String, documentId: String) = try {
        firestore.collection(collectionName).document(documentId).get().await()
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching document $documentId in $collectionName: ${e.message}", e)
        null
    }

    // Utility function to fetch a sub-collection
    private suspend fun fetchSubCollection(idRestaurant: String, subCollection: String): List<DocumentSnapshot> {
        return try {
            val subCollectionRef = FirebaseFirestore.getInstance()
                .collection(RESTAURANTS_COLLECTION)
                .document(idRestaurant)
                .collection(subCollection)
            val querySnapshot = subCollectionRef.get().await()
            querySnapshot.documents
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching subcollection for restaurant: $idRestaurant", e)
            emptyList()
        }
    }

}
