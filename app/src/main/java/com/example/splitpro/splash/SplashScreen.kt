package com.example.splitpro.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        
        if (auth.currentUser != null) {
            try {
                // Check if user document exists in Firestore
                val userDoc = firestore.collection("Users")
                    .document(auth.currentUser!!.uid)
                    .get()
                    .await()

                if (userDoc.exists()) {
                    onNavigateToMain()
                } else {
                    // If somehow user auth exists but no Firestore document,
                    // sign out and go to login
                    auth.signOut()
                    onNavigateToLogin()
                }
            } catch (e: Exception) {
                // Handle error case
                auth.signOut()
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
