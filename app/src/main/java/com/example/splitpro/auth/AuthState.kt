package com.example.splitpro.auth

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
    data class SignedIn(val shouldNavigateToProfile: Boolean = false) : AuthState()
}
