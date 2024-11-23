package com.example.splitpro.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Initial)
    val state: StateFlow<ProfileState> = _state

    fun saveName(name: String) {
        viewModelScope.launch {
            try {
                _state.value = ProfileState.Loading
                auth.currentUser?.let { user ->
                    val userRef = firestore.collection("Users").document(user.uid)
                    userRef.update("name", name).await()
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
                auth.currentUser?.let { user ->
                    val userRef = firestore.collection("Users").document(user.uid)
                    userRef.update("phoneNumber", phoneNumber).await()
                    _state.value = ProfileState.PhoneNumberSaved
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
