package com.example.splitpro.firebase

import com.example.splitpro.data.models.GroupMember
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirebaseManager private constructor() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Current User
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_GROUPS = "groups"
        
        private object UserFields {
            const val NAME = "name"
            const val EMAIL = "email"
            const val PHONE_NUMBER = "phoneNumber"
            const val PROFILE_PICTURE = "profilePicture"
            const val CREATED_AT = "createdAt"
            const val UPDATED_AT = "updatedAt"
        }
        
        private object GroupFields {
            const val NAME = "name"
            const val TYPE = "type"
            const val CREATED_BY = "createdBy"
            const val CREATED_AT = "createdAt"
            const val UPDATED_AT = "updatedAt"
            const val MEMBERS = "members"
            const val MEMBER_USER_ID = "userId"
            const val MEMBER_NAME = "name"
            const val MEMBER_BALANCE = "balance"
            const val MEMBER_IS_REGISTERED = "isRegistered"
            const val MEMBER_UNREGISTERED_NAME = "unregisteredName"
            const val MEMBER_ADDED_BY = "addedBy"
        }
        
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
        return authResult.user ?: throw Exception("SIGN_IN_FAILED")
    }

    fun signOut() {
        auth.signOut()
    }

    // User Methods
    suspend fun createUserDocument(user: FirebaseUser) {
        val userData = hashMapOf(
            UserFields.NAME to user.displayName,
            UserFields.EMAIL to user.email,
            UserFields.PHONE_NUMBER to user.phoneNumber,
            UserFields.PROFILE_PICTURE to user.photoUrl?.toString(),
            UserFields.CREATED_AT to Date(),
            UserFields.UPDATED_AT to Date()
        )

        firestore.collection(COLLECTION_USERS)
            .document(user.uid)
            .set(userData)
            .await()
    }

    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>) {
        firestore.collection(COLLECTION_USERS)
            .document(userId)
            .update(updates)
            .await()
    }

    suspend fun getUserDocument(userId: String) = 
        firestore.collection(COLLECTION_USERS)
            .document(userId)
            .get()
            .await()

    suspend fun checkUserExists(userId: String): Boolean {
        return getUserDocument(userId).exists()
    }

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

    suspend fun findUserByPhoneNumber(phoneNumber: String): String? {
        val users = firestore.collection(COLLECTION_USERS)
            .whereEqualTo(UserFields.PHONE_NUMBER, phoneNumber)
            .get()
            .await()
        
        return if (!users.isEmpty) {
            users.documents.first().id
        } else {
            null
        }
    }

    // Group Methods
    suspend fun getUserGroups(): List<Map<String, Any>> {
        val currentUserId = currentUser?.uid ?: throw Exception("User not logged in")
        println("DEBUG: Fetching groups for user: $currentUserId")
        
        val querySnapshot = firestore.collection(COLLECTION_GROUPS)
            .get()
            .await()
            
        println("DEBUG: Found ${querySnapshot.documents.size} total groups")
        
        return querySnapshot.documents.mapNotNull { doc ->
            val data = doc.data
            println("DEBUG: Processing group ${doc.id}, data: $data")
            
            if (data != null) {
                @Suppress("UNCHECKED_CAST")
                val members = data[GroupFields.MEMBERS] as? List<Map<String, Any>> ?: listOf()
                println("DEBUG: Group ${doc.id} has ${members.size} members")
                
                if (members.any { member -> 
                    val userId = member[GroupFields.MEMBER_USER_ID] as? String
                    println("DEBUG: Checking member userId: $userId against currentUserId: $currentUserId")
                    userId == currentUserId 
                }) {
                    println("DEBUG: User is member of group ${doc.id}")
                    data.plus(mapOf("id" to doc.id))
                } else {
                    println("DEBUG: User is not member of group ${doc.id}")
                    null
                }
            } else {
                println("DEBUG: No data for group ${doc.id}")
                null
            }
        }.also { 
            println("DEBUG: Returning ${it.size} groups for user")
        }
    }

    suspend fun createGroup(groupName: String, groupType: String): String {
        val currentUserId = currentUser?.uid ?: throw Exception("User not logged in")
        
        val groupData = hashMapOf(
            GroupFields.NAME to groupName,
            GroupFields.TYPE to groupType,
            GroupFields.CREATED_BY to currentUserId,
            GroupFields.CREATED_AT to Date(),
            GroupFields.UPDATED_AT to Date(),
            GroupFields.MEMBERS to listOf(
                hashMapOf(
                    GroupFields.MEMBER_USER_ID to currentUserId,
                    GroupFields.MEMBER_NAME to (currentUser?.displayName ?: ""),
                    GroupFields.MEMBER_BALANCE to 0.0,
                    GroupFields.MEMBER_IS_REGISTERED to true,
                    GroupFields.MEMBER_UNREGISTERED_NAME to "",
                    GroupFields.MEMBER_ADDED_BY to currentUserId
                )
            )
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

    suspend fun addMembersToGroup(
        groupId: String,
        members: List<GroupMember>
    ) {
        val groupRef = firestore.collection(COLLECTION_GROUPS).document(groupId)
        val group = groupRef.get().await()
        
        if (group.exists()) {
            val currentMembers = try {
                @Suppress("UNCHECKED_CAST")
                group.get(GroupFields.MEMBERS) as? List<Map<String, Any>> ?: listOf()
            } catch (e: Exception) {
                listOf<Map<String, Any>>()
            }
            
            val updatedMembers = currentMembers.toMutableList()
            
            members.forEach { newMember ->
                if (!currentMembers.any { it[GroupFields.MEMBER_USER_ID] == newMember.userId }) {
                    updatedMembers.add(
                        hashMapOf(
                            GroupFields.MEMBER_USER_ID to newMember.userId,
                            GroupFields.MEMBER_NAME to newMember.name,
                            GroupFields.MEMBER_BALANCE to 0.0,
                            GroupFields.MEMBER_IS_REGISTERED to newMember.isRegistered,
                            GroupFields.MEMBER_UNREGISTERED_NAME to (newMember.unregisteredName ?: ""),
                            GroupFields.MEMBER_ADDED_BY to (newMember.addedBy ?: "")
                        )
                    )
                }
            }
            
            groupRef.update(
                hashMapOf(
                    GroupFields.MEMBERS to updatedMembers,
                    GroupFields.UPDATED_AT to Date()
                ) as Map<String, Any>
            ).await()
        } else {
            throw Exception("Group not found")
        }
    }
}