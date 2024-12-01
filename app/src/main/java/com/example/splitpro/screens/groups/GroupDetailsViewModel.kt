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
                    val members = (groupData[GroupFields.MEMBERS] as? List<*>)?.size ?: 0
                    val groupName = groupData[GroupFields.NAME] as? String ?: "Unknown Group"
                    val groupType = groupData[GroupFields.TYPE] as? String ?: "Unknown Type"
                    
                    _groupDetails.value = GroupDetails(
                        id = groupId,
                        name = groupName,
                        groupType = groupType,
                        members = emptyList(), // Empty list for now
                        entries = emptyList()  // Empty list for now
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
