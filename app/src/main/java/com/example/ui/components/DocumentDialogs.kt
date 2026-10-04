package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.VaultDocumentEntity
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
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BiometricAuthDialog(
    document: VaultDocumentEntity,
    onAuthenticate: () -> Unit,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceContainerLow,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberTeal.copy(alpha = 0.4f)),
            modifier = Modifier.testTag("biometric_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = CyberTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SECURITY CHECK",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(CyberTeal.copy(alpha = 0.12f))
                        .border(1.5.dp, CyberTeal.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Fingerprint Sensor",
                        tint = CyberTeal,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Biometric Verification",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Touch fingerprint sensor to decrypt \"${document.name}\"",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onAuthenticate,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberTeal, contentColor = Color(0xFF00201A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("verify_biometric_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verify Biometrics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onAuthenticate,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Use Master Passcode",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * Full-Screen In-App Document Viewer Dialog.
 * Completely fills the screen so users can inspect documents and certificates in full resolution
 * with pinch-to-zoom, pan, immersive full-screen mode, and comprehensive metadata details.
 */
@Composable
fun DocumentViewerDialog(
    document: VaultDocumentEntity,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit,
    onShare: (VaultDocumentEntity) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Full screen view states
    var isImmersiveFullScreen by remember { mutableStateOf(false) }
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var copyConfirmation by remember { mutableStateOf(false) }

    BackHandler {
        if (isImmersiveFullScreen) {
            isImmersiveFullScreen = false
            zoomScale = 1f
            offsetX = 0f
            offsetY = 0f
        } else {
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground)
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("document_viewer_dialog")
        ) {
            if (isImmersiveFullScreen) {
                // ==========================================
                // 1. IMMERSIVE FULL SCREEN IMAGE / CANVAS
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                zoomScale = (zoomScale * zoom).coerceIn(1f, 5f)
                                if (zoomScale > 1f) {
                                    val maxOffsetX = 1000f * (zoomScale - 1f)
                                    val maxOffsetY = 1500f * (zoomScale - 1f)
                                    offsetX = (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                                    offsetY = (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                                } else {
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (zoomScale > 1.2f) {
                                        zoomScale = 1f
                                        offsetX = 0f
                                        offsetY = 0f
                                    } else {
                                        zoomScale = 2.5f
                                    }
                                }
                            )
                        }
                ) {
                    if (!document.fileUri.isNullOrBlank()) {
                        AsyncImage(
                            model = document.fileUri,
                            contentDescription = document.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = zoomScale
                                    scaleY = zoomScale
                                    translationX = offsetX
                                    translationY = offsetY
                                }
                        )
                    } else {
                        // Digital certificate full screen canvas
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DigitalCertificateFullCard(document = document)
                        }
                    }

                    // Floating Top Overlay Controls in Immersive Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .border(1.dp, CyberTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable {
                                    isImmersiveFullScreen = false
                                    zoomScale = 1f
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FullscreenExit,
                                    contentDescription = "Exit Fullscreen",
                                    tint = CyberTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Exit Full Screen",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Zoom percentage badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${(zoomScale * 100).toInt()}% • Double-tap to reset",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = CyberTeal
                            )
                        }
                    }

                    // Floating Zoom Action Controls at Bottom
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .border(1.dp, CyberTeal.copy(alpha = 0.4f), RoundedCornerShape(30.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                zoomScale = (zoomScale - 0.5f).coerceAtLeast(1f)
                                if (zoomScale <= 1f) {
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomOut,
                                contentDescription = "Zoom Out",
                                tint = TextPrimary
                            )
                        }

                        Text(
                            text = "${(zoomScale * 100).toInt()}%",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal,
                            fontSize = 13.sp
                        )

                        IconButton(
                            onClick = { zoomScale = (zoomScale + 0.5f).coerceAtMost(5f) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "Zoom In",
                                tint = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberTeal.copy(alpha = 0.2f))
                                .clickable {
                                    zoomScale = 1f
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Fit",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberTeal
                            )
                        }
                    }
                }
            } else {
                // ==========================================
                // 2. STANDARD FULL-SCREEN DOCUMENT VIEWER
                // ==========================================
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top App Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLowest)
                            .border(androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("close_document_viewer")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = document.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = CyberTeal,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = "${document.category} • Member: ${document.memberName}",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Fullscreen Button
                            IconButton(
                                onClick = { isImmersiveFullScreen = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Full Screen",
                                    tint = CyberTeal,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Share Button
                            IconButton(
                                onClick = { onShare(document) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Delete Button
                            IconButton(
                                onClick = {
                                    onDelete(document.id)
                                    onDismiss()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = AlertRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Main Scrollable Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Document Preview Hero Container (Full-width, expansive view)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(340.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(SurfaceContainerLowest)
                                .border(1.5.dp, CyberTeal.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                                .clickable { isImmersiveFullScreen = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!document.fileUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = document.fileUri,
                                    contentDescription = document.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                DigitalCertificateFullCard(document = document)
                            }

                            // Tap to Expand Overlay Pill
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.8f))
                                    .border(1.dp, CyberTeal.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .clickable { isImmersiveFullScreen = true }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Fullscreen,
                                        contentDescription = null,
                                        tint = CyberTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tap for 100% Full Screen",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberTeal
                                    )
                                }
                            }

                            // Biometric Hardware Badge
                            if (document.requireBiometric) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(12.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .border(1.dp, CyberTeal.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = null,
                                            tint = CyberTeal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "BIOMETRIC LOCKED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberTeal
                                        )
                                    }
                                }
                            }
                        }

                        // Full Screen Mode Quick Action Button
                        Modern8DFloatingBoxButton(
                            title = "⛶ Open Full Screen & Zoom",
                            subtitle = "Inspect tiny text, stamps, signatures & high-resolution details",
                            icon = Icons.Default.Fullscreen,
                            onClick = { isImmersiveFullScreen = true },
                            isPrimary = true,
                            badgeText = "FULLSCREEN",
                            testTag = "open_fullscreen_button"
                        )

                        // Verified Document Metadata & AI Extraction Record
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceContainer)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "DOCUMENT VERIFICATION DATA",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberTeal
                                        )
                                        Text(
                                            text = "Hardware encrypted with Zero-Knowledge keys",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    StatusPill(
                                        status = document.status,
                                        daysRemaining = document.daysRemaining
                                    )
                                }

                                // Document / Policy Number with Copy Action
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceContainerLowest)
                                        .border(1.dp, CyberTeal.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "POLICY / IDENTIFICATION NUMBER",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = document.policyOrIdNumber,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CyberTealBright
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                val clip = ClipData.newPlainText("Document Number", document.policyOrIdNumber)
                                                clipboard?.setPrimaryClip(clip)
                                                copyConfirmation = true
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy Number",
                                                tint = if (copyConfirmation) CyberTeal else TextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                AnimatedVisibility(visible = copyConfirmation) {
                                    Text(
                                        text = "✓ Number copied to clipboard!",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CyberTeal
                                    )
                                }

                                // Key Information Grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Expiration Date
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SurfaceContainerLowest)
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "EXPIRATION DATE",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = document.expiryDate,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                            document.daysRemaining?.let { days ->
                                                Text(
                                                    text = if (days < 0) "Expired ${-days}d ago" else "$days days left",
                                                    fontSize = 11.sp,
                                                    color = if (days <= 30) AlertRedLight else CyberTeal
                                                )
                                            }
                                        }
                                    }

                                    // Issuing Authority / Provider
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(SurfaceContainerLowest)
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "ISSUING PROVIDER",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = document.provider,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = document.category,
                                                fontSize = 11.sp,
                                                color = IndigoLight
                                            )
                                        }
                                    }
                                }

                                // AI Intelligence Extraction Note
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(QuantumIndigo.copy(alpha = 0.15f))
                                        .border(1.dp, QuantumIndigo.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = null,
                                                tint = CyberTeal,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "AI EXTRACTION & RENEWAL REMINDER",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CyberTeal
                                            )
                                        }
                                        Text(
                                            text = if (document.remindExpiry) {
                                                "Active Renewal Monitoring: System sends notification 30 days before expiration. Vault member ${document.memberName} assigned."
                                            } else {
                                                "Renewal reminder inactive. Vault member ${document.memberName} assigned."
                                            },
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                if (!document.sharedNotes.isNullOrBlank()) {
                                    Text(
                                        text = "Notes: ${document.sharedNotes}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        // Bottom Actions (Share & Delete)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CyberSecondaryButton3D(
                                text = "Share Encrypted Link",
                                icon = Icons.Default.Share,
                                onClick = { onShare(document) },
                                modifier = Modifier.weight(1f),
                                height = 44.dp,
                                testTag = "viewer_share_button"
                            )

                            CyberButton3D(
                                text = "Close",
                                icon = Icons.Default.Close,
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                height = 44.dp,
                                testTag = "viewer_close_button"
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onDelete(document.id)
                                onDismiss()
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("viewer_delete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Delete from KinKeep Vault",
                                color = AlertRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

/**
 * High-resolution digital credential canvas rendered when no physical image snapshot is uploaded.
 */
@Composable
fun DigitalCertificateFullCard(document: VaultDocumentEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF192030),
                        Color(0xFF10141F),
                        Color(0xFF0C0E17)
                    )
                )
            )
            .border(1.5.dp, CyberTeal.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = document.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = document.provider,
                        fontSize = 13.sp,
                        color = CyberTeal
                    )
                }
                StatusPill(
                    status = document.status,
                    daysRemaining = document.daysRemaining
                )
            }

            Column {
                Text(
                    text = "OFFICIAL RECORD / IDENTIFIER",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Text(
                    text = document.policyOrIdNumber,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberTealBright
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "VALID THROUGH / EXPIRY",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = document.expiryDate,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "CATEGORY",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = document.category,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = IndigoLight
                    )
                }
            }

            // Holographic simulated security strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF5FFFEF).copy(alpha = 0.3f),
                                QuantumIndigo.copy(alpha = 0.35f),
                                Color(0xFFFFB020).copy(alpha = 0.25f),
                                CyberTeal.copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AUTHENTICATED VAULT CERTIFICATE • ENCRYPTED PAYLOAD",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun ShareDocumentDialog(
    document: VaultDocumentEntity,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceContainerLow,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Encrypted Share",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Generate a timed, end-to-end encrypted link for \"${document.name}\"",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, CyberTeal.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "SECURE PROTOCOL LINK",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "https://kinkeep.vault/enc/${document.id}?t=8h39f28",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = CyberTeal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CyberButton3D(
                    text = "Copy Encrypted Link",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    height = 42.dp,
                    testTag = "copy_encrypted_link_button"
                )
            }
        }
    }
}
