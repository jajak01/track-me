package com.desen.trackme.core.session

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String,
    val displayName: String
)
