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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.VaultDocumentEntity
import com.example.ui.components.CyberSecondaryButton3D
import com.example.ui.components.Modern8DFloatingBoxButton
import com.example.ui.components.StatusPill
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.QuantumIndigo
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun ElenaVaultScreen(
    viewModel: VaultViewModel,
    memberId: String = "organizer",
    onBack: () -> Unit,
    onAddDocument: (memberId: String, category: String?) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val allDocs by viewModel.allDocuments.collectAsStateWithLifecycle()
    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()
    val activeCategory by viewModel.memberCategoryFilter.collectAsStateWithLifecycle()

    val currentMember = allMembers.find { it.id == memberId } ?: allMembers.firstOrNull()
    val memberDocs = allDocs.filter { it.memberId == currentMember?.id }

    val categories = listOf("All" to memberDocs.size) +
        memberDocs.groupBy { it.category }
            .map { it.key to it.value.size }
            .sortedByDescending { it.second }

    val filteredDocs = if (activeCategory == "All") {
        memberDocs
    } else {
        memberDocs.filter { it.category.equals(activeCategory, ignoreCase = true) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CyberTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${currentMember?.name ?: "Member"}'s Vault",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = { viewModel.showToast("Exporting zero-knowledge archive package...") },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = CyberTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Profile Card (Notice: Top Add Document button has been removed as requested!)
                item {
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
                                // Avatar circle with initials badge
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHighest)
                                        .border(2.dp, CyberTeal, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!currentMember?.avatarUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = currentMember?.avatarUrl,
                                            contentDescription = currentMember?.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Text(
                                            text = currentMember?.name?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("") ?: "FM",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberTeal
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = currentMember?.name ?: "Family Member",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(QuantumIndigo.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = currentMember?.role ?: "Member",
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
                                            text = "${memberDocs.size} Documents • All Zero-Knowledge Encrypted",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            // Share action only at the top; Top Add Document button was removed
                            CyberSecondaryButton3D(
                                text = "Share Encrypted Archive",
                                icon = Icons.Default.FolderShared,
                                onClick = {
                                    viewModel.showToast("Preparing zero-knowledge export archive...")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                height = 40.dp,
                                testTag = "share_archive_button"
                            )
                        }
                    }
                }

                // Category Chips Row
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { (cat, count) ->
                            val isSelected = activeCategory.equals(cat, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSelected) CyberTeal else SurfaceContainer)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyberTeal else Color.White.copy(alpha = 0.1f),
                                        RoundedCornerShape(999.dp)
                                    )
                                    .clickable { viewModel.setMemberCategoryFilter(cat) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$cat ($count)",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF00382E) else TextPrimary
                                )
                            }
                        }
                    }
                }

                // Documents List
                if (filteredDocs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceContainer)
                                .border(1.dp, CyberTeal.copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No documents found in this section.",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    items(filteredDocs, key = { it.id }) { doc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceContainer)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                            .clickable { viewModel.viewDocument(doc) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CyberTeal.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = CyberTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = doc.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${doc.provider} • ID: ${doc.policyOrIdNumber}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Expires: ${doc.expiryDate} (In-App View)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = CyberTeal
                                )
                            }
                        }

                        StatusPill(status = doc.status, daysRemaining = doc.daysRemaining)
                    }
                }
            }

            // Bottom Add Document Button (Preserved and styled as Modern 8D Floating Box Button!)
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Modern8DFloatingBoxButton(
                    title = if (activeCategory != "All") "+ Add $activeCategory Document" else "+ Add to ${currentMember?.name?.split(" ")?.firstOrNull() ?: "Member"}'s Docs",
                    subtitle = if (activeCategory != "All") "Scan or upload $activeCategory with AI analysis" else "Secure optical or digital document upload",
                    icon = Icons.Default.Add,
                    onClick = {
                        onAddDocument(
                            currentMember?.id ?: "organizer",
                            if (activeCategory != "All") activeCategory else null
                        )
                    },
                    isPrimary = true,
                    testTag = "add_to_member_docs_button"
                )
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
}
