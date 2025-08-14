package com.bersamadapa.recylefood.data.model

import java.io.Serializable

data class Voucher(
    var id : String?="null",
    val name: String? = "null",
    val discount: Int?= 0,
    val maximumDiscount: Double?=0.0,
    val minimumPayment: Double?=0.0
): Serializable