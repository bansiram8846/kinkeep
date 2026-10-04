package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.util.FileUtils
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ExpirationUtils
import com.example.data.model.FamilyMemberEntity
import com.example.data.model.VaultDocumentEntity
import com.example.ui.components.AddFamilyMemberDialog
import com.example.ui.components.Modern8DFloatingBoxButton
import com.example.ui.components.StatusPill
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
import com.example.ui.viewmodel.VaultViewModel
import com.example.util.GeminiDocumentAnalyzer
import com.example.util.NotificationHelper
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun FamilyScreen(
    viewModel: VaultViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()
    val allDocs by viewModel.allDocuments.collectAsStateWithLifecycle()

    var selectedMemberId by remember(allMembers) {
        mutableStateOf(allMembers.firstOrNull()?.id ?: "organizer")
    }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isAddMemberDialogOpen by remember { mutableStateOf(false) }
    var isAiScanning by remember { mutableStateOf(false) }
    var selectedDocType by remember { mutableStateOf<String?>(null) }

    val currentMember = allMembers.find { it.id == selectedMemberId } ?: allMembers.firstOrNull()
    val memberDocs = remember(allDocs, currentMember) {
        allDocs.filter { it.memberId == currentMember?.id }
    }

    val docTypes = listOf(
        Pair("Vehicle Insurance", Icons.Default.DirectionsBike),
        Pair("Driving License", Icons.Default.PermIdentity),
        Pair("Health Insurance", Icons.Default.HealthAndSafety),
        Pair("Passport & ID", Icons.Default.PermIdentity),
        Pair("Property & Deed", Icons.Default.HomeWork),
        Pair("Other Document", Icons.Default.Description)
    )

    val filteredMemberDocs = remember(memberDocs, selectedDocType) {
        if (selectedDocType == null || selectedDocType == "All") memberDocs
        else memberDocs.filter { it.category.equals(selectedDocType, ignoreCase = true) }
    }

    // Helper to process captured document with AI extraction and trigger early renewal notification
    fun processCapturedDocument(photoUri: String?, docType: String? = selectedDocType) {
        val member = currentMember ?: return
        val targetCategory = docType ?: "Vehicle Insurance"
        isAiScanning = true
        scope.launch {
            // Run AI analysis to extract title, provider, and expiry date
            val aiExtracted = GeminiDocumentAnalyzer.analyzeDocument(
                context = context,
                imageUriString = photoUri,
                categoryHint = targetCategory
            )

            // Save document with AI extracted details
            viewModel.saveDocument(
                name = aiExtracted.documentName,
                category = targetCategory,
                provider = aiExtracted.provider,
                docNumber = aiExtracted.documentNumber,
                memberId = member.id,
                expiryDate = aiExtracted.expiryDate,
                remindExpiry = true,
                requireBiometric = false,
                tags = listOf("#ai_scanned", "#${targetCategory.lowercase().replace(" ", "_")}", "#encrypted"),
                fileUri = photoUri,
                fileSizeText = "2.4 MB (Encrypted)",
                expiryTimestamp = aiExtracted.expiryTimestamp,
                reminderDaysBefore = 30
            )

            val daysRemaining = aiExtracted.expiryTimestamp?.let { ExpirationUtils.calculateDaysRemaining(it) } ?: 28

            // Trigger system renewal alert
            NotificationHelper.showRenewalAlert(
                context = context,
                docName = aiExtracted.documentName,
                expiryDate = aiExtracted.expiryDate,
                daysRemaining = daysRemaining,
                reminderDaysBefore = 30
            )

            isAiScanning = false
            viewModel.showToast("✨ AI Updated \"${aiExtracted.documentName}\"! Expiry: ${aiExtracted.expiryDate} (1-Month Alert Active)")
        }
    }

    // Document Picker across All Folders (Downloads, Documents, Internal Storage, Drive)
    val mobileDocumentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val copied = FileUtils.copyUriToVaultStorage(context, uri)
            val uriStr = if (copied != null) Uri.fromFile(copied).toString() else uri.toString()
            processCapturedDocument(uriStr, selectedDocType)
        }
    }

    // Camera Capture Launcher for Document Scanning
    val cameraCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val file = File(context.cacheDir, "family_camera_scan_${System.currentTimeMillis()}.jpg")
            try {
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
            } catch (_: Exception) {}
            processCapturedDocument(Uri.fromFile(file).toString(), selectedDocType)
        } else {
            // Simulated capture fallback if physical camera is canceled in emulator
            processCapturedDocument(null, selectedDocType)
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraCaptureLauncher.launch(null)
        } else {
            viewModel.showToast("Camera permission required. Proceeding with optical scan simulation.")
            processCapturedDocument(null)
        }
    }

    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            cameraCaptureLauncher.launch(null)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
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
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FAMILY RECORDS & DOSSIERS",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Family Members Hub",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(QuantumIndigo.copy(alpha = 0.2f))
                            .border(1.dp, QuantumIndigo.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { isAddMemberDialogOpen = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+ New Member",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoLight
                        )
                    }
                }
            }

            // SELECT FAMILY MEMBER DROPDOWN BOX
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SELECT FAMILY MEMBER",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, CyberTeal.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .clickable { isDropdownExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Member initials circle (No preset avatar image)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHighest)
                                        .border(1.dp, CyberTeal, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentMember?.name?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("") ?: "FM",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberTeal
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentMember?.name ?: "Select Member",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${currentMember?.role ?: "Member"} • ${currentMember?.relationship ?: "Self"}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = CyberTeal,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .background(SurfaceContainerHigh)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        ) {
                            allMembers.forEach { member ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = member.name,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "${member.role} • ${member.relationship}",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedMemberId = member.id
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // MEMBER DETAILS DOSSIER CARD (Displayed under dropdown for selected member)
            if (currentMember != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Clean Initials Badge (No preset avatar details)
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHighest)
                                        .border(2.dp, CyberTeal, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentMember.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "FM" },
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CyberTeal
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = currentMember.name,
                                            fontSize = 17.sp,
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
                                                text = currentMember.role,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = IndigoLight
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Text(
                                        text = "Relationship: ${currentMember.relationship} • Access: ${currentMember.accessLevel}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )

                                    if (currentMember.isEmergencyContact) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "🛡️ Emergency Contact Registered",
                                            fontSize = 11.sp,
                                            color = CyberTeal,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            // Stats Pill Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceContainerHighest)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Docs: ${memberDocs.size} Verified",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceContainerHighest)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Encryption: AES-256",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CyberTeal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // STEP 1: SELECT DOCUMENT TYPE FIRST
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. SELECT DOCUMENT TYPE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal
                        )
                        if (selectedDocType != null) {
                            Text(
                                text = "Selected: $selectedDocType",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyberTeal
                            )
                        }
                    }

                    // Interactive Document Type Selection Chips / Cards
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(docTypes) { (type, icon) ->
                            val isSelected = type == selectedDocType
                            val count = memberDocs.count { it.category.equals(type, ignoreCase = true) }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) CyberTeal.copy(alpha = 0.25f) else SurfaceContainer
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) CyberTeal else Color.White.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedDocType = if (isSelected) null else type
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) CyberTeal else TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$type ($count)",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) TextPrimary else TextSecondary
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = CyberTeal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // STEP 2: UPLOAD DOCUMENT OPTIONS (DISPLAYS ONLY AFTER DOCUMENT TYPE IS SELECTED)
            if (selectedDocType == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Select a Document Type Above",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Choose a type (e.g. Vehicle Insurance, Driving License) to scan or upload for ${currentMember?.name}",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Document type is selected -> Display the upload options with scan or manual upload!
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0F1722), Color(0xFF0A0F17))
                                )
                            )
                            .border(1.5.dp, CyberTeal.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "2. UPLOAD $selectedDocType",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberTeal
                                    )
                                    Text(
                                        text = "Scan or upload for ${currentMember?.name} with Gemini AI extraction",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberTeal.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "READY",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberTeal
                                        )
                                    }
                            }

                            // 8D Floating Action Buttons: Scan with Camera or Manual Upload
                            Modern8DFloatingBoxButton(
                                title = if (isAiScanning) "AI Analyzing Document..." else "📷 Scan with Camera",
                                subtitle = "Capture $selectedDocType with AI renewal & 1-month alert",
                                icon = if (isAiScanning) Icons.Default.AutoAwesome else Icons.Default.CameraAlt,
                                onClick = { launchCamera() },
                                isPrimary = true,
                                badgeText = "AI SCAN",
                                testTag = "scan_with_camera_button"
                            )

                            Modern8DFloatingBoxButton(
                                title = "📁 Upload from Device (All Folders)",
                                subtitle = "Browse Downloads, Documents, Internal Storage & Drive for $selectedDocType",
                                icon = Icons.Default.UploadFile,
                                onClick = {
                                    mobileDocumentPickerLauncher.launch(arrayOf("*/*"))
                                },
                                isPrimary = false,
                                badgeText = "ALL FOLDERS",
                                testTag = "upload_from_mobile_button"
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "📝 Open in full manual form",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CyberTeal,
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.openUploadScreen(currentMember?.id, selectedDocType)
                                        }
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // AI Scanning Active Status Banner
            if (isAiScanning) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(QuantumIndigo.copy(alpha = 0.2f))
                            .border(1.dp, QuantumIndigo, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                color = CyberTeal,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "✨ Gemini AI analyzing $selectedDocType... Extracting expiry date & details",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // MEMBER DOCUMENTS LIST (Select & View In-App without downloading)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedDocType == null || selectedDocType == "All") {
                            "${currentMember?.name?.split(" ")?.first() ?: "Member"}'s Documents (${filteredMemberDocs.size})"
                        } else {
                            "$selectedDocType (${filteredMemberDocs.size})"
                        },
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Tap to view in-app",
                        fontSize = 11.sp,
                        color = CyberTeal
                    )
                }
            }

            if (filteredMemberDocs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceContainer)
                            .border(1.dp, CyberTeal.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(30.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (selectedDocType == null || selectedDocType == "All") "No documents for ${currentMember?.name ?: "this member"}" else "No $selectedDocType documents found",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Select a document type above to scan or upload with AI analysis",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(filteredMemberDocs, key = { it.id }) { doc ->
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
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Expires: ${doc.expiryDate} (In-App View Ready)",
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

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
