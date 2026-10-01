package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.ExpirationUtils
import com.example.ui.components.AddFamilyMemberDialog
import com.example.ui.components.CyberButton3D
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.CyberTealBright
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
fun UploadDocumentScreen(
    viewModel: VaultViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onClose()
    }

    val members by viewModel.allMembers.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedMemberId by remember { mutableStateOf(uiState.selectedMemberId) }
    var selectedCategory by remember { mutableStateOf("Vehicle Insurance") }

    // Dynamic Categories list with ability to create new ones
    val categories = remember {
        mutableStateListOf(
            Pair("Driving License", Icons.Default.Badge),
            Pair("Vehicle Insurance", Icons.Default.DirectionsBike),
            Pair("Health Insurance", Icons.Default.HealthAndSafety),
            Pair("Property & Deed", Icons.Default.HomeWork),
            Pair("Passport & ID", Icons.Default.PermIdentity),
            Pair("Other Document", Icons.Default.Description)
        )
    }

    var showNewCategoryField by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    // Dialog state for adding a family member directly from upload screen
    var isAddMemberDialogOpen by remember { mutableStateOf(false) }

    // Attached document file
    var attachedFileUri by remember { mutableStateOf<String?>(null) }
    var attachedFileName by remember { mutableStateOf<String?>(null) }
    var attachedFileSize by remember { mutableStateOf<String?>("3.4 MB PDF") }

    // Photo/File Picker
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedFileUri = uri.toString()
            attachedFileName = "vault_scan_${System.currentTimeMillis().toString().takeLast(4)}.jpg"
            attachedFileSize = "2.8 MB (Encrypted)"
            viewModel.showToast("Document image encrypted and attached!")
        }
    }

    var docName by remember { mutableStateOf("Vehicle Insurance") }
    var docNumber by remember { mutableStateOf("POL-99201938-B") }
    var provider by remember { mutableStateOf("Insurance Provider") }
    var expiryTimestamp by remember { mutableStateOf<Long?>(ExpirationUtils.getTimestampAfterDays(365)) }
    var expiryDate by remember { mutableStateOf(ExpirationUtils.formatDate(ExpirationUtils.getTimestampAfterDays(365))) }
    var reminderDaysBefore by remember { mutableStateOf(30) }

    var remindExpiry by remember { mutableStateOf(true) }
    var requireBiometric by remember { mutableStateOf(false) }

    val tags = remember { mutableStateListOf("#vehicle", "#insurance") }
    var newTagInput by remember { mutableStateOf("") }
    var showTagField by remember { mutableStateOf(false) }

    val currentMember = members.find { it.id == selectedMemberId } ?: members.firstOrNull()

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
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "KinKeep",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTeal
                )
                Text(text = " • ", color = TextMuted)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(CyberTeal.copy(alpha = 0.12f))
                        .border(1.dp, CyberTeal.copy(alpha = 0.3f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = CyberTeal,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Encrypted & Private",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CyberTeal
                        )
                    }
                }
            }

            IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stepper
            item {
                StepperHeader()
            }

            // Upload Box / Attached file card
            item {
                if (attachedFileUri != null || attachedFileName != null) {
                    // Attached File preview card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, CyberTeal.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
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
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceContainerLowest)
                                        .border(1.dp, CyberTeal.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (attachedFileUri != null) {
                                        AsyncImage(
                                            model = attachedFileUri,
                                            contentDescription = "Document Scan",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = CyberTeal,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = CyberTeal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Encrypted File Attached",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberTeal
                                        )
                                    }
                                    Text(
                                        text = attachedFileName ?: "Document File",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = attachedFileSize ?: "2.8 MB",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    attachedFileUri = null
                                    attachedFileName = null
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove file",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                } else {
                    AddFileArea(
                        onScanClick = {
                            docPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onUploadClick = {
                            docPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onDemoScan = {
                            attachedFileName = "insurance_certificate_scan.pdf"
                            attachedFileSize = "3.2 MB PDF"
                            docName = "Policy Document"
                            viewModel.showToast("Optical scan simulated • File attached")
                        }
                    )
                }
            }

            // Assign to Family Member
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PermIdentity,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ASSIGN TO FAMILY MEMBER",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.6.sp
                            )
                        }

                        Text(
                            text = "${currentMember?.name?.split(" ")?.firstOrNull() ?: "Member"} Selected",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(members) { member ->
                            val isSelected = member.id == selectedMemberId
                            MemberAvatarItem(
                                member = member,
                                isSelected = isSelected,
                                onClick = { selectedMemberId = member.id }
                            )
                        }

                        // Add new member directly!
                        item {
                            AddMemberMiniItem(
                                onClick = {
                                    isAddMemberDialogOpen = true
                                }
                            )
                        }
                    }
                }
            }

            // Document Category Selector & Create Custom Type
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DOCUMENT CATEGORY",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.6.sp
                        )

                        Text(
                            text = "+ Create Custom Type",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            modifier = Modifier.clickable { showNewCategoryField = !showNewCategoryField }
                        )
                    }

                    if (showNewCategoryField) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FormInput(
                                value = newCategoryName,
                                onValueChange = { newCategoryName = it },
                                placeholder = "e.g. Tax Returns, Pet Medical, Academic",
                                modifier = Modifier.weight(1f),
                                testTag = "input_custom_category"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newCategoryName.isNotBlank()) {
                                        val trimmed = newCategoryName.trim()
                                        categories.add(Pair(trimmed, Icons.Default.Description))
                                        selectedCategory = trimmed
                                        newCategoryName = ""
                                        showNewCategoryField = false
                                        viewModel.showToast("Created type \"$trimmed\"")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberTeal,
                                    contentColor = CyberTealDark
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Text("Add", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in categories.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CategorySelectButton(
                                    label = categories[i].first,
                                    icon = categories[i].second,
                                    isSelected = selectedCategory == categories[i].first,
                                    onClick = { selectedCategory = categories[i].first },
                                    modifier = Modifier.weight(1f)
                                )
                                if (i + 1 < categories.size) {
                                    CategorySelectButton(
                                        label = categories[i + 1].first,
                                        icon = categories[i + 1].second,
                                        isSelected = selectedCategory == categories[i + 1].first,
                                        onClick = { selectedCategory = categories[i + 1].first },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Document Details Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceContainer)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = CyberTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Document Details",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "Encrypted Record",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        // Document Name
                        Column {
                            Text(
                                text = "Document Name",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FormInput(
                                value = docName,
                                onValueChange = { docName = it },
                                testTag = "input_doc_name"
                            )
                        }

                        // Policy / ID Number
                        Column {
                            Text(
                                text = "Policy / ID / Registration Number",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FormInput(
                                value = docNumber,
                                onValueChange = { docNumber = it },
                                testTag = "input_policy_number"
                            )
                        }

                        // Provider / Authority
                        Column {
                            Text(
                                text = "Provider / Issuing Authority",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FormInput(
                                value = provider,
                                onValueChange = { provider = it },
                                testTag = "input_provider"
                            )
                        }

                        // Expiry Date Field & Interactive Presets
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "EXPIRY DATE",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 0.6.sp
                                )

                                val calculatedDays = if (expiryTimestamp != null) {
                                    ExpirationUtils.calculateDaysRemaining(expiryTimestamp!!)
                                } else null

                                if (calculatedDays != null) {
                                    Text(
                                        text = if (calculatedDays < 0) "Expired" else "Expires in $calculatedDays days",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (calculatedDays <= 30) AlertRedLight else CyberTeal
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = {
                                    expiryDate = it
                                    val parsed = ExpirationUtils.parseDate(it)
                                    if (parsed != null) {
                                        expiryTimestamp = parsed
                                    }
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Pick date",
                                        tint = CyberTeal,
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
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Quick Presets Row
                            Text(
                                text = "QUICK EXPIRY PRESETS",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TextMuted
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item {
                                    // 18 days preset for testing 30-day alert immediately
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AlertRed.copy(alpha = 0.15f))
                                            .border(1.dp, AlertRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                val ts = ExpirationUtils.getTimestampAfterDays(18)
                                                expiryTimestamp = ts
                                                expiryDate = ExpirationUtils.formatDate(ts)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "⚡ +18 Days (30d Alert)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AlertRedLight
                                        )
                                    }
                                }

                                item {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SurfaceContainerHighest)
                                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                val ts = ExpirationUtils.getTimestampAfterDays(30)
                                                expiryTimestamp = ts
                                                expiryDate = ExpirationUtils.formatDate(ts)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(text = "+30 Days", fontSize = 11.sp, color = TextPrimary)
                                    }
                                }

                                item {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SurfaceContainerHighest)
                                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                val ts = ExpirationUtils.getTimestampAfterDays(180)
                                                expiryTimestamp = ts
                                                expiryDate = ExpirationUtils.formatDate(ts)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(text = "+6 Months", fontSize = 11.sp, color = TextPrimary)
                                    }
                                }

                                item {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SurfaceContainerHighest)
                                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                val ts = ExpirationUtils.getTimestampAfterDays(365)
                                                expiryTimestamp = ts
                                                expiryDate = ExpirationUtils.formatDate(ts)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(text = "+1 Year", fontSize = 11.sp, color = TextPrimary)
                                    }
                                }

                                item {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SurfaceContainerHighest)
                                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                val ts = ExpirationUtils.getTimestampAfterDays(1825)
                                                expiryTimestamp = ts
                                                expiryDate = ExpirationUtils.formatDate(ts)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(text = "+5 Years", fontSize = 11.sp, color = TextPrimary)
                                    }
                                }
                            }

                            // Live Visual Alert Banner preview
                            val calculatedDaysRemaining = if (expiryTimestamp != null) {
                                ExpirationUtils.calculateDaysRemaining(expiryTimestamp!!)
                            } else null

                            if (calculatedDaysRemaining != null && calculatedDaysRemaining in -999..30) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF3B151C))
                                        .border(1.dp, AlertRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = AlertRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "⚠️ 30-Day Alert Trigger: Document expires in $calculatedDaysRemaining days! Will activate urgent radar banner & notification bell.",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AlertRedLight
                                        )
                                    }
                                }
                            }
                        }

                        // 30-Day Early Expiry Alert switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerLowest)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (remindExpiry) AlertRed.copy(alpha = 0.15f) else CyberTeal.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = if (remindExpiry) AlertRed else CyberTeal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "30-Day Early Expiration Alert",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Proactively alerts you on the Renewal Radar & Top Alert Bell 30 days prior",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Switch(
                                checked = remindExpiry,
                                onCheckedChange = { remindExpiry = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberTealDark,
                                    checkedTrackColor = CyberTeal,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = SurfaceContainerHighest
                                )
                            )
                        }

                        // Require Biometric / PIN
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerLowest)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyberTeal.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = null,
                                        tint = CyberTeal,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Require Biometric / PIN",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Extra privacy lock to view this document",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Switch(
                                checked = requireBiometric,
                                onCheckedChange = { requireBiometric = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberTealDark,
                                    checkedTrackColor = CyberTeal,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = SurfaceContainerHighest
                                )
                            )
                        }
                    }
                }
            }

            // 8D Tactile Save Button
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = CyberTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Protected by KinKeep End-to-End Encryption",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = CyberTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 8D Tactile Button
                    CyberButton3D(
                        text = "Save to Vault",
                        icon = Icons.Default.Lock,
                        onClick = {
                            viewModel.saveDocument(
                                name = docName,
                                category = selectedCategory,
                                provider = provider,
                                docNumber = docNumber,
                                memberId = selectedMemberId,
                                expiryDate = expiryDate,
                                remindExpiry = remindExpiry,
                                requireBiometric = requireBiometric,
                                tags = tags.toList(),
                                fileUri = attachedFileUri,
                                fileSizeText = attachedFileSize,
                                expiryTimestamp = expiryTimestamp,
                                reminderDaysBefore = reminderDaysBefore
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "save_to_vault_button"
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // Add Member Modal Dialog
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
private fun StepperHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STEP 2 OF 3",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "Details",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = CyberTeal
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(CyberTeal)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(CyberTeal)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(SurfaceContainerHighest)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "✓ 1. Upload",
                fontSize = 11.sp,
                color = CyberTeal,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "• 2. Details",
                fontSize = 11.sp,
                color = CyberTeal,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "3. Done",
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun AddFileArea(
    onScanClick: () -> Unit,
    onUploadClick: () -> Unit,
    onDemoScan: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(CyberTeal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = CyberTeal,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Add Document File",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Supports PDF, JPG, PNG up to 50MB",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onScanClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberTeal,
                        contentColor = CyberTealDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("scan_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Scan with Camera", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onUploadClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceContainerHigh,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("upload_file_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Upload File / PDF", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Text(
                text = "Or click to attach simulated scan sample",
                fontSize = 11.sp,
                color = CyberTeal,
                modifier = Modifier.clickable { onDemoScan() }
            )
        }
    }
}

@Composable
private fun MemberAvatarItem(
    member: com.example.data.model.FamilyMemberEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceContainerHigh)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) CyberTeal else Color.White.copy(alpha = 0.1f),
                    shape = CircleShape
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
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) CyberTeal else TextSecondary
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(CyberTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = CyberTealDark,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (member.id == "alex") "Alex" else member.name.split(" ").first(),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) CyberTeal else TextPrimary
        )

        Text(
            text = member.relationship,
            fontSize = 10.sp,
            color = if (isSelected) CyberTeal else TextMuted
        )
    }
}

@Composable
private fun AddMemberMiniItem(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceContainerLowest)
                .border(1.5.dp, CyberTeal.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Member",
                tint = CyberTeal,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "+ Add New", fontSize = 11.sp, color = CyberTeal, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CategorySelectButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CyberTeal.copy(alpha = 0.12f) else SurfaceContainer)
            .border(
                1.dp,
                if (isSelected) CyberTeal else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) CyberTeal else TextMuted,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) CyberTeal else TextPrimary,
            maxLines = 1
        )
    }
}

@Composable
private fun FormInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextMuted, fontSize = 13.sp) },
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
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}
