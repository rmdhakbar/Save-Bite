package com.bersamadapa.recylefood.data.model



data class User(
    var profilePicture: PictureData? = null, // Add default value
    var username: String? = "", // Add default value
    var noHandphone: String? = "", // Add default value
    var password: String? = "", // Add default value
    var email: String? = "", // Add default value
    var points:Int? = 0, // Add default value
)

data class PictureData(
    var key: String = "", // Add default value
    var alt: String = "", // Add default value
    var url: String = "" // Add default value
)
