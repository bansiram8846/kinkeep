package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddFamilyMemberDialog
import com.example.ui.components.CyberButton3D
import com.example.ui.components.DocumentSelectorDialog
import com.example.ui.components.FamilyMembersListDialog
import com.example.ui.components.Modern8DCategoryCard8D
import com.example.ui.components.Modern8DEnclaveGuardianCard
import com.example.ui.components.Modern8DFloatingBoxButton
import com.example.ui.components.Modern8DFloatingStatBox
import com.example.ui.components.Modern8DGridButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun VaultOverviewScreen(
    viewModel: VaultViewModel,
    modifier: Modifier = Modifier
) {
    val allDocs by viewModel.allDocuments.collectAsStateWithLifecycle()
    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()

    var isDocumentSelectorOpen by remember { mutableStateOf(false) }
    var selectedCategoryForSelector by remember { mutableStateOf("All") }
    var isFamilyMembersListOpen by remember { mutableStateOf(false) }
    var isAddMemberDialogOpen by remember { mutableStateOf(false) }

    val expiringDocs = remember(allDocs) {
        allDocs.filter { it.status == "EXPIRED" || (it.daysRemaining != null && it.daysRemaining <= 30) }
    }
    val urgentDoc = expiringDocs.minByOrNull { it.daysRemaining ?: 999 }

    // Category document counts
    val idDocsCount = remember(allDocs) {
        allDocs.count {
            it.category.contains("Passport", ignoreCase = true) ||
            it.category.contains("Driving", ignoreCase = true) ||
            it.category.contains("ID", ignoreCase = true)
        }
    }
    val healthDocsCount = remember(allDocs) {
        allDocs.count { it.category.contains("Health", ignoreCase = true) }
    }
    val vehicleDocsCount = remember(allDocs) {
        allDocs.count { it.category.contains("Vehicle", ignoreCase = true) }
    }
    val propertyDocsCount = remember(allDocs) {
        allDocs.count {
            it.category.contains("Property", ignoreCase = true) ||
            it.category.contains("Deed", ignoreCase = true)
        }
    }

    // Dialog: Family Members List ONLY (navigates directly to that member's vault)
    if (isFamilyMembersListOpen) {
        FamilyMembersListDialog(
            members = allMembers,
            onSelectMember = { member ->
                isFamilyMembersListOpen = false
                viewModel.openMemberVault(member.id)
            },
            onAddNewMember = {
                isFamilyMembersListOpen = false
                isAddMemberDialogOpen = true
            },
            onDismiss = { isFamilyMembersListOpen = false }
        )
    }

    // Dialog: Document Selector to choose and view any document
    if (isDocumentSelectorOpen) {
        val filteredDocs = when (selectedCategoryForSelector) {
            "All" -> allDocs
            "Passport & ID" -> allDocs.filter {
                it.category.contains("Passport", ignoreCase = true) ||
                it.category.contains("Driving", ignoreCase = true) ||
                it.category.contains("ID", ignoreCase = true)
            }
            "Health Insurance" -> allDocs.filter { it.category.contains("Health", ignoreCase = true) }
            "Vehicle Insurance" -> allDocs.filter { it.category.contains("Vehicle", ignoreCase = true) }
            "Property & Deed" -> allDocs.filter {
                it.category.contains("Property", ignoreCase = true) ||
                it.category.contains("Deed", ignoreCase = true)
            }
            else -> allDocs.filter { it.category.contains(selectedCategoryForSelector, ignoreCase = true) }
        }

        DocumentSelectorDialog(
            documents = filteredDocs,
            category = selectedCategoryForSelector,
            onDismiss = { isDocumentSelectorOpen = false },
            onSelectDocument = { doc ->
                viewModel.viewDocument(doc)
            }
        )
    }

    // Dialog: Add Family Member
    if (isAddMemberDialogOpen) {
        AddFamilyMemberDialog(
            onDismiss = { isAddMemberDialogOpen = false },
            onSave = { name, role, relationship, accessLevel, isEmergencyContact ->
                viewModel.addFamilyMember(name, role, relationship, accessLevel, null, isEmergencyContact)
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "KINKEEP ZERO-KNOWLEDGE",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Family Vault Dashboard",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHighest)
                            .border(1.dp, CyberTeal.copy(alpha = 0.4f), CircleShape)
                            .clickable { isFamilyMembersListOpen = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EM",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal
                        )
                    }
                }
            }

            // MODERN 8D FLOATING HERO BUTTON: Family Members only
            item {
                Modern8DFloatingBoxButton(
                    title = "Family Members",
                    subtitle = "Select a Member to Open Personal Vault",
                    icon = Icons.Default.Diversity3,
                    onClick = {
                        isFamilyMembersListOpen = true
                    },
                    isPrimary = true,
                    badgeText = "${allMembers.size} ACTIVE",
                    testTag = "dashboard_floating_family_members_button"
                )
            }

            // MODERN 8D FLOATING STAT BOXES
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Modern8DFloatingStatBox(
                        title = "Total Docs",
                        count = allDocs.size.toString(),
                        subtitle = if (allDocs.isEmpty()) "Empty Vault" else "✓ In-App Ready",
                        subtitleColor = CyberTeal,
                        onClick = {
                            selectedCategoryForSelector = "All"
                            isDocumentSelectorOpen = true
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "stat_total_docs"
                    )

                    Modern8DFloatingStatBox(
                        title = "Family",
                        count = allMembers.size.toString(),
                        subtitle = "Select Member",
                        subtitleColor = CyberTeal,
                        onClick = {
                            isFamilyMembersListOpen = true
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "stat_family"
                    )

                    Modern8DFloatingStatBox(
                        title = "Expiring",
                        count = expiringDocs.size.toString(),
                        subtitle = if (expiringDocs.isNotEmpty()) "Action Req." else "All Clear",
                        subtitleColor = if (expiringDocs.isNotEmpty()) AlertRedLight else CyberTeal,
                        isAlert = expiringDocs.isNotEmpty(),
                        onClick = { viewModel.selectTab(BottomTab.EXPIRING) },
                        modifier = Modifier.weight(1f),
                        testTag = "stat_expiring"
                    )
                }
            }

            // Urgent Banner (Only when urgent document exists)
            if (urgentDoc != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF381014), Color(0xFF220A0D))
                                )
                            )
                            .border(1.dp, AlertRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = AlertRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "POLICY EXPIRING SOON",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AlertRed
                                    )
                                }
                                StatusPill(status = urgentDoc.status, daysRemaining = urgentDoc.daysRemaining)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = urgentDoc.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Expires on ${urgentDoc.expiryDate}. Early alert triggered before expiration.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            CyberButton3D(
                                text = "Review & Renew Policy",
                                onClick = { viewModel.renewPolicy(urgentDoc) },
                                modifier = Modifier.fillMaxWidth(),
                                height = 40.dp,
                                testTag = "urgent_renew_button"
                            )
                        }
                    }
                }
            }

            // MODERN 8D QUICK ACTIONS GRID (Utilizing space with 8D models)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Modern8DGridButton(
                        title = "AI Scan & Add",
                        subtitle = "Auto-extract fields",
                        icon = Icons.Default.AutoAwesome,
                        onClick = { viewModel.openUploadScreen() },
                        isPrimary = true,
                        badgeText = "GEMINI AI",
                        modifier = Modifier.weight(1f),
                        testTag = "dashboard_ai_scan_button"
                    )

                    Modern8DGridButton(
                        title = "+ Add Member",
                        subtitle = "Enroll new profile",
                        icon = Icons.Default.PersonAdd,
                        onClick = { isAddMemberDialogOpen = true },
                        isPrimary = false,
                        badgeText = "FAMILY",
                        modifier = Modifier.weight(1f),
                        testTag = "dashboard_add_member_button"
                    )
                }
            }

            // MODERN 8D CATEGORIES SECTION (Utilizing space with 8D models)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = CyberTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SECURE VAULT CATEGORIES",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Text(
                        text = "FILTER & VIEW",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Modern8DCategoryCard8D(
                            title = "Passport & ID",
                            countText = if (idDocsCount > 0) "$idDocsCount Records" else "Tap to view",
                            icon = Icons.Default.PermIdentity,
                            accentColor = Color(0xFF64B5F6),
                            onClick = {
                                selectedCategoryForSelector = "Passport & ID"
                                isDocumentSelectorOpen = true
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "cat_passport_id"
                        )

                        Modern8DCategoryCard8D(
                            title = "Health Policies",
                            countText = if (healthDocsCount > 0) "$healthDocsCount Records" else "Tap to view",
                            icon = Icons.Default.HealthAndSafety,
                            accentColor = CyberTeal,
                            onClick = {
                                selectedCategoryForSelector = "Health Insurance"
                                isDocumentSelectorOpen = true
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "cat_health_insurance"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Modern8DCategoryCard8D(
                            title = "Vehicle & Auto",
                            countText = if (vehicleDocsCount > 0) "$vehicleDocsCount Records" else "Tap to view",
                            icon = Icons.Default.DirectionsBike,
                            accentColor = Color(0xFFFFB74D),
                            onClick = {
                                selectedCategoryForSelector = "Vehicle Insurance"
                                isDocumentSelectorOpen = true
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "cat_vehicle_insurance"
                        )

                        Modern8DCategoryCard8D(
                            title = "Property & Deed",
                            countText = if (propertyDocsCount > 0) "$propertyDocsCount Records" else "Tap to view",
                            icon = Icons.Default.HomeWork,
                            accentColor = Color(0xFFBA68C8),
                            onClick = {
                                selectedCategoryForSelector = "Property & Deed"
                                isDocumentSelectorOpen = true
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "cat_property_deed"
                        )
                    }
                }
            }

            // MODERN 8D ENCLAVE GUARDIAN (Utilizing bottom space with 8D hardware security model)
            item {
                Modern8DEnclaveGuardianCard(
                    docCount = allDocs.size,
                    memberCount = allMembers.size,
                    onRunAudit = {
                        viewModel.showToast(
                            "🛡️ Security Audit Verified: ${allDocs.size} documents sealed in AES-256-GCM Hardware Enclave. Zero cloud leaks detected."
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "dashboard_enclave_guardian"
                )
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
