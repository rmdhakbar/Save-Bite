package com.bersamadapa.recylefood.data.model

data class OrderRequest (
    val mysteryBox : List<String>,
    val category : String,
    val addressReceiver : String? ,
    val phoneNumberReceiver : String?,
    val voucherId: String?
)


data class OrderResponse(
    val id:String,
    val token_midtrans: Midtrans
)

data class Midtrans(
    val token:String,
    val redirect_url: String
)

