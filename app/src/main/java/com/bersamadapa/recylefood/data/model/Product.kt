package com.bersamadapa.recylefood.data.model

data class Product(
    val name: String = "",
    val description: String = "",
    val expiredTime: Int = 0,
    val price: Int = 0,
    val productPicture: PictureData? = null
)
