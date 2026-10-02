package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.VaultDocumentEntity
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.CyberTealDark
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DocumentSelectorDialog(
    allDocuments: List<VaultDocumentEntity>,
    initialCategory: String = "All",
    onSelectDocument: (VaultDocumentEntity) -> Unit,
    onAddNewDocument: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    val categories = listOf("All", "Identity", "Vehicles", "Insurance", "Property")

    val filteredDocs = when (selectedCategory) {
        "Identity" -> allDocuments.filter { it.category.contains("Identity", ignoreCase = true) }
        "Vehicles" -> allDocuments.filter { it.category.contains("Vehicles", ignoreCase = true) }
        "Insurance" -> allDocuments.filter { it.category.contains("Insurance", ignoreCase = true) || it.name.contains("Insurance", ignoreCase = true) }
        "Property" -> allDocuments.filter { it.category.contains("Property", ignoreCase = true) }
        else -> allDocuments
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = SurfaceContainerLow,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberTeal.copy(alpha = 0.35f)),
            modifier = Modifier.testTag("document_selector_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
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
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Select & View Document",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Tap any record to decrypt and view",
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

                // Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                        val count = when (cat) {
                            "Identity" -> allDocuments.count { it.category.contains("Identity", ignoreCase = true) }
                            "Vehicles" -> allDocuments.count { it.category.contains("Vehicles", ignoreCase = true) }
                            "Insurance" -> allDocuments.count { it.category.contains("Insurance", ignoreCase = true) || it.name.contains("Insurance", ignoreCase = true) }
                            "Property" -> allDocuments.count { it.category.contains("Property", ignoreCase = true) }
                            else -> allDocuments.size
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (isSelected) CyberTeal else SurfaceContainerHighest)
                                .border(
                                    1.dp,
                                    if (isSelected) CyberTeal else Color.White.copy(alpha = 0.12f),
                                    RoundedCornerShape(999.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$cat ($count)",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CyberTealDark else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Documents List
                if (filteredDocs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLowest)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "No documents found",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "No documents in $selectedCategory category yet. Tap below to upload or scan a new document.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                    ) {
                        items(filteredDocs) { doc ->
                            val isUrgent = doc.isActionNeeded || (doc.daysRemaining != null && doc.daysRemaining <= 30)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerLowest)
                                    .border(
                                        1.dp,
                                        if (isUrgent) AlertRed.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        onDismiss()
                                        onSelectDocument(doc)
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = doc.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            if (doc.requireBiometric) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = "Encrypted",
                                                    tint = CyberTeal,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${doc.memberName} • ${doc.provider}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                        Text(
                                            text = "Ref: ${doc.policyOrIdNumber} • Exp: ${doc.expiryDate}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = if (isUrgent) AlertRedLight else TextMuted
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        StatusPill(status = doc.status, daysRemaining = doc.daysRemaining)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(CyberTeal.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = "Open Document",
                                                tint = CyberTeal,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Floating 8D Buttons
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
                        text = "+ Add Document",
                        icon = Icons.Default.Add,
                        onClick = {
                            onDismiss()
                            onAddNewDocument()
                        },
                        modifier = Modifier.weight(1.2f),
                        height = 42.dp
                    )
                }
            }
        }
    }
}
