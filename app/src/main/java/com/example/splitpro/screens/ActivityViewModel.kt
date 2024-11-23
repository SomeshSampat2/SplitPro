package com.example.splitpro.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

enum class ActivityType {
    EXPENSE_ADDED,
    PAYMENT_SENT,
    PAYMENT_RECEIVED,
    GROUP_CREATED,
    MEMBER_ADDED,
    EXPENSE_SETTLED
}

data class ActivityItem(
    val id: String,
    val type: ActivityType,
    val timestamp: Date,
    val amount: Double? = null,
    val groupName: String,
    val description: String,
    val involvedUser: String
)

data class ActivityState(
    val activities: List<ActivityItem> = listOf(
        ActivityItem(
            id = "1",
            type = ActivityType.EXPENSE_ADDED,
            timestamp = Calendar.getInstance().apply { add(Calendar.MINUTE, -30) }.time,
            amount = 1250.00,
            groupName = "Goa Trip 2024",
            description = "Added expense for Hotel Booking",
            involvedUser = "Rahul"
        ),
        ActivityItem(
            id = "2",
            type = ActivityType.PAYMENT_RECEIVED,
            timestamp = Calendar.getInstance().apply { add(Calendar.HOUR, -2) }.time,
            amount = 500.00,
            groupName = "Monthly Flatmates",
            description = "Electricity bill payment received",
            involvedUser = "Priya"
        ),
        ActivityItem(
            id = "3",
            type = ActivityType.GROUP_CREATED,
            timestamp = Calendar.getInstance().apply { add(Calendar.HOUR, -5) }.time,
            groupName = "Weekend Party",
            description = "Created new group",
            involvedUser = "You"
        ),
        ActivityItem(
            id = "4",
            type = ActivityType.PAYMENT_SENT,
            timestamp = Calendar.getInstance().apply { add(Calendar.HOUR, -12) }.time,
            amount = 750.25,
            groupName = "Office Lunch Group",
            description = "Sent payment for lunch",
            involvedUser = "Amit"
        ),
        ActivityItem(
            id = "5",
            type = ActivityType.MEMBER_ADDED,
            timestamp = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            groupName = "Goa Trip 2024",
            description = "Added to the group",
            involvedUser = "Neha"
        ),
        ActivityItem(
            id = "6",
            type = ActivityType.EXPENSE_ADDED,
            timestamp = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
            amount = 2500.00,
            groupName = "Monthly Flatmates",
            description = "Added expense for Grocery Shopping",
            involvedUser = "Raj"
        ),
        ActivityItem(
            id = "7",
            type = ActivityType.EXPENSE_SETTLED,
            timestamp = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
            amount = 1875.50,
            groupName = "Movie Night",
            description = "Settled all expenses",
            involvedUser = "Group"
        ),
        ActivityItem(
            id = "8",
            type = ActivityType.PAYMENT_RECEIVED,
            timestamp = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
            amount = 1000.00,
            groupName = "Road Trip",
            description = "Fuel expenses payment received",
            involvedUser = "Arjun"
        ),
        ActivityItem(
            id = "9",
            type = ActivityType.EXPENSE_ADDED,
            timestamp = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
            amount = 3500.00,
            groupName = "Birthday Celebration",
            description = "Added expense for Cake and Decorations",
            involvedUser = "Sneha"
        ),
        ActivityItem(
            id = "10",
            type = ActivityType.PAYMENT_SENT,
            timestamp = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
            amount = 450.75,
            groupName = "Dinner Club",
            description = "Sent payment for dinner",
            involvedUser = "Vikram"
        )
    )
)

class ActivityViewModel : ViewModel() {
    private val _state = MutableStateFlow(ActivityState())
    val state: StateFlow<ActivityState> = _state

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    fun formatTimestamp(date: Date): String {
        val now = Calendar.getInstance()
        val activityTime = Calendar.getInstance().apply { time = date }

        return when {
            isSameDay(now, activityTime) -> "Today ${timeFormat.format(date)}"
            isYesterday(now, activityTime) -> "Yesterday ${timeFormat.format(date)}"
            else -> "${dateFormat.format(date)} ${timeFormat.format(date)}"
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
