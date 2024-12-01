package com.example.splitpro.firebase

object FirebaseConstants {
    // Collections
    const val COLLECTION_USERS = "Users"
    const val COLLECTION_GROUPS = "groups"
    
    // Document Fields - User
    object UserFields {
        const val EMAIL = "email"
        const val NAME = "name"
        const val PHONE_NUMBER = "phoneNumber"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }
    
    // Document Fields - Group
    object GroupFields {
        const val NAME = "name"
        const val TYPE = "type"
        const val CREATED_BY = "created_by"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
        const val MEMBERS = "members"
    }
    
    // Error Messages
    object ErrorMessages {
        const val USER_NOT_FOUND = "User not found"
        const val SIGN_IN_FAILED = "Sign in failed"
        const val PROFILE_UPDATE_FAILED = "Failed to update profile"
    }
}
