package com.example.splitpro.screens

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

data class ProfileState(
    val name: String = "John Doe",
    val email: String = "john.doe@example.com",
    val phoneNumber: String = "+91 9876543210",
    val profileColor: Color = generateRandomColor(),
    val totalToPay: Double = 1250.50,
    val totalToReceive: Double = 2500.75,
    val showSignOutMessage: Boolean = false
)

class ProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state
    private val auth = FirebaseAuth.getInstance()

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _state.value = _state.value.copy(phoneNumber = phoneNumber)
    }

    fun signOut() {
        auth.signOut()
        _state.value = _state.value.copy(showSignOutMessage = true)
    }

    fun signOutMessageShown() {
        _state.value = _state.value.copy(showSignOutMessage = false)
    }
}

private fun generateRandomColor(): Color {
    val colors = listOf(
        Color(0xFF1E88E5), // Blue
        Color(0xFF43A047), // Green
        Color(0xFFE53935), // Red
        Color(0xFF8E24AA), // Purple
        Color(0xFFF4511E), // Deep Orange
        Color(0xFF00897B), // Teal
        Color(0xFF3949AB), // Indigo
        Color(0xFF7CB342), // Light Green
        Color(0xFFC0CA33), // Lime
        Color(0xFFFFB300)  // Amber
    )
    return colors[Random.nextInt(colors.size)]
}
