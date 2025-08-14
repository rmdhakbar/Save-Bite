package com.bersamadapa.recylefood.data.model

import com.google.firebase.firestore.GeoPoint


data class Restaurant(
    var id: String = "", // Document ID
    var name: String = "",
    var location: GeoPoint? = null, // Firestore GeoPoint
    var profilePicture:  PictureData? = null,
    var adminRestaurant: String? = "",
    var createdAt: com.google.firebase.Timestamp? = null, // Firestore Timestamp
    var rating: Double? = 0.0,
    var address: String = "",
    var products: List<Product>? = emptyList(), // Products will be populated dynamically
    var distance: Float? = null,
    var mysteryBox: List<MysteryBox>?= null,
    var selling: Int? = 0,
)
