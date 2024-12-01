package com.example.splitpro.firebase

object FirebaseConstants {
    // Collections
    const val COLLECTION_USERS = "Users"
    
    // Document Fields - User
    object UserFields {
        const val EMAIL = "email"
        const val NAME = "name"
        const val PHONE_NUMBER = "phoneNumber"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
    
    // Error Messages
    object ErrorMessages {
        const val USER_NOT_FOUND = "User not found"
        const val SIGN_IN_FAILED = "Sign in failed"
        const val PROFILE_UPDATE_FAILED = "Failed to update profile"
    }
}
