package com.example.splitpro.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.splitpro.firebase.FirebaseManager

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    LaunchedEffect(Unit) {
        val firebaseManager = FirebaseManager.getInstance()
        
        if (firebaseManager.currentUser != null) {
            try {
                // Check if user document exists in Firestore
                val exists = firebaseManager.checkUserExists(firebaseManager.currentUser!!.uid)

                if (exists) {
                    onNavigateToMain()
                } else {
                    // If somehow user auth exists but no Firestore document,
                    // sign out and go to login
                    firebaseManager.signOut()
                    onNavigateToLogin()
                }
            } catch (e: Exception) {
                // Handle error case
                firebaseManager.signOut()
                onNavigateToLogin()
            }
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
