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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity
import com.example.ui.components.AddFamilyMemberDialog
import com.example.ui.components.CyberButton3D
import com.example.ui.components.CyberIconButton3D
import com.example.ui.components.CyberSecondaryButton3D
import com.example.ui.components.StatusPill
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.CyberTealBright
import com.example.ui.theme.CyberTealDark
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.QuantumIndigo
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun VaultOverviewScreen(
    viewModel: VaultViewModel,
    onOpenMemberVault: (String) -> Unit,
    onOpenUpload: () -> Unit,
    onManageFamily: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allDocs by viewModel.allDocuments.collectAsState()
    val allMembers by viewModel.allMembers.collectAsState()
    val expiringDocs by viewModel.expiringDocuments.collectAsState()

    var isAddMemberDialogOpen by remember { mutableStateOf(false) }

    val pinnedDocs = allDocs.filter { it.isPinned }.take(4).ifEmpty { allDocs.take(3) }
    val urgentDoc = expiringDocs.firstOrNull()

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
            // Header Top Bar
            item {
                Spacer(modifier = Modifier.height(4.dp))
                DashboardHeader(
                    expiringCount = expiringDocs.size,
                    onNotificationsClick = { viewModel.openAlertsModal() },
                    onProfileClick = { onOpenMemberVault("alex") }
                )
            }

            // Greeting
            item {
                Column {
                    Text(
                        text = "Good morning, Alex 👋",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Your family archive is secure and updated",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // 3 Stat Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Docs",
                        count = allDocs.size.toString(),
                        subtitle = if (allDocs.isEmpty()) "Empty" else "✓ Safe",
                        subtitleColor = CyberTeal,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Family",
                        count = allMembers.size.toString(),
                        subtitle = "Members",
                        subtitleColor = TextSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onManageFamily() }
                    )
                    StatCard(
                        title = "Expiring",
                        count = expiringDocs.size.toString(),
                        subtitle = if (expiringDocs.isNotEmpty()) "Action req." else "All Clear",
                        subtitleColor = if (expiringDocs.isNotEmpty()) AlertRedLight else CyberTeal,
                        isAlert = expiringDocs.isNotEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.selectTab(BottomTab.EXPIRING) }
                    )
                }
            }

            // Urgent Banner (Only when urgent document exists)
            if (urgentDoc != null) {
                item {
                    UrgentRenewalBanner(
                        document = urgentDoc,
                        onRenew = { viewModel.renewPolicy(urgentDoc) }
                    )
                }
            }

            // Clean Zero State if vault is empty
            if (allDocs.isEmpty()) {
                item {
                    EmptyVaultZeroState(
                        onUploadClick = onOpenUpload,
                        onAddMemberClick = { isAddMemberDialogOpen = true }
                    )
                }
            }

            // Family Members Carousel
            item {
                FamilyMembersSection(
                    members = allMembers,
                    totalDocsCount = allDocs.size,
                    onMemberClick = { memberId ->
                        if (memberId == "all") {
                            onOpenMemberVault("alex")
                        } else {
                            onOpenMemberVault(memberId)
                        }
                    },
                    onManageClick = onManageFamily,
                    onAddMemberClick = { isAddMemberDialogOpen = true }
                )
            }

            // Document Categories (2x2 Grid)
            item {
                DocumentCategoriesSection(
                    allDocs = allDocs,
                    onCategoryClick = { catName ->
                        viewModel.setDashboardCategoryFilter(catName)
                        onOpenUpload()
                    }
                )
            }

            // Pinned & Recent Section
            if (pinnedDocs.isNotEmpty()) {
                item {
                    PinnedAndRecentSection(
                        documents = pinnedDocs,
                        onViewDoc = { viewModel.viewDocument(it) },
                        onShareDoc = { viewModel.openShare(it) },
                        onViewAll = { onOpenMemberVault("alex") }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(88.dp))
            }
        }

        // 8D Tactile Floating Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 68.dp)
        ) {
            CyberButton3D(
                text = "Add Document",
                icon = Icons.Default.QrCodeScanner,
                onClick = onOpenUpload,
                height = 48.dp,
                modifier = Modifier.width(180.dp),
                testTag = "fab_add_document"
            )
        }

        // Add Family Member Modal Dialog
        if (isAddMemberDialogOpen) {
            AddFamilyMemberDialog(
                onDismiss = { isAddMemberDialogOpen = false },
                onSave = { name, rel, perm, avatarUrl ->
                    viewModel.addFamilyMember(name, rel, perm, avatarUrl)
                }
            )
        }
    }
}

