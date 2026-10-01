package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey val id: String,
    val name: String,
    val relationship: String,
    val role: String,
    val accessPermission: String,
    val avatarUrl: String? = null,
    val initials: String,
    val badgeColorHex: Long,
    val isEmergencyContact: Boolean = false
)

@Entity(tableName = "vault_documents")
data class VaultDocumentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val provider: String,
    val policyOrIdNumber: String,
    val memberId: String,
    val memberName: String,
    val expiryDate: String,
    val expiryTimestamp: Long? = null,
    val daysRemaining: Int? = null,
    val status: String, // "Verified", "Expiring", "Expired", "Active"
    val metricText: String? = null,
    val isActionNeeded: Boolean = false,
    val remindBeforeExpiry: Boolean = true,
    val reminderDaysBefore: Int = 30, // 30-day early notification trigger
    val requireBiometric: Boolean = false,
    val tagsCsv: String = "",
    val sharedNotes: String? = null,
    val isPinned: Boolean = false,
    val fileUri: String? = null,
    val fileType: String? = null,
    val fileSizeText: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
