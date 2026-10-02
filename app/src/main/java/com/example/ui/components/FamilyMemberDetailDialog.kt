package com.example.ui.components

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.CyberTealDark
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.QuantumIndigo
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FamilyMemberDetailDialog(
    initialMember: FamilyMemberEntity,
    allMembers: List<FamilyMemberEntity>,
    allDocuments: List<VaultDocumentEntity>,
    onViewDocument: (VaultDocumentEntity) -> Unit,
    onOpenMemberVault: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMember by remember { mutableStateOf(initialMember) }
    val memberDocs = allDocuments.filter { it.memberId == selectedMember.id }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = SurfaceContainerLow,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberTeal.copy(alpha = 0.35f)),
            modifier = Modifier.testTag("family_member_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Family Member Profile",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Access & Assigned Vault Records",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Switch Member Chips
                if (allMembers.size > 1) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allMembers) { m ->
                            val isSelected = m.id == selectedMember.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSelected) CyberTeal else SurfaceContainerHighest)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyberTeal else Color.White.copy(alpha = 0.12f),
                                        RoundedCornerShape(999.dp)
                                    )
                                    .clickable { selectedMember = m }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (m.relationship == "You") "${m.name} (You)" else m.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CyberTealDark else TextPrimary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Member Details Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHighest)
                                    .border(2.dp, CyberTeal, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedMember.avatarUrl != null) {
                                    AsyncImage(
                                        model = selectedMember.avatarUrl,
                                        contentDescription = selectedMember.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Text(
                                        text = selectedMember.initials,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberTeal
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = selectedMember.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(CyberTeal.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = selectedMember.relationship,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CyberTeal
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(QuantumIndigo.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = selectedMember.role,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = IndigoLight
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = CyberTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Access: ${selectedMember.accessPermission}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            if (selectedMember.isEmergencyContact) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContactEmergency,
                                        contentDescription = null,
                                        tint = CyberTeal,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Emergency Nominee",
                                        fontSize = 11.sp,
                                        color = CyberTeal,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Assigned Documents Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ASSIGNED DOCUMENTS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = "${memberDocs.size} Records",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberTeal
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Documents List
                if (memberDocs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLowest)
                            .padding(18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "No documents assigned yet",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                    ) {
                        items(memberDocs) { doc ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceContainerLowest)
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                    .clickable {
                                        onDismiss()
                                        onViewDocument(doc)
                                    }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = doc.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${doc.provider} • Ref: ${doc.policyOrIdNumber}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "Expires: ${doc.expiryDate}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        StatusPill(status = doc.status, daysRemaining = doc.daysRemaining)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = "View",
                                            tint = CyberTeal,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Floating 8D Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyberSecondaryButton3D(
                        text = "Close",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        height = 42.dp
                    )

                    CyberButton3D(
                        text = "Open Vault",
                        icon = Icons.Default.FolderOpen,
                        onClick = {
                            onDismiss()
                            onOpenMemberVault(selectedMember.id)
                        },
                        modifier = Modifier.weight(1f),
                        height = 42.dp
                    )
                }
            }
        }
    }
}
