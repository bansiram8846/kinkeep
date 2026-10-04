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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
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
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity
import com.example.ui.components.AddFamilyMemberDialog
import com.example.ui.components.CyberButton3D
import com.example.ui.components.DocumentSelectorDialog
import com.example.ui.components.FamilyMemberDetailDialog
import com.example.ui.components.Modern8DFloatingBoxButton
import com.example.ui.components.Modern8DFloatingStatBox
import com.example.ui.components.StatusPill
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.CyberTealDark
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.QuantumIndigo
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
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
    var selectedDetailMember by remember { mutableStateOf<FamilyMemberEntity?>(null) }
    var isAddMemberDialogOpen by remember { mutableStateOf(false) }

    val expiringDocs = remember(allDocs) {
        allDocs.filter { it.status == "EXPIRED" || (it.daysRemaining != null && it.daysRemaining <= 30) }
    }
    val urgentDoc = expiringDocs.minByOrNull { it.daysRemaining ?: 999 }

    // Dialog: Family Member Details
    selectedDetailMember?.let { member ->
        val memberDocs = allDocs.filter { it.memberId == member.id }
        FamilyMemberDetailDialog(
            member = member,
            documents = memberDocs,
            onDismiss = { selectedDetailMember = null },
            onViewDocument = { doc ->
                viewModel.viewDocument(doc)
            },
            onAddDocumentForMember = { memberId ->
                viewModel.openUploadScreen(memberId)
            }
        )
    }

    // Dialog: Document Selector to choose and view any document
    if (isDocumentSelectorOpen) {
        val filteredDocs = if (selectedCategoryForSelector == "All") {
            allDocs
        } else {
            allDocs.filter { it.category.equals(selectedCategoryForSelector, ignoreCase = true) }
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
                            .clickable { selectedDetailMember = allMembers.firstOrNull() },
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

            // MODERN 8D FLOATING BOX BUTTONS
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Modern8DFloatingBoxButton(
                        title = "Family Members",
                        subtitle = "Instant Member Dossier & Security Access",
                        icon = Icons.Default.Diversity3,
                        onClick = {
                            selectedDetailMember = allMembers.firstOrNull()
                        },
                        isPrimary = true,
                        badgeText = "${allMembers.size} ACTIVE",
                        testTag = "dashboard_floating_family_members_button"
                    )

                    Modern8DFloatingBoxButton(
                        title = "Browse Vault Documents",
                        subtitle = "Select, Decrypt & View In-App",
                        icon = Icons.Default.FolderOpen,
                        onClick = {
                            selectedCategoryForSelector = "All"
                            isDocumentSelectorOpen = true
                        },
                        isPrimary = false,
                        badgeText = "${allDocs.size} SECURE",
                        testTag = "dashboard_floating_documents_button"
                    )
                }
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
                        subtitle = "View Details",
                        subtitleColor = CyberTeal,
                        onClick = {
                            selectedDetailMember = allMembers.firstOrNull()
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

            // Family Members Carousel
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Family Members",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "+ Add Member",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            modifier = Modifier.clickable { isAddMemberDialogOpen = true }
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allMembers, key = { it.id }) { member ->
                            val docCount = allDocs.count { it.memberId == member.id }
                            Box(
                                modifier = Modifier
                                    .width(150.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceContainer)
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                    .clickable { selectedDetailMember = member }
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceContainerHighest)
                                            .border(1.dp, CyberTeal, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = member.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "M" },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberTeal
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = member.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = member.role,
                                        fontSize = 11.sp,
                                        color = IndigoLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$docCount docs",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Documents Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Documents",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "View All",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            modifier = Modifier.clickable {
                                selectedCategoryForSelector = "All"
                                isDocumentSelectorOpen = true
                            }
                        )
                    }

                    if (allDocs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceContainer)
                                .border(1.dp, CyberTeal.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    tint = CyberTeal,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Your Clean Family Vault is Empty",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Use the Family tab to scan your first document with AI",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            allDocs.take(5).forEach { doc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SurfaceContainer)
                                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                                        .clickable { viewModel.viewDocument(doc) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(CyberTeal.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = CyberTeal,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = doc.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${doc.memberName} • Expires: ${doc.expiryDate}",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }

                                    StatusPill(status = doc.status, daysRemaining = doc.daysRemaining)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
