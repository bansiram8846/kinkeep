package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.CyberTealDark
import com.example.ui.theme.QuantumIndigo
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddFamilyMemberDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, relationship: String, permission: String, avatarUrl: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Child") }
    var permission by remember { mutableStateOf("Full Access") }
    var selectedAvatarUrl by remember { mutableStateOf<String?>(null) }
    var isRelDropdownOpen by remember { mutableStateOf(false) }

    // Android zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedAvatarUrl = uri.toString()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = SurfaceContainerLow,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberTeal.copy(alpha = 0.4f)),
            modifier = Modifier.testTag("add_family_member_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Add Family Member",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
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

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Upload Area
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        SurfaceContainerHighest,
                                        SurfaceContainerLowest
                                    )
                                )
                            )
                            .border(2.dp, CyberTeal, CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedAvatarUrl != null) {
                            AsyncImage(
                                model = selectedAvatarUrl,
                                contentDescription = "Member Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "No photo",
                                tint = CyberTeal,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    // Camera / Upload badge button
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(CyberTeal)
                            .border(2.dp, SurfaceContainerLow, CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Upload photo",
                            tint = CyberTealDark,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Tap circle or icon to pick photo from device",
                    fontSize = 11.sp,
                    color = CyberTeal,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Full Name input
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "FULL NAME",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("e.g. Jordan Morgan", color = TextMuted, fontSize = 13.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerLowest,
                            unfocusedContainerColor = SurfaceContainerLowest,
                            focusedBorderColor = CyberTeal,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_input_member_name")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Relationship Dropdown
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "RELATIONSHIP",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceContainerLowest)
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                            .clickable { isRelDropdownOpen = true }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = relationship, fontSize = 13.sp, color = TextPrimary)
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = isRelDropdownOpen,
                            onDismissRequest = { isRelDropdownOpen = false },
                            modifier = Modifier.background(SurfaceContainerHigh)
                        ) {
                            listOf("Spouse", "Child", "Parent", "Sibling", "Partner", "Guardian", "Pet", "Other").forEach { rel ->
                                DropdownMenuItem(
                                    text = { Text(rel, color = TextPrimary) },
                                    onClick = {
                                        relationship = rel
                                        isRelDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Permission Selector
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "ACCESS PERMISSION",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.6.sp
                    )

                    listOf(
                        Pair("Full Access", "Can view, add and share vault records"),
                        Pair("View Only", "Can view assigned documents only"),
                        Pair("Emergency Contact", "Access only during emergency sharing")
                    ).forEach { (perm, desc) ->
                        val isSelected = permission == perm
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyberTeal.copy(alpha = 0.1f) else SurfaceContainerLowest)
                                .border(
                                    1.dp,
                                    if (isSelected) CyberTeal else Color.White.copy(alpha = 0.08f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { permission = perm }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { permission = perm },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CyberTeal,
                                    unselectedColor = TextMuted
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = perm,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CyberTeal else TextPrimary
                                )
                                Text(
                                    text = desc,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 8D Tactile 3D Save Button
                CyberButton3D(
                    text = "Save & Add to Vault",
                    onClick = {
                        val finalName = name.trim().ifBlank { "Family Member" }
                        onSave(finalName, relationship, permission, selectedAvatarUrl)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "dialog_save_member_button"
                )
            }
        }
    }
}
