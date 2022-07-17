package com.frost.neuroquest.model


data class Places(
    val id : Int,
    val nombre : String,
    val latitude : Double,
    val longitude : Double,
    val image_url: String,
    val url: String
)
