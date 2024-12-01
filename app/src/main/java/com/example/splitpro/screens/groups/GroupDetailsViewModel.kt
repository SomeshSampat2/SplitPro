package com.example.splitpro.screens.groups

import androidx.lifecycle.ViewModel
import com.example.splitpro.data.models.GroupDetails
import com.example.splitpro.data.models.GroupEntry
import com.example.splitpro.data.models.GroupMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class GroupDetailsViewModel : ViewModel() {
    private val _groupDetails = MutableStateFlow<GroupDetails?>(null)
    val groupDetails: StateFlow<GroupDetails?> = _groupDetails.asStateFlow()

    init {
        loadSampleData()
    }

    private fun loadSampleData() {
        val sampleMembers = listOf(
            GroupMember("1", "Raj Kumar", 1500.0),
            GroupMember("2", "Priya Singh", -800.0),
            GroupMember("3", "Amit Shah", 2200.0),
            GroupMember("4", "Neha Verma", -1200.0),
            GroupMember("5", "Vikram Mehta", 600.0),
            GroupMember("6", "Ananya Patel", -300.0)
        )

        val calendar = Calendar.getInstance()
        
        // Today's entries
        val todayEntries = mutableListOf(
            GroupEntry(
                "1",
                calendar.time,
                "Raj Kumar",
                2500.0,
                "Dinner at Taj Restaurant",
                "8:30 PM",
                625.0
            ),
            GroupEntry(
                "2",
                calendar.time,
                "Priya Singh",
                1200.0,
                "Movie tickets - Pathaan",
                "4:15 PM",
                -300.0
            ),
            GroupEntry(
                "3",
                calendar.time,
                "Amit Shah",
                800.0,
                "Evening snacks",
                "6:45 PM",
                200.0
            )
        )

        // Yesterday's entries
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val yesterdayEntries = mutableListOf(
            GroupEntry(
                "4",
                calendar.time,
                "Neha Verma",
                3600.0,
                "Weekend shopping",
                "2:30 PM",
                -900.0
            ),
            GroupEntry(
                "5",
                calendar.time,
                "Vikram Mehta",
                1500.0,
                "Groceries from BigBasket",
                "11:20 AM",
                375.0
            )
        )

        // Day before yesterday entries
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val dayBeforeEntries = mutableListOf(
            GroupEntry(
                "6",
                calendar.time,
                "Ananya Patel",
                4000.0,
                "House party supplies",
                "5:00 PM",
                -1000.0
            ),
            GroupEntry(
                "7",
                calendar.time,
                "Raj Kumar",
                1800.0,
                "Pizza order",
                "7:30 PM",
                450.0
            ),
            GroupEntry(
                "8",
                calendar.time,
                "Amit Shah",
                2200.0,
                "Drinks and snacks",
                "8:15 PM",
                550.0
            )
        )

        val allEntries = todayEntries + yesterdayEntries + dayBeforeEntries

        _groupDetails.value = GroupDetails(
            "sample_id",
            "Weekend Trip Group",
            "Trip",
            sampleMembers,
            allEntries
        )
    }
}
