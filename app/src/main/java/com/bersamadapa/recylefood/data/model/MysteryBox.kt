package com.bersamadapa.recylefood.data.model

import com.google.firebase.Timestamp
import java.io.Serializable


data class MysteryBox (
    var id: String = "", // Document ID
    val name:String? = null,
    val price:Int? = null,
    var restaurant: String? = null,
    var restaurantData: Restaurant? = null,
    var products: List<String>? = null,
    var productsData: List<Product>? = null,
    var createdAt: Timestamp? = null
): Serializable

