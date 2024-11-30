package com.example.splitpro.auth

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    data class SignedIn(val isNewUser: Boolean, val userId: String) : AuthState()
}
