package com.tamboo.profile.domain.model

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val address: UserAddress
)
