package com.example.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.data.DefaultVaultData
import com.example.data.model.FamilyMemberEntity
import com.example.ui.components.AddFamilyMemberDialog
import com.example.ui.components.CyberButton3D
import com.example.ui.components.CyberIconButton3D
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.CyberTealBright
import com.example.ui.theme.CyberTealDark
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.QuantumIndigo
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VaultViewModel

@Composable
fun FamilyScreen(
    viewModel: VaultViewModel,
    onOpenMemberVault: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val members by viewModel.allMembers.collectAsState()
    val allDocs by viewModel.allDocuments.collectAsState()

    var selectedMemberId by remember { mutableStateOf("elena") }
    val currentSelectedMember = members.find { it.id == selectedMemberId } ?: members.firstOrNull()

    // Dialog state
    var isAddMemberDialogOpen by remember { mutableStateOf(false) }

    // Inline form state
    var newMemberName by remember { mutableStateOf("") }
    var selectedRelationship by remember { mutableStateOf("Child") }
    var selectedPermission by remember { mutableStateOf("Full Access") }
    var uploadedAvatarUrl by remember { mutableStateOf<String?>(null) }

    var isRelationshipMenuOpen by remember { mutableStateOf(false) }
    var isMemberPickerOpen by remember { mutableStateOf(false) }

    // Photo picker for inline form
    val inlinePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadedAvatarUrl = uri.toString()
        }
    }

    val fullOrganizersCount = members.count { it.role.contains("Organizer", ignoreCase = true) }

    // Presets for quick inline avatar selection
    val inlinePresets = remember {
        listOf(
            Pair("Elena", DefaultVaultData.ELENA_AVATAR_URL),
            Pair("Jordan", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=256&q=80"),
            Pair("Leo", "https://images.unsplash.com/photo-1543610892-0b1f7e6d8ac1?auto=format&fit=crop&w=256&q=80"),
            Pair("Maya", "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=256&q=80"),
            Pair("Grandparent", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=256&q=80")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        imageVector = Icons.Default.Diversity3,
                        contentDescription = "KinKeep",
                        tint = CyberTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "KinKeep",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(CyberTeal)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Secure Family Vault",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // 8D Tactile Add Member Action Button
            CyberIconButton3D(
                icon = Icons.Default.PersonAdd,
                contentDescription = "Add Member",
                isPrimary = true,
                size = 38.dp,
                onClick = { isAddMemberDialogOpen = true },
                testTag = "top_add_member_button"
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with badge chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Family Members",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Manage family profiles, access and emergency sharing",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(CyberTeal.copy(alpha = 0.15f))
                                .border(1.dp, CyberTeal.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${members.size} Members",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(SurfaceContainerHighest)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${allDocs.size} Docs",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // 4 Stats in 2x2 grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FamilyStatItem(
                            icon = Icons.Default.Diversity3,
                            count = "${members.size}",
                            label = "Family Members",
                            modifier = Modifier.weight(1f)
                        )
                        FamilyStatItem(
                            icon = Icons.Default.Folder,
                            count = "${allDocs.size}",
                            label = "Total Documents",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FamilyStatItem(
                            icon = Icons.Default.Security,
                            count = "$fullOrganizersCount",
                            label = "Full Organizers",
                            modifier = Modifier.weight(1f)
                        )
                        FamilyStatItem(
                            icon = Icons.Default.ContactEmergency,
                            count = "Active",
                            label = "Emergency Contact",
                            isStatus = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // SELECT FAMILY MEMBER (Dropdown & Link)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECT FAMILY MEMBER",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = "${members.size} Profiles",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            // Member Picker Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerLowest)
                                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                    .clickable { isMemberPickerOpen = true }
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Avatar circle
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(SurfaceContainerHighest)
                                                .border(1.5.dp, CyberTeal, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (currentSelectedMember?.avatarUrl != null) {
                                                AsyncImage(
                                                    model = currentSelectedMember.avatarUrl,
                                                    contentDescription = currentSelectedMember.name,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Text(
                                                    text = currentSelectedMember?.initials ?: "FM",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CyberTeal
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = "${currentSelectedMember?.name ?: "Family Member"} (${currentSelectedMember?.role ?: "Organizer"})",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                    }

                                    Icon(
                                        imageVector = if (isMemberPickerOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = isMemberPickerOpen,
                                    onDismissRequest = { isMemberPickerOpen = false },
                                    modifier = Modifier.background(SurfaceContainerHigh)
                                ) {
                                    members.forEach { m ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(text = "${m.name} (${m.role})", color = TextPrimary)
                                                }
                                            },
                                            onClick = {
                                                selectedMemberId = m.id
                                                isMemberPickerOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Sub status row with link to Documents
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
                                        text = "${currentSelectedMember?.role ?: "Co-Organizer"} • Active",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            onOpenMemberVault(currentSelectedMember?.id ?: "elena")
                                        }
                                        .padding(4.dp)
                                        .testTag("view_member_docs_link")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = CyberTeal,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "View ${currentSelectedMember?.name?.split(" ")?.firstOrNull() ?: "Elena"}'s Documents >",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberTeal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ADD FAMILY MEMBER CARD (with Image Upload & 8D Button)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceContainer)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
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
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(CyberTeal.copy(alpha = 0.15f))
                                    .border(1.dp, CyberTeal.copy(alpha = 0.3f), RoundedCornerShape(999.dp))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Instant Setup",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CyberTeal
                                )
                            }
                        }

                        // Avatar photo upload row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerLowest)
                                        .border(2.dp, CyberTeal, CircleShape)
                                        .clickable {
                                            inlinePhotoPicker.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uploadedAvatarUrl != null) {
                                        AsyncImage(
                                            model = uploadedAvatarUrl,
                                            contentDescription = "Avatar",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = CyberTeal,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(CyberTeal)
                                        .clickable {
                                            inlinePhotoPicker.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = "Upload",
                                        tint = CyberTealDark,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Upload Member Image",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Tap circle to pick photo or select preset below",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Preset avatar pills
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(inlinePresets) { (label, url) ->
                                val isSelected = uploadedAvatarUrl == url
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHighest)
                                        .border(
                                            1.5.dp,
                                            if (isSelected) CyberTeal else Color.White.copy(alpha = 0.15f),
                                            CircleShape
                                        )
                                        .clickable {
                                            uploadedAvatarUrl = url
                                            if (newMemberName.isBlank()) {
                                                newMemberName = label
                                            }
                                        }
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(CyberTeal.copy(alpha = 0.35f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Full Name
                        Column {
                            Text(
                                text = "FULL NAME",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = newMemberName,
                                onValueChange = { newMemberName = it },
                                placeholder = { Text("e.g. Jordan Morgan", color = TextMuted, fontSize = 13.sp) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
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
                                    .testTag("input_new_member_name")
                            )
                        }

                        // Relationship
                        Column {
                            Text(
                                text = "RELATIONSHIP",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
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
                                    .clickable { isRelationshipMenuOpen = true }
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedRelationship,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = isRelationshipMenuOpen,
                                    onDismissRequest = { isRelationshipMenuOpen = false },
                                    modifier = Modifier.background(SurfaceContainerHigh)
                                ) {
                                    listOf("Spouse", "Child", "Parent", "Sibling", "Partner", "Guardian", "Pet", "Other").forEach { rel ->
                                        DropdownMenuItem(
                                            text = { Text(text = rel, color = TextPrimary) },
                                            onClick = {
                                                selectedRelationship = rel
                                                isRelationshipMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Access Permission Radio Options
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "ACCESS PERMISSION",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.6.sp
                            )

                            PermissionRadioItem(
                                title = "Full Access",
                                subtitle = "Can view, add and share vault records",
                                isSelected = selectedPermission == "Full Access",
                                onSelect = { selectedPermission = "Full Access" }
                            )

                            PermissionRadioItem(
                                title = "View Only",
                                subtitle = "Can view assigned documents only",
                                isSelected = selectedPermission == "View Only",
                                onSelect = { selectedPermission = "View Only" }
                            )

                            PermissionRadioItem(
                                title = "Emergency Contact",
                                subtitle = "Access only during emergency sharing",
                                isSelected = selectedPermission == "Emergency Contact",
                                onSelect = { selectedPermission = "Emergency Contact" }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // 8D Tactile 3D Save Button
                        CyberButton3D(
                            text = "Save Family Profile",
                            icon = Icons.Default.PersonAdd,
                            onClick = {
                                if (newMemberName.isNotBlank()) {
                                    viewModel.addFamilyMember(
                                        name = newMemberName,
                                        relationship = selectedRelationship,
                                        accessPermission = selectedPermission,
                                        avatarUrl = uploadedAvatarUrl
                                    )
                                    newMemberName = ""
                                    uploadedAvatarUrl = null
                                } else {
                                    viewModel.showToast("Please enter a member name")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "save_member_button"
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Add Family Member Modal Dialog
        if (isAddMemberDialogOpen) {
            AddFamilyMemberDialog(
                onDismiss = { isAddMemberDialogOpen = false },
                onSave = { name, rel, perm, avatarUrl ->
                    viewModel.addFamilyMember(name, rel, perm, avatarUrl)
                    selectedMemberId = name.lowercase().replace(" ", "_").take(10)
                }
            )
        }
    }
}

@Composable
private fun FamilyStatItem(
    icon: ImageVector,
    count: String,
    label: String,
    isStatus: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainer)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyberTeal,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = count,
                fontSize = if (isStatus) 18.sp else 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun PermissionRadioItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceContainerLowest)
            .border(
                1.dp,
                if (isSelected) CyberTeal else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = CyberTeal,
                    unselectedColor = Color.White.copy(alpha = 0.3f)
                )
            )
        }
    }
}
