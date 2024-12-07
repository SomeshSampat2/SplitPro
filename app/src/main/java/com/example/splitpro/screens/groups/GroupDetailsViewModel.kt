package com.example.splitpro.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitpro.data.models.Expense
import com.example.splitpro.data.models.GroupDetails
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
    
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()
    
    private var currentGroupId: String? = null

    fun loadGroupDetails(groupId: String) {
        if (currentGroupId == groupId) return
        currentGroupId = groupId
        
        viewModelScope.launch {
            try {
                val details = firebaseManager.getGroupDetails(groupId)
                _groupDetails.value = details
                
                // Load expenses if group details are available
                details?.let { group ->
                    val expenses = firebaseManager.getGroupExpenses(group.entries)
                    _expenses.value = expenses
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
