package com.example.splitpro.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitpro.data.models.GroupDetails
import com.example.splitpro.data.models.GroupEntry
import com.example.splitpro.data.models.GroupMember
import com.example.splitpro.firebase.FirebaseManager
import com.example.splitpro.firebase.FirebaseConstants.GroupFields
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupDetailsViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager.getInstance()
    private val _groupDetails = MutableStateFlow<GroupDetails?>(null)
    val groupDetails: StateFlow<GroupDetails?> = _groupDetails.asStateFlow()

    private var currentGroupId: String? = null

    fun loadGroupDetails(groupId: String) {
        if (currentGroupId == groupId) return
        currentGroupId = groupId
        
        viewModelScope.launch {
            try {
                val groupData = firebaseManager.getGroupDetails(groupId)
                if (groupData != null) {
                    @Suppress("UNCHECKED_CAST")
                    val membersData = groupData["members"] as? List<Map<String, Any>> ?: emptyList()
                    val members = membersData.map { memberData ->
                        GroupMember(
                            userId = memberData["userId"] as? String ?: "",
                            name = memberData["name"] as? String ?: "Unknown",
                            balance = (memberData["balance"] as? Number)?.toDouble() ?: 0.0,
                            isRegistered = memberData["isRegistered"] as? Boolean ?: false,
                            unregisteredName = memberData["unregisteredName"] as? String,
                            addedBy = memberData["addedBy"] as? String
                        )
                    }
                    
                    _groupDetails.value = GroupDetails(
                        id = groupId,
                        name = groupData["name"] as? String ?: "Unknown Group",
                        groupType = groupData["type"] as? String ?: "Unknown Type",
                        members = members,
                        entries = emptyList()  // We'll handle entries later
                    )
                }
            } catch (e: Exception) {
                println("Error loading group details: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    suspend fun addMembersToGroup(groupId: String, selectedContacts: List<Contact>) {
        try {
            val currentUserId = firebaseManager.currentUser?.uid ?: throw Exception("User not logged in")
            val members = selectedContacts.map { contact ->
                val userId = firebaseManager.findUserByPhoneNumber(contact.phoneNumber) ?: contact.phoneNumber
                GroupMember(
                    userId = userId,
                    name = contact.name,
                    balance = 0.0,
                    isRegistered = userId != contact.phoneNumber,
                    unregisteredName = if (userId == contact.phoneNumber) contact.name else null,
                    addedBy = currentUserId
                )
            }
            
            firebaseManager.addMembersToGroup(groupId, members)
            // Refresh group details
            loadGroupDetails(groupId)
        } catch (e: Exception) {
            _groupDetails.value = null
            throw e
        }
    }
}
