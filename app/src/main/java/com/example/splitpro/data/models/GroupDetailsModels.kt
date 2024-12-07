package com.example.splitpro.data.models

import java.util.Date

data class GroupMember(
    val userId: String,
    val name: String,
    val balance: Double,
    val isRegistered: Boolean,
    val unregisteredName: String? = null,
    val addedBy: String? = null // ID of the user who added this member
)

data class ExpenseContributor(
    val userId: String,
    val amount: Double
)

data class Expense(
    val id: String,
    val description: String,
    val amount: Double,
    val createdBy: String,
    val createdAt: Date,
    val groupId: String,
    val contributors: List<ExpenseContributor>
)

data class GroupDetails(
    val id: String,
    val name: String,
    val groupType: String,
    val members: List<GroupMember>,
    val entries: List<String> // Now stores expense IDs instead of GroupEntry objects
)
