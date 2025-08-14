package com.bersamadapa.recylefood.data.repository


import android.util.Log
import com.bersamadapa.recylefood.data.model.CategoryOrder
import com.bersamadapa.recylefood.data.model.MysteryBox
import com.bersamadapa.recylefood.data.model.Order
import com.bersamadapa.recylefood.data.model.OrderRequest
import com.bersamadapa.recylefood.data.model.OrderResponse
import com.bersamadapa.recylefood.data.model.OrderStatus
import com.bersamadapa.recylefood.data.model.Restaurant
import com.bersamadapa.recylefood.network.api.ApiService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class OrderRepository(private val firestoreInstance: FirebaseFirestore, private val apiService: ApiService) {


    companion object {
        private const val TAG = "OrderRepository"
        private const val ORDERS_COLLECTION = "orders"
        private const val MYSTERY_BOX_COLLECTION = "mysterybox"
    }

    // Fetch all orders
    suspend fun getAllOrders(userId: String, statusFilter: OrderStatus? = null): Result<List<Order>> {
        Log.d(TAG, "Fetching all orders for user: $userId with status: $statusFilter")
        return try {
            val query = firestoreInstance.collection(ORDERS_COLLECTION)
                .whereEqualTo("userId", userId)

            // Apply status filter if provided
            val snapshot = if (statusFilter != null) {
                query.whereEqualTo("status", statusFilter.name).get().await()
            } else {
                query.get().await()
            }

            Log.d(TAG, "Fetched ${snapshot.documents.size} orders from Firestore.")

            val orders = snapshot.documents.mapNotNull { document ->
                document.toObject(Order::class.java)?.apply {
                    id = document.id
                    Log.d(TAG, "Mapped order with ID: ${document.id}")

                    statusOrder = when (status) {
                        "Pending" -> OrderStatus.Pending
                        "OnGoing" -> OrderStatus.OnGoing
                        "Done" -> OrderStatus.Done
                        else -> OrderStatus.Pending
                    }

                    categoryOrder = when (category) {
                        "Donation" -> CategoryOrder.Donation
                        "Personal" -> CategoryOrder.Personal
                        else -> CategoryOrder.Personal
                    }

                    // If the category is "Donation", fetch receiver info
                    if (category == "Donation") {
                        // Add logic to fetch receiver details here
                        receiverPhoneNumber = document.getString("phoneNumberReceiver")
                        receiverAddress = document.getString("addressReceiver")
                    }

                    // Fetch and map mystery box data for each order
                    mysteryBoxsData = mysteryBoxs?.mapNotNull { mysteryBoxId ->
                        val mysteryBoxDocument = firestoreInstance.collection(MYSTERY_BOX_COLLECTION)
                            .document(mysteryBoxId)
                            .get()
                            .await()
                        mysteryBoxDocument.toObject(MysteryBox::class.java)?.apply {
                            id = mysteryBoxDocument.id

                            // Fetch restaurant details
                            restaurantData = mysteryBoxDocument.getString("restaurant")?.let { restaurantPath ->
                                val restaurantId = restaurantPath.split("/").last()
                                firestoreInstance.collection("restaurants")
                                    .document(restaurantId)
                                    .get()
                                    .await()
                                    .toObject(Restaurant::class.java)?.apply {
                                        this@apply.id = restaurantId
                                    }
                            }
                        }
                    }
                }
            }

            Log.d(TAG, "Successfully fetched and mapped orders.")
            Result.success(orders)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching orders: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun createOrder(
        userId: String,
        orderRequest: OrderRequest
    ): Result<OrderResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Convert order data into RequestBody or use Multipart for files if needed

                // Make the API call to create an order using Retrofit's suspend function
                val response = apiService.createOrder(
                    userId = userId,
                    orderRequest = orderRequest
                )

                // Assuming `ApiResponse<Order>` is the return type and it includes a `statusCode` and `data`
                if (response.statusCode == 201) {
                    Log.d("OrderRepository", "Order created successfully: ${response.data}")
                    Result.success(response.data)
                } else {
                    Log.e("OrderRepository", "Failed to create order: ${response.message}")
                    Result.failure(Exception("Failed to create order: ${response.message}"))
                }
            } catch (e: Exception) {
                Log.e("OrderRepository", "Error creating order", e)
                Result.failure(e)
            }
        }
    }


    // Fetch a single order by ID
    suspend fun getOrderById(orderId: String): Result<Order> {
        Log.d(TAG, "Fetching order with ID: $orderId")
        return try {
            val document = firestoreInstance.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .await()

            if (document.exists()) {
                val order = document.toObject(Order::class.java)?.apply {
                    id = document.id
                    Log.d(TAG, "Mapped order with ID: ${document.id}")

                    statusOrder = when (status) {
                        "Pending" -> OrderStatus.Pending
                        "OnGoing" -> OrderStatus.OnGoing
                        "Done" -> OrderStatus.Done
                        else -> OrderStatus.Pending
                    }

                    categoryOrder = when (category) {
                        "Donation" -> CategoryOrder.Donation
                        "Personal" -> CategoryOrder.Personal
                        else -> CategoryOrder.Personal
                    }

                    // Fetch receiver details if category is "Donation"
                    if (category == "Donation") {
                        receiverPhoneNumber = document.getString("phoneNumberReceiver")
                        receiverAddress = document.getString("addressReceiver")
                    }

                    // Fetch mystery box data for the order
                    mysteryBoxsData = mysteryBoxs?.mapNotNull { mysteryBoxId ->
                        val mysteryBoxDocument = firestoreInstance.collection(MYSTERY_BOX_COLLECTION)
                            .document(mysteryBoxId)
                            .get()
                            .await()
                        mysteryBoxDocument.toObject(MysteryBox::class.java)?.apply {
                            id = mysteryBoxDocument.id

                            // Fetch restaurant details
                            restaurantData = mysteryBoxDocument.getString("restaurant")?.let { restaurantPath ->
                                val restaurantId = restaurantPath.split("/").last()
                                firestoreInstance.collection("restaurants")
                                    .document(restaurantId)
                                    .get()
                                    .await()
                                    .toObject(Restaurant::class.java)?.apply {
                                        this@apply.id = restaurantId
                                    }
                            }
                        }
                    }
                }

                Log.d(TAG, "Successfully fetched order: $orderId")
                Result.success(order!!)
            } else {
                Log.e(TAG, "Order not found: $orderId")
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching order: ${e.message}", e)
            Result.failure(e)
        }
    }


}
