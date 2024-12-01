package com.example.splitpro.screens

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitpro.firebase.FirebaseManager
import com.example.splitpro.firebase.FirebaseConstants.UserFields
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profileColor: Color = generateRandomColor(),
    val totalToPay: Double = 1250.50,
    val totalToReceive: Double = 2500.75,
    val showSignOutMessage: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager.getInstance()
    
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                
                val userData = firebaseManager.getCurrentUserData()
                if (userData != null) {
                    _state.value = _state.value.copy(
                        name = (userData[UserFields.NAME] as? String) ?: "",
                        email = (userData[UserFields.EMAIL] as? String) ?: "",
                        phoneNumber = (userData[UserFields.PHONE_NUMBER] as? String) ?: "",
                        isLoading = false,
                        error = null
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Failed to load user data"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun signOut() {
        firebaseManager.signOut()
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
    return colors.random()
}
