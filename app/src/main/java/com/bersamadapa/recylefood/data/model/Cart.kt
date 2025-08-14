    package com.bersamadapa.recylefood.data.model

    data class CartItem(
        var id: String? = null,
        val quantity: Int = 0,
        var restaurant: Restaurant? = null,
        var mysteryBox: List<String>? = null,
        var mysteryBoxData: List<MysteryBox>? = null
    )

    data class CartRequest(
        val quantity: Int = 0,
        val restaurantId: String = "",
        val mysteryBox: List<String>? = null

    )