package com.example.splitpro.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.example.splitpro.firebase.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager.getInstance()
    
    private val _state = MutableStateFlow<AuthState>(AuthState.Initial)
    val state: StateFlow<AuthState> = _state

    init {
        // Check if user is already signed in
        firebaseManager.currentUser?.let { user ->
            viewModelScope.launch {
                try {
                    val isNewUser = !firebaseManager.checkUserExists(user.uid)
                    _state.value = AuthState.SignedIn(isNewUser = isNewUser, userId = user.uid)
                } catch (e: Exception) {
                    _state.value = AuthState.Error(e.message ?: "Failed to check user status")
                }
            }
        }
    }

    fun handleSignInResult(context: Context, task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            try {
                _state.value = AuthState.Loading
                val account = task.getResult(ApiException::class.java)
                
                // Sign in with Firebase
                val firebaseUser = firebaseManager.signInWithGoogle(account)
                
                // Check if user exists in Firestore
                val isNewUser = !firebaseManager.checkUserExists(firebaseUser.uid)
                
                if (isNewUser) {
                    // Create new user document
                    firebaseManager.createUserDocument(firebaseUser)
                }
                
                _state.value = AuthState.SignedIn(isNewUser = isNewUser, userId = firebaseUser.uid)
                Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
                
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Sign in failed")
                Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signOut() {
        firebaseManager.signOut()
        _state.value = AuthState.Initial
    }
}
