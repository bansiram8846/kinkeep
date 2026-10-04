package com.example.data

import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity

object DefaultVaultData {
    val defaultMembers = listOf(
        FamilyMemberEntity(
            id = "organizer",
            name = "Elena Morgan",
            role = "Co-Organizer",
            relationship = "Self",
            accessLevel = "FULL",
            avatarUrl = null,
            isEmergencyContact = true,
            documentCount = 0
        )
    )

    // No sample dummy test data - keeps the vault clean and ready for real scanned documents
    val defaultDocuments = emptyList<VaultDocumentEntity>()
}
