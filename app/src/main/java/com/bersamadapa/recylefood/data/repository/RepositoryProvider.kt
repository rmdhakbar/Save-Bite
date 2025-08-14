package com.bersamadapa.recylefood.data.repository

import androidx.compose.ui.platform.LocalContext
import com.bersamadapa.recylefood.network.api.ApiClient
import com.bersamadapa.recylefood.network.api.ApiService
import com.bersamadapa.recylefood.utils.LocationHelper
import com.google.firebase.firestore.FirebaseFirestore

object RepositoryProvider {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val apiService: ApiService by lazy {
        ApiClient.createService(ApiService::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(firestoreInstance)
    }

    val restaurantRepository: RestaurantRepository by lazy {
        RestaurantRepository(firestoreInstance)
    }

    val mysteryBoxRepository: MysteryBoxRepository by lazy {
        MysteryBoxRepository(firestoreInstance)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(firestoreInstance, apiService)
    }


    val orderRepository: OrderRepository by lazy {
        OrderRepository(firestoreInstance, apiService)
    }

    val voucherRepository: VoucherRepository by lazy {
        VoucherRepository(firestoreInstance)
    }

    val cartRepository:CartRepository by lazy {
        CartRepository(firestoreInstance)

    }
}
