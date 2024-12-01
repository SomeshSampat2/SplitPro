package com.example.splitpro.CreateProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitpro.firebase.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateProfileViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager.getInstance()
    
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Initial)
    val state: StateFlow<ProfileState> = _state

    fun saveName(name: String) {
        viewModelScope.launch {
            try {
                _state.value = ProfileState.Loading
                firebaseManager.currentUser?.let { user ->
                    firebaseManager.updateUserName(user.uid, name)
                    _state.value = ProfileState.NameSaved
                }
            } catch (e: Exception) {
                _state.value = ProfileState.Error("Failed to save name: ${e.message}")
            }
        }
    }

    fun savePhoneNumber(phoneNumber: String?) {
        viewModelScope.launch {
            try {
                _state.value = ProfileState.Loading
                firebaseManager.currentUser?.let { user ->
                    if (phoneNumber != null) {
                        firebaseManager.updateUserPhoneNumber(user.uid, phoneNumber)
                        _state.value = ProfileState.PhoneNumberSaved
                    }
                }
            } catch (e: Exception) {
                _state.value = ProfileState.Error("Failed to save phone number: ${e.message}")
            }
        }
    }

    fun resetState() {
        _state.value = ProfileState.Initial
    }
}

sealed class ProfileState {
    object Initial : ProfileState()
    object Loading : ProfileState()
    object NameSaved : ProfileState()
    object PhoneNumberSaved : ProfileState()
    data class Error(val message: String) : ProfileState()
}
