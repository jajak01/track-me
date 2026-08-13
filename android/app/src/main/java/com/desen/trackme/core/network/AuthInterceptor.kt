package com.desen.trackme.core.network

import com.desen.trackme.core.session.TokenStore
import okhttp3.Interceptor
import okhttp3.Response

/** Attaches the current Bearer access token to every outgoing request. */
class AuthInterceptor(
    private val tokenStore: TokenStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStore.accessToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
