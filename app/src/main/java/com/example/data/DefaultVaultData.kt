package com.example.data

import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity

object DefaultVaultData {
    val ELENA_AVATAR_URL = "https://lh3.googleusercontent.com/aida-public/AB6AXuCG6PejAm7XdgMmDZPpR05CnMzWMKmzItG7qIiLK2grODN3tI7b8b48Z5S1eFUZfNmARrdXUJb9k55gfaUqWpQwCluVSZIeFvzYuckFb2laYiYfAzs8YiCwb2rjaD5qkUyC3ht5mMnPRXtE52OzgQBUZX1MU_z8uU8v8mxPacmCKht_84T2liTJd8siKR7qGAWNAynV71eUR7Cecua1s1A1h0guWMkDGGr4jKuqERi6iAcLONCcXfaf"

    // Default core family profiles without any fake test documents
    val members = listOf(
        FamilyMemberEntity(
            id = "alex",
            name = "Alex Morgan",
            relationship = "You",
            role = "Full Organizer",
            accessPermission = "Full Access",
            avatarUrl = null,
            initials = "AL",
            badgeColorHex = 0xFF00F0D0,
            isEmergencyContact = false
        ),
        FamilyMemberEntity(
            id = "elena",
            name = "Elena Morgan",
            relationship = "Spouse",
            role = "Co-Organizer",
            accessPermission = "Full Access",
            avatarUrl = ELENA_AVATAR_URL,
            initials = "EL",
            badgeColorHex = 0xFF5352ED,
            isEmergencyContact = true
        )
    )

    // Clean initial vault: NO test documents
    val documents = emptyList<VaultDocumentEntity>()
}
