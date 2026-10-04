package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.ExpirationUtils

@Entity(tableName = "vault_documents")
@TypeConverters(StringListConverter::class)
data class VaultDocumentEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val provider: String,
    val policyOrIdNumber: String,
    val memberId: String,
    val memberName: String,
    val expiryDate: String,
    val expiryTimestamp: Long? = null,
    val remindExpiry: Boolean = true,
    val requireBiometric: Boolean = false,
    val tags: List<String> = emptyList(),
    val fileUri: String? = null,
    val fileSizeText: String? = null,
    val metricText: String? = null,
    val sharedNotes: String? = null,
    val status: String = "ACTIVE",
    val daysRemaining: Int? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val role: String,
    val relationship: String,
    val accessLevel: String = "FULL",
    val avatarUrl: String? = null,
    val isEmergencyContact: Boolean = true,
    val documentCount: Int = 0
)

class StringListConverter {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("||")
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        if (list.isNullOrEmpty()) return ""
        return list.joinToString("||")
    }
}
