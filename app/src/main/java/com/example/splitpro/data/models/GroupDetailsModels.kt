package com.example.splitpro.data.models

import java.util.Date

data class GroupMember(
    val id: String,
    val name: String,
    val balance: Double // positive means you'll receive, negative means you'll pay
)

data class GroupEntry(
    val id: String,
    val date: Date,
    val paidBy: String,
    val amount: Double,
    val description: String,
    val time: String,
    val yourShare: Double // positive means you'll receive, negative means you'll pay
)

data class GroupDetails(
    val id: String,
    val name: String,
    val groupType: String,
    val members: List<GroupMember>,
    val entries: List<GroupEntry>
)
