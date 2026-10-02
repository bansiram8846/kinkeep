package com.example.data

import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity

object DefaultVaultData {
    // Clean production profile without any test details, mock identities, or sample documents
    val members = listOf(
        FamilyMemberEntity(
            id = "organizer",
            name = "Family Organizer",
            relationship = "Self",
            role = "Primary Administrator",
            accessPermission = "Full Access",
            avatarUrl = null,
            initials = "ME",
            badgeColorHex = 0xFF00F0D0,
            isEmergencyContact = false
        )
    )

    // Clean vault: Zero test documents
    val documents = emptyList<VaultDocumentEntity>()
}
