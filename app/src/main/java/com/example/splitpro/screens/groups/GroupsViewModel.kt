package com.example.splitpro.screens.groups

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

data class Group(
    val id: String,
    val name: String,
    val createdAt: Date,
    val memberCount: Int,
    val balance: Double // positive means you'll receive, negative means you owe
)

data class GroupsState(
    val groups: List<Group> = listOf(
        Group(
            id = "1",
            name = "Goa Trip 2024",
            createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
            memberCount = 5,
            balance = -1250.50
        ),
        Group(
            id = "2",
            name = "Monthly Flatmates",
            createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -15) }.time,
            memberCount = 3,
            balance = 2500.75
        ),
        Group(
            id = "3",
            name = "Office Lunch Group",
            createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -7) }.time,
            memberCount = 8,
            balance = -750.25
        ),
        Group(
            id = "4",
            name = "Weekend Party",
            createdAt = Calendar.getInstance().apply { add(Calendar.HOUR, -12) }.time,
            memberCount = 4,
            balance = 1000.00
        ),
        Group(
            id = "5",
            name = "Movie Night",
            createdAt = Calendar.getInstance().apply { add(Calendar.HOUR, -6) }.time,
            memberCount = 6,
            balance = -450.75
        ),
        Group(
            id = "6",
            name = "Birthday Celebration",
            createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            memberCount = 12,
            balance = 875.25
        ),
        Group(
            id = "7",
            name = "Road Trip",
            createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -5) }.time,
            memberCount = 7,
            balance = -2100.00
        ),
        Group(
            id = "8",
            name = "Dinner Club",
            createdAt = Calendar.getInstance().apply { add(Calendar.HOUR, -36) }.time,
            memberCount = 5,
            balance = 1500.50
        ),
        Group(
            id = "9",
            name = "Shopping Split",
            createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
            memberCount = 3,
            balance = -675.25
        ),
        Group(
            id = "10",
            name = "Utility Bills",
            createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -30) }.time,
            memberCount = 4,
            balance = 3250.00
        ),
        Group(
            id = "11",
            name = "Gaming Night",
            createdAt = Calendar.getInstance().apply { add(Calendar.HOUR, -24) }.time,
            memberCount = 6,
            balance = -325.50
        ),
        Group(
            id = "12",
            name = "Beach Day",
            createdAt = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -4) }.time,
            memberCount = 8,
            balance = 950.75
        )
    )
)

class GroupsViewModel : ViewModel() {
    private val _state = MutableStateFlow(GroupsState())
    val state: StateFlow<GroupsState> = _state

    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private val yearFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun formatDate(date: Date): String {
        val now = Calendar.getInstance()
        val createdAt = Calendar.getInstance().apply { time = date }

        return when {
            isSameDay(now, createdAt) -> "Today"
            isYesterday(now, createdAt) -> "Yesterday"
            now.get(Calendar.YEAR) == createdAt.get(Calendar.YEAR) -> dateFormat.format(date)
            else -> yearFormat.format(date)
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
