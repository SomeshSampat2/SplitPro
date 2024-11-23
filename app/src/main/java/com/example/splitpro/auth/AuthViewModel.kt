package com.example.splitpro.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _state = MutableStateFlow<AuthState>(AuthState.Initial)
    val state: StateFlow<AuthState> = _state

    init {
        // Check if user is already signed in
        auth.currentUser?.let { user ->
            _state.value = AuthState.Success(user.uid)
        }
    }

    fun handleSignInResult(context: Context, task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            try {
                _state.value = AuthState.Loading
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                
                // Sign in with Firebase
                val authResult = auth.signInWithCredential(credential).await()
                
                // Create user in Firestore if it's a new sign-in
                authResult.user?.let { firebaseUser ->
                    val userRef = firestore.collection("Users").document(firebaseUser.uid)
                    val userSnapshot = userRef.get().await()
                    
                    if (!userSnapshot.exists()) {
                        // Create new user document
                        val userData = hashMapOf(
                            "email" to firebaseUser.email
                        )
                        userRef.set(userData).await()
                        // Navigate to profile creation for new users
                        _state.value = AuthState.SignedIn(shouldNavigateToProfile = true)
                    } else {
                        // Existing user, just update state
                        _state.value = AuthState.Success(firebaseUser.uid)
                    }
                    
                    Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
                }
                
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Sign in failed")
                Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _state.value = AuthState.Initial
    }
}
