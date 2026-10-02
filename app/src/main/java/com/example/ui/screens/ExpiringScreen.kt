package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultDocumentEntity
import com.example.ui.components.CyberButton3D
import com.example.ui.components.CyberSecondaryButton3D
import com.example.ui.components.StatusPill
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.CyberTealDark
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun ExpiringScreen(
    viewModel: VaultViewModel,
    modifier: Modifier = Modifier
) {
    val allDocs by viewModel.allDocuments.collectAsState()
    val expiringDocs = allDocs.filter {
        it.isActionNeeded || (it.daysRemaining != null && it.daysRemaining <= 30) || it.status.equals("Expiring", ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Renewal Radar",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Automated proactive 30-day expiration monitoring",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (expiringDocs.isNotEmpty()) AlertRed.copy(alpha = 0.15f) else CyberTeal.copy(alpha = 0.15f))
                    .border(
                        1.dp,
                        if (expiringDocs.isNotEmpty()) AlertRed.copy(alpha = 0.4f) else CyberTeal.copy(alpha = 0.4f),
                        RoundedCornerShape(999.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (expiringDocs.isNotEmpty()) "${expiringDocs.size} Urgent (≤30d)" else "All Good",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (expiringDocs.isNotEmpty()) AlertRedLight else CyberTeal
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (expiringDocs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, CyberTeal.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = "All Documents Active",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Zero policies or credentials expiring in the next 30 days. KinKeep will automatically alert you when a document reaches the 30-day window.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(expiringDocs) { doc ->
                    ExpiringItemCard(
                        doc = doc,
                        onRenew = { viewModel.renewPolicy(doc) },
                        onView = { viewModel.viewDocument(doc) }
                    )
                }
            }

            item {
                // Info block
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = CyberTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Smart Expiry Reminders Active",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "KinKeep scans document renewal windows 30 days and 7 days prior.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ExpiringItemCard(
    doc: VaultDocumentEntity,
    onRenew: () -> Unit,
    onView: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainer)
            .border(1.dp, AlertRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doc.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${doc.provider} • Assigned to ${doc.memberName.split(" ").first()}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                StatusPill(
                    status = doc.status,
                    daysRemaining = doc.daysRemaining
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerLowest)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "POLICY NUMBER",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = doc.policyOrIdNumber,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "EXPIRATION",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = doc.expiryDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRedLight
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CyberSecondaryButton3D(
                    text = "View Details",
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    height = 40.dp
                )

                CyberButton3D(
                    text = "Renew Policy",
                    icon = Icons.Default.Autorenew,
                    onClick = onRenew,
                    modifier = Modifier.weight(1f),
                    height = 40.dp
                )
            }
        }
    }
}
