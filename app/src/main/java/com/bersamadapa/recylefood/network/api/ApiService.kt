package com.bersamadapa.recylefood.network.api

import com.bersamadapa.recylefood.data.model.OrderRequest
import com.bersamadapa.recylefood.data.model.OrderResponse
import com.bersamadapa.recylefood.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @Multipart
    @PATCH("user/{id_user}")
    suspend fun updateUser(
        @Path("id_user") userId: String,
        @Part("username") username: RequestBody,
        @Part profile_picture: MultipartBody.Part?
    ): ApiResponse<User>

    @POST("order/{id_user}/pay")
    suspend fun createOrder(
        @Path("id_user") userId: String,
        @Body orderRequest: OrderRequest
    ):ApiResponse<OrderResponse>
}
