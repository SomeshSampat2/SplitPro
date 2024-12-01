package com.example.splitpro.firebase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.splitpro.firebase.FirebaseConstants.COLLECTION_USERS
import com.example.splitpro.firebase.FirebaseConstants.UserFields
import com.example.splitpro.firebase.FirebaseConstants.COLLECTION_GROUPS
import com.example.splitpro.firebase.FirebaseConstants.GroupFields
import java.util.Date

class FirebaseManager private constructor() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Current User
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    companion object {
        @Volatile
        private var instance: FirebaseManager? = null

        fun getInstance(): FirebaseManager {
            return instance ?: synchronized(this) {
                instance ?: FirebaseManager().also { instance = it }
            }
        }
    }

    // Authentication Methods
    suspend fun signInWithGoogle(account: GoogleSignInAccount): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val authResult = auth.signInWithCredential(credential).await()
        return authResult.user ?: throw Exception(FirebaseConstants.ErrorMessages.SIGN_IN_FAILED)
    }

    fun signOut() {
        auth.signOut()
    }

    // User Methods
    suspend fun createUserDocument(user: FirebaseUser) {
        val userData = hashMapOf(
            UserFields.EMAIL to user.email,
            UserFields.CREATED_AT to Date(),
            UserFields.UPDATED_AT to Date()
        )
        firestore.collection(COLLECTION_USERS)
            .document(user.uid)
            .set(userData)
            .await()
    }

    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>) {
        val updatedData = HashMap(updates)
        updatedData[UserFields.UPDATED_AT] = Date()
        
        firestore.collection(COLLECTION_USERS)
            .document(userId)
            .update(updatedData)
            .await()
    }

    suspend fun getUserDocument(userId: String) = 
        firestore.collection(COLLECTION_USERS)
            .document(userId)
            .get()
            .await()

    suspend fun checkUserExists(userId: String): Boolean =
        getUserDocument(userId).exists()

    suspend fun updateUserName(userId: String, name: String) {
        updateUserProfile(userId, mapOf(UserFields.NAME to name))
    }

    suspend fun updateUserPhoneNumber(userId: String, phoneNumber: String) {
        updateUserProfile(userId, mapOf(UserFields.PHONE_NUMBER to phoneNumber))
    }

    suspend fun getUserData(userId: String): Map<String, Any>? {
        val document = getUserDocument(userId)
        return if (document.exists()) {
            document.data
        } else {
            null
        }
    }

    suspend fun getCurrentUserData(): Map<String, Any>? {
        return currentUser?.let { user ->
            getUserData(user.uid)
        }
    }

    // Group Methods
    suspend fun createGroup(groupName: String, groupType: String): String {
        val currentUserId = currentUser?.uid ?: throw Exception("User not authenticated")
        
        val groupData = hashMapOf(
            GroupFields.NAME to groupName,
            GroupFields.TYPE to groupType,
            GroupFields.CREATED_BY to currentUserId,
            GroupFields.CREATED_AT to Date(),
            GroupFields.UPDATED_AT to Date(),
            GroupFields.MEMBERS to listOf(currentUserId)
        )

        val groupRef = firestore.collection(COLLECTION_GROUPS)
            .add(groupData)
            .await()

        return groupRef.id
    }

    suspend fun getGroupDetails(groupId: String): Map<String, Any>? {
        return try {
            firestore.collection(COLLECTION_GROUPS)
                .document(groupId)
                .get()
                .await()
                .data
        } catch (e: Exception) {
            null
        }
    }
}
