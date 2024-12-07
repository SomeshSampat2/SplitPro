package com.example.splitpro.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitpro.firebase.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Group(
    val id: String,
    val name: String,
    val createdAt: Date,
    val memberCount: Int,
    val balance: Double = 0.0 // For now, default to 0 since we haven't implemented expenses
)

data class GroupsState(
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class GroupsViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager.getInstance()
    private val _state = MutableStateFlow(GroupsState())
    val state: StateFlow<GroupsState> = _state.asStateFlow()

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                println("DEBUG: ViewModel - Starting to load groups")
                val groupsData = firebaseManager.getUserGroups()
                println("DEBUG: ViewModel - Received ${groupsData.size} groups from Firebase")
                
                val groups = groupsData.map { data ->
                    println("DEBUG: ViewModel - Processing group data: $data")
                    Group(
                        id = data["id"] as String,
                        name = data["name"] as? String ?: "Unnamed Group",
                        createdAt = (data["createdAt"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                        memberCount = (data["members"] as? List<*>)?.size ?: 1
                    ).also {
                        println("DEBUG: ViewModel - Created Group object: $it")
                    }
                }
                
                println("DEBUG: ViewModel - Updating state with ${groups.size} groups")
                _state.value = _state.value.copy(
                    groups = groups,
                    isLoading = false,
                    error = null
                )
                println("DEBUG: ViewModel - State updated: ${_state.value}")
            } catch (e: Exception) {
                println("DEBUG: ViewModel - Error loading groups: ${e.message}")
                e.printStackTrace()
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    suspend fun createGroup(groupName: String, groupType: String): String {
        try {
            val groupId = firebaseManager.createGroup(groupName, groupType)
            loadGroups() // Refresh the groups list
            return groupId
        } catch (e: Exception) {
            _state.value = _state.value.copy(error = e.message)
            throw e
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null)
    }

    fun formatDate(date: Date): String {
        val now = Calendar.getInstance()
        val dateCalendar = Calendar.getInstance().apply { time = date }

        return when {
            isSameDay(now, dateCalendar) -> "Today"
            isYesterday(now, dateCalendar) -> "Yesterday"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(now: Calendar, date: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            timeInMillis = now.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(yesterday, date)
    }
}
