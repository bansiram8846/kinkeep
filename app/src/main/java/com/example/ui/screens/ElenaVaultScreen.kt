package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DefaultVaultData
import com.example.data.model.VaultDocumentEntity
import com.example.ui.components.CyberButton3D
import com.example.ui.components.CyberSecondaryButton3D
import com.example.ui.components.StatusPill
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.CyberTealDark
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.QuantumIndigo
import com.example.ui.theme.SurfaceBright
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun ElenaVaultScreen(
    viewModel: VaultViewModel,
    onBack: () -> Unit,
    onAddDocument: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val uiState by viewModel.uiState.collectAsState()
    val allDocs by viewModel.allDocuments.collectAsState()
    val members by viewModel.allMembers.collectAsState()

    val currentMember = members.find { it.id == uiState.selectedMemberId } ?: members.find { it.id == "elena" }
    val memberDocs = allDocs.filter { it.memberId == (currentMember?.id ?: "elena") }

    val activeCategory = uiState.memberCategoryFilter

    val filteredDocs = when (activeCategory) {
        "Vehicles" -> memberDocs.filter { it.category.contains("Vehicles", ignoreCase = true) }
        "Insurance" -> memberDocs.filter { it.category.contains("Insurance", ignoreCase = true) || it.name.contains("Insurance", ignoreCase = true) }
        "Identity" -> memberDocs.filter { it.category.contains("Identity", ignoreCase = true) }
        "Property" -> memberDocs.filter { it.category.contains("Property", ignoreCase = true) }
        else -> memberDocs
    }

    val categories = listOf(
        Pair("All", memberDocs.size),
        Pair("Vehicles", memberDocs.count { it.category.contains("Vehicles", ignoreCase = true) }),
        Pair("Insurance", memberDocs.count { it.category.contains("Insurance", ignoreCase = true) || it.name.contains("Insurance", ignoreCase = true) }),
        Pair("Identity", memberDocs.count { it.category.contains("Identity", ignoreCase = true) }),
        Pair("Property", memberDocs.count { it.category.contains("Property", ignoreCase = true) })
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
    ) {
        // Top App Bar
        TopVaultHeader(
            memberName = currentMember?.name ?: "Elena Morgan",
            onBack = onBack,
            onShareAll = {
                viewModel.showToast("Exporting encrypted archive for ${currentMember?.name ?: "Elena"}")
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                MemberProfileCard(
                    name = currentMember?.name ?: "Elena Morgan",
                    role = currentMember?.role ?: "Co-Organizer",
                    avatarUrl = currentMember?.avatarUrl ?: DefaultVaultData.ELENA_AVATAR_URL,
                    docCount = memberDocs.size,
                    onShareAll = {
                        viewModel.showToast("Preparing zero-knowledge share package...")
                    },
                    onAddDoc = {
                        onAddDocument(currentMember?.id ?: "elena")
                    }
                )
            }

            // Filter Chips
            item {
                CategoryChipsRow(
                    categories = categories,
                    selectedCategory = activeCategory,
                    onSelectCategory = { viewModel.setMemberCategoryFilter(it) }
                )
            }

            // Empty state if no documents
            if (memberDocs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, CyberTeal.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderShared,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No Documents in this Vault Yet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tap below to add ${currentMember?.name?.split(" ")?.firstOrNull() ?: "member"}'s first verified document",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Document Cards
            items(filteredDocs, key = { it.id }) { doc ->
                ElenaDocumentCard(
                    document = doc,
                    onView = { viewModel.viewDocument(doc) },
                    onShare = { viewModel.openShare(doc) },
                    onRenew = { viewModel.renewPolicy(doc) }
                )
            }

            // Bottom "+ Add to Elena's Docs" button
            item {
                Spacer(modifier = Modifier.height(4.dp))
                CyberButton3D(
                    text = "+ Add to ${currentMember?.name?.split(" ")?.firstOrNull() ?: "Elena"}'s Docs",
                    icon = Icons.Default.AddCircle,
                    onClick = { onAddDocument(currentMember?.id ?: "elena") },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "add_to_member_docs_button"
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun TopVaultHeader(
    memberName: String,
    onBack: () -> Unit,
    onShareAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF10131A))
            .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Vault",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = " / ",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Text(
                        text = memberName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = CyberTeal
                    )
                }
                Text(
                    text = "${memberName.split(" ").firstOrNull() ?: "Elena"}'s Vault",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onShareAll,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share vault",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = onShareAll,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MemberProfileCard(
    name: String,
    role: String,
    avatarUrl: String,
    docCount: Int,
    onShareAll: () -> Unit,
    onAddDoc: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceContainer)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar with online / verified ring
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(2.dp, CyberTeal, CircleShape)
                        .background(SurfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(CyberTeal)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(QuantumIndigo.copy(alpha = 0.2f))
                                .border(1.dp, QuantumIndigo.copy(alpha = 0.4f), RoundedCornerShape(999.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = role,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = IndigoLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = CyberTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "$docCount Documents • All verified",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Row with 8D tactile buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CyberSecondaryButton3D(
                    text = "Share All Docs",
                    icon = Icons.Default.FolderShared,
                    onClick = onShareAll,
                    modifier = Modifier.weight(1f),
                    height = 40.dp,
                    testTag = "share_all_docs_button"
                )

                CyberButton3D(
                    text = "Add Document",
                    icon = Icons.Default.Add,
                    onClick = onAddDoc,
                    modifier = Modifier.weight(1f),
                    height = 40.dp,
                    testTag = "add_document_member_button"
                )
            }
        }
    }
}

@Composable
private fun CategoryChipsRow(
    categories: List<Pair<String, Int>>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { (cat, count) ->
            val isSelected = selectedCategory.equals(cat, ignoreCase = true)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isSelected) CyberTeal else SurfaceContainer)
                    .border(
                        1.dp,
                        if (isSelected) CyberTeal else Color.White.copy(alpha = 0.12f),
                        RoundedCornerShape(999.dp)
                    )
                    .clickable { onSelectCategory(cat) }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cat,
                    color = if (isSelected) CyberTealDark else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isSelected) CyberTealDark.copy(alpha = 0.2f) else SurfaceContainerHighest)
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "$count",
                        color = if (isSelected) CyberTealDark else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ElenaDocumentCard(
    document: VaultDocumentEntity,
    onView: () -> Unit,
    onShare: () -> Unit,
    onRenew: () -> Unit
) {
    val isUrgent = document.isActionNeeded || (document.daysRemaining != null && document.daysRemaining <= 30)

    val icon: ImageVector = when {
        document.name.contains("License", ignoreCase = true) || document.category.contains("Identity", ignoreCase = true) -> Icons.Default.Badge
        document.name.contains("Duke", ignoreCase = true) || document.name.contains("Bike", ignoreCase = true) || document.category.contains("Vehicles", ignoreCase = true) -> Icons.Default.DirectionsBike
        document.name.contains("Health", ignoreCase = true) || document.category.contains("Health", ignoreCase = true) -> Icons.Default.HealthAndSafety
        document.name.contains("Deed", ignoreCase = true) || document.category.contains("Property", ignoreCase = true) -> Icons.Default.HomeWork
        else -> Icons.AutoMirrored.Filled.Help
    }

    val iconTint = when {
        document.name.contains("Deed", ignoreCase = true) -> QuantumIndigo
        isUrgent -> CyberTeal
        else -> CyberTeal
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainer)
            .border(
                width = 1.dp,
                color = if (isUrgent) AlertRed.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerHighest)
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = document.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = document.provider,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                StatusPill(
                    status = document.status,
                    daysRemaining = document.daysRemaining
                )
            }

            // Center details box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val label = when {
                            document.name.contains("License", ignoreCase = true) -> "LICENSE NO"
                            document.name.contains("Insurance", ignoreCase = true) -> "POLICY NO"
                            document.name.contains("Health", ignoreCase = true) -> "POLICY REF"
                            document.name.contains("Deed", ignoreCase = true) -> "REGISTRY REF"
                            else -> "DOCUMENT ID"
                        }
                        Text(
                            text = label,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = document.policyOrIdNumber,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        val rightLabel = when {
                            isUrgent -> "⚠️ EXPIRY ALERT"
                            else -> "EXPIRES"
                        }
                        val rightValue = when {
                            document.daysRemaining != null && document.daysRemaining in 0..30 -> "${document.daysRemaining}d left (${document.expiryDate})"
                            document.daysRemaining != null && document.daysRemaining < 0 -> "Expired (${document.expiryDate})"
                            else -> document.expiryDate
                        }

                        Text(
                            text = rightLabel,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUrgent) AlertRedLight else TextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = rightValue,
                            fontSize = 13.sp,
                            fontWeight = if (isUrgent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isUrgent) AlertRedLight else TextPrimary
                        )
                    }
                }
            }

            // Bottom Actions row
            when {
                isUrgent -> {
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
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Action needed soon",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = onRenew,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberTeal,
                                contentColor = CyberTealDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("renew_policy_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Renew Policy",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                document.name.contains("Health", ignoreCase = true) -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = document.expiryDate,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onView() }
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Download Card",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyberTeal
                            )
                        }
                    }
                }
                document.name.contains("Deed", ignoreCase = true) -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = QuantumIndigo,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = document.sharedNotes ?: "Shared with Co-Organizer",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Text(
                            text = "View Deed",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = QuantumIndigo,
                            modifier = Modifier
                                .clickable { onView() }
                                .padding(4.dp)
                        )
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onView,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceContainerHigh,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "View Document", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = onShare,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceContainerHigh,
                                contentColor = TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Share", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