@Composable
private fun EmptyVaultZeroState(
    onUploadClick: () -> Unit,
    onAddMemberClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceContainer)
            .border(1.5.dp, CyberTeal.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(CyberTeal.copy(alpha = 0.15f))
                    .border(2.dp, CyberTeal.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = CyberTeal,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Your Family Vault is Ready",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Zero test clutter. Secure your driving licenses, vehicle policies, passports, property deeds & health insurance records in one private encrypted repository.",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 8D Buttons in zero state
            CyberButton3D(
                text = "+ Upload First Document",
                icon = Icons.Default.UploadFile,
                onClick = onUploadClick,
                modifier = Modifier.fillMaxWidth()
            )

            CyberSecondaryButton3D(
                text = "+ Add Family Member",
                icon = Icons.Default.PersonAdd,
                onClick = onAddMemberClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DashboardHeader(
    expiringCount: Int = 0,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberTeal.copy(alpha = 0.15f))
                        .border(1.dp, CyberTeal.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "KinKeep Shield",
                        tint = CyberTeal,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "KinKeep",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Family Vault",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (expiringCount > 0) Color(0xFF3B151C) else SurfaceContainerHigh)
                            .border(
                                1.dp,
                                if (expiringCount > 0) AlertRed.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f),
                                CircleShape
                            )
                            .testTag("notification_bell_button")
                    ) {
                        Icon(
                            imageVector = if (expiringCount > 0) Icons.Default.NotificationsActive else Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = if (expiringCount > 0) AlertRedLight else TextSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    if (expiringCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(AlertRed)
                                .border(1.5.dp, ObsidianBackground, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (expiringCount > 9) "9+" else "$expiringCount",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CyberTeal)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTealDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Security Status sub-bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(CyberTeal)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Private & End-to-End Encrypted",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "All synced",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: String,
    subtitle: String,
    subtitleColor: Color,
    isAlert: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainer)
            .border(
                1.dp,
                if (isAlert) AlertRed.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = subtitleColor
            )
        }
    }
}

@Composable
private fun UrgentRenewalBanner(
    document: VaultDocumentEntity,
    onRenew: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainer)
            .border(1.dp, AlertRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AlertRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = AlertRed,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = document.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    val days = document.daysRemaining
                    val daysText = if (days != null) "Expires in $days days (${document.expiryDate})" else document.expiryDate
                    Text(
                        text = "⚠️ 30-Day Alert • $daysText",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertRedLight
                    )
                }
            }

            Button(
                onClick = onRenew,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF9A8A8),
                    contentColor = Color(0xFF690005)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(
                    text = "Renew",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FamilyMembersSection(
    members: List<FamilyMemberEntity>,
    totalDocsCount: Int,
    onMemberClick: (String) -> Unit,
    onManageClick: () -> Unit,
    onAddMemberClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FAMILY MEMBERS",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "Manage",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTeal,
                modifier = Modifier.clickable { onManageClick() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(CyberTeal)
                        .clickable { onMemberClick("all") }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "All ($totalDocsCount)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTealDark
                    )
                }
            }

            items(members) { member ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(SurfaceContainer)
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
                        .clickable { onMemberClick(member.id) }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (member.id == "alex") CyberTeal.copy(alpha = 0.25f)
                                else QuantumIndigo.copy(alpha = 0.25f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (member.avatarUrl != null) {
                            AsyncImage(
                                model = member.avatarUrl,
                                contentDescription = member.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = member.initials,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (member.id == "alex") CyberTeal else IndigoLight
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (member.id == "alex") "Alex (You)" else member.name.split(" ").first(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }

            // Quick + Add Member pill in carousel
            item {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(SurfaceContainerHighest)
                        .border(1.dp, CyberTeal.copy(alpha = 0.4f), RoundedCornerShape(999.dp))
                        .clickable { onAddMemberClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Member",
                        tint = CyberTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Member",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentCategoriesSection(
    allDocs: List<VaultDocumentEntity>,
    onCategoryClick: (String) -> Unit
) {
    val idCount = allDocs.count { it.category.contains("Identity", ignoreCase = true) }
    val vehCount = allDocs.count { it.category.contains("Vehicles", ignoreCase = true) }
    val healthCount = allDocs.count { it.category.contains("Health", ignoreCase = true) }
    val propCount = allDocs.count { it.category.contains("Property", ignoreCase = true) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DOCUMENT CATEGORIES",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "Organized",
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CategoryGridCard(
                icon = Icons.Default.Badge,
                iconTint = CyberTeal,
                title = "Identity & IDs",
                subtitle = "Passports, Aadhaar, PAN",
                count = idCount,
                onClick = { onCategoryClick("Identity") },
                modifier = Modifier.weight(1f)
            )
            CategoryGridCard(
                icon = Icons.Default.DirectionsBike,
                iconTint = QuantumIndigo,
                title = "Vehicles & Auto",
                subtitle = "RC, Insurance, PUC",
                count = vehCount,
                onClick = { onCategoryClick("Vehicles") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CategoryGridCard(
                icon = Icons.Default.HealthAndSafety,
                iconTint = CyberTeal,
                title = "Health & Insurance",
                subtitle = "Mediclaim, Health Cards",
                count = healthCount,
                onClick = { onCategoryClick("Insurance") },
                modifier = Modifier.weight(1f)
            )
            CategoryGridCard(
                icon = Icons.Default.HomeWork,
                iconTint = QuantumIndigo,
                title = "Property & Legal",
                subtitle = "Deeds, Tax Receipts, Wills",
                count = propCount,
                onClick = { onCategoryClick("Property") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CategoryGridCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainer)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHighest)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "$count",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PinnedAndRecentSection(
    documents: List<VaultDocumentEntity>,
    onViewDoc: (VaultDocumentEntity) -> Unit,
    onShareDoc: (VaultDocumentEntity) -> Unit,
    onViewAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PINNED & RECENT",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "View All (${documents.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTeal,
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            documents.forEach { doc ->
                PinnedDocItem(
                    document = doc,
                    onView = { onViewDoc(doc) },
                    onShare = { onShareDoc(doc) }
                )
            }
        }
    }
}

@Composable
private fun PinnedDocItem(
    document: VaultDocumentEntity,
    onView: () -> Unit,
    onShare: () -> Unit
) {
    val isUrgent = document.isActionNeeded || (document.daysRemaining != null && document.daysRemaining <= 30)

    val icon = when {
        document.name.contains("License", ignoreCase = true) -> Icons.Default.Badge
        document.name.contains("Insurance", ignoreCase = true) || document.name.contains("Bike", ignoreCase = true) -> Icons.Default.DirectionsBike
        document.name.contains("Health", ignoreCase = true) -> Icons.Default.HealthAndSafety
        else -> Icons.Default.HomeWork
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainer)
            .border(
                1.dp,
                if (isUrgent) AlertRed.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onView() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isUrgent) AlertRed else CyberTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = document.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${document.expiryDate} • ${document.memberName.split(" ").first()}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusPill(
                    status = document.status,
                    daysRemaining = document.daysRemaining
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onView,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "View",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
