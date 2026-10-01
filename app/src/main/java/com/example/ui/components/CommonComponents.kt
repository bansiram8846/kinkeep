package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Diversity3
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedLight
import com.example.ui.theme.CyberTeal
import com.example.ui.theme.CyberTealDark
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.QuantumIndigo
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BottomTab

@Composable
fun StatusPill(
    status: String,
    daysRemaining: Int? = null,
    modifier: Modifier = Modifier
) {
    when {
        daysRemaining != null || status.equals("Expiring", ignoreCase = true) -> {
            val text = if (daysRemaining != null) "Expires in $daysRemaining days" else "Action needed"
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFF3B151C))
                    .border(1.dp, Color(0xFFFF4757).copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AlertRed)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = text,
                    color = AlertRedLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        status.equals("Verified", ignoreCase = true) -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(CyberTeal.copy(alpha = 0.15f))
                    .border(1.dp, CyberTeal.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 9.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(CyberTeal)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Verified",
                    color = CyberTeal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        else -> {
            Box(
                modifier = modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(SurfaceContainerHigh)
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = status,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * 8D Cyber Model Button with physical spring press travel, bottom 3D bevel extrusion,
 * specular reflection, and luminous cyber teal glow.
 */
@Composable
fun CyberButton3D(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    height: Dp = 50.dp,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 3D physical button compression
    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 3.5.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pressOffset"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )

    // Animated diagonal shimmer across 3D face
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(height + 4.dp)
            .testTag(testTag)
    ) {
        // Bottom 3D extrusion slab (stationary underneath)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF005E4F))
        )

        // Floating top face that physically depresses downward on touch
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = pressOffsetY)
                .shadow(
                    elevation = if (isPressed) 2.dp else 8.dp,
                    shape = RoundedCornerShape(14.dp),
                    spotColor = CyberTeal
                )
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF5FFFEF),
                            CyberTeal,
                            Color(0xFF00C7AB)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            // Diagonal specular sheen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = shimmerAlpha),
                                Color.Transparent,
                                Color.White.copy(alpha = shimmerAlpha * 0.4f)
                            )
                        )
                    )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = CyberTealDark,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CyberTealDark,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

/**
 * 8D Secondary Tactile Button with deep obsidian 3D extrusion,
 * quantum indigo rim highlight, and physical press feedback.
 */
@Composable
fun CyberSecondaryButton3D(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp = 44.dp,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pressOffsetSecondary"
    )

    Box(
        modifier = modifier
            .height(height + 3.dp)
            .testTag(testTag)
    ) {
        // Bottom 3D extrusion
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = 3.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF090D15))
        )

        // Top face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = pressOffsetY)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF232836),
                            Color(0xFF161A24)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }
    }
}

/**
 * 8D Tactile Icon Button (e.g. for quick (+) add, back, or options)
 */
@Composable
fun CyberIconButton3D(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    size: Dp = 40.dp,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffset by animateDpAsState(
        targetValue = if (isPressed) 2.5.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconPressOffset"
    )

    Box(
        modifier = modifier
            .size(size + 3.dp)
            .testTag(testTag)
    ) {
        // 3D base
        Box(
            modifier = Modifier
                .size(size)
                .offset(y = 3.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isPrimary) Color(0xFF005E4F) else Color(0xFF0A0D15))
        )

        // Top face
        Box(
            modifier = Modifier
                .size(size)
                .offset(y = pressOffset)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isPrimary) {
                        Brush.verticalGradient(
                            listOf(Color(0xFF5FFFEF), CyberTeal, Color(0xFF00C7AB))
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(Color(0xFF262C3A), Color(0xFF181C26))
                        )
                    }
                )
                .border(
                    1.dp,
                    if (isPrimary) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f),
                    RoundedCornerShape(12.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isPrimary) CyberTealDark else TextPrimary,
                modifier = Modifier.size((size.value * 0.5f).dp)
            )
        }
    }
}

@Composable
fun KinKeepBottomNav(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    hasExpiringAlert: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceContainerLowest)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = if (currentTab == BottomTab.VAULT) Icons.Filled.Lock else Icons.Outlined.Lock,
                label = "Vault",
                isSelected = currentTab == BottomTab.VAULT,
                onClick = { onTabSelected(BottomTab.VAULT) },
                modifier = Modifier.weight(1f),
                testTag = "nav_tab_vault"
            )

            BottomNavItem(
                icon = if (currentTab == BottomTab.FAMILY) Icons.Filled.Diversity3 else Icons.Outlined.Diversity3,
                label = "Family",
                isSelected = currentTab == BottomTab.FAMILY,
                onClick = { onTabSelected(BottomTab.FAMILY) },
                modifier = Modifier.weight(1f),
                testTag = "nav_tab_family"
            )

            BottomNavItem(
                icon = if (currentTab == BottomTab.EXPIRING) Icons.Filled.Schedule else Icons.Outlined.Schedule,
                label = "Expiring",
                isSelected = currentTab == BottomTab.EXPIRING,
                onClick = { onTabSelected(BottomTab.EXPIRING) },
                hasBadge = hasExpiringAlert,
                modifier = Modifier.weight(1f),
                testTag = "nav_tab_expiring"
            )

            BottomNavItem(
                icon = if (currentTab == BottomTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                label = "Settings",
                isSelected = currentTab == BottomTab.SETTINGS,
                onClick = { onTabSelected(BottomTab.SETTINGS) },
                modifier = Modifier.weight(1f),
                testTag = "nav_tab_settings"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasBadge: Boolean = false,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "navItemScale"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) SurfaceContainerHigh else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) CyberTeal else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            if (hasBadge && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AlertRed)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) CyberTeal else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
