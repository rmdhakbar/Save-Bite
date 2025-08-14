package com.bersamadapa.recylefood.data.model

import com.google.firebase.Timestamp

data class Order(
    var id: String? = null,
    var totalAmount: Int? = null,
    var createdAt: Timestamp? = null,
    var mysteryBoxs: List<String>? = null,
    var mysteryBoxsData: List<MysteryBox>? = null,
    var userId: String? = null,
    var statusOrder: OrderStatus? = OrderStatus.Pending, // Default value
    var status: String? = null,
    var tokenMidtrans: String? = null,
    var category: String? = null,
    var categoryOrder: CategoryOrder = CategoryOrder.Personal,
    var receiverPhoneNumber: String? = null,  // New field for receiver's phone number
    var receiverAddress: String? = null,
    var proveDeliveryDonasi: PictureData?=null
)

enum class CategoryOrder{
    Donation,
    Personal
}


enum class OrderStatus {
    Pending,
    OnGoing,
    Done
}