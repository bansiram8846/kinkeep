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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
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
import com.example.ui.viewmodel.BottomTab

/**
 * Standard Status Pill indicator
 */
@Composable
fun StatusPill(
    status: String,
    daysRemaining: Int? = null,
    modifier: Modifier = Modifier
) {
    val isUrgent = daysRemaining != null && daysRemaining <= 30
    val isExpired = daysRemaining != null && daysRemaining <= 0

    val (bg, border, text, label) = when {
        isExpired -> Quad(
            AlertRed.copy(alpha = 0.15f),
            AlertRed.copy(alpha = 0.5f),
            AlertRedLight,
            "EXPIRED"
        )
        isUrgent -> Quad(
            AlertRed.copy(alpha = 0.15f),
            AlertRed.copy(alpha = 0.5f),
            AlertRedLight,
            "EXPIRING (${daysRemaining}d)"
        )
        status == "Expiring" -> Quad(
            Color(0xFFFFA502).copy(alpha = 0.15f),
            Color(0xFFFFA502).copy(alpha = 0.4f),
            Color(0xFFFFA502),
            "EXPIRING"
        )
        else -> Quad(
            CyberTeal.copy(alpha = 0.15f),
            CyberTeal.copy(alpha = 0.4f),
            CyberTeal,
            "VERIFIED"
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(text)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = text
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * StatCard for overview screens
 */
@Composable
fun StatCard(
    title: String,
    count: String,
    subtitle: String,
    subtitleColor: Color = TextSecondary,
    isAlert: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainer)
            .border(
                1.dp,
                if (isAlert) AlertRed.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAlert) AlertRedLight else TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = subtitleColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * 8D Tactile Primary Button with 3D extrusion, physical press response,
 * and immediate ripple feedback for maximum reliability in both emulator and touch devices.
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

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 3.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressOffset"
    )

    Box(
        modifier = modifier
            .height(height + 4.dp)
            .testTag(testTag)
    ) {
        // Bottom 3D shadow/extrusion base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF005E4F))
        )

        // Floating top face with responsive click & ripple
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = pressOffsetY)
                .shadow(
                    elevation = if (isPressed) 2.dp else 6.dp,
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
                    indication = ripple(color = CyberTealDark),
                    enabled = enabled,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
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
 * quantum indigo rim highlight, and responsive click handling.
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
        targetValue = if (isPressed) 2.5.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
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
                    indication = ripple(color = CyberTeal),
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
 * 8D Square Icon Button with 3D tactile extrusion
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
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
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
                    indication = ripple(color = if (isPrimary) CyberTealDark else CyberTeal),
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

/**
 * Robust Material 3 Bottom Navigation Bar with instant responsiveness,
 * high touch accuracy for emulator and physical devices.
 */
@Composable
fun KinKeepBottomNav(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    hasExpiringAlert: Boolean = false,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = SurfaceContainerLowest,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == BottomTab.VAULT,
            onClick = { onTabSelected(BottomTab.VAULT) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.VAULT) Icons.Filled.Lock else Icons.Outlined.Lock,
                    contentDescription = "Vault"
                )
            },
            label = {
                Text(
                    text = "Vault",
                    fontWeight = if (currentTab == BottomTab.VAULT) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberTealDark,
                selectedTextColor = CyberTeal,
                indicatorColor = CyberTeal,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_vault")
        )

        NavigationBarItem(
            selected = currentTab == BottomTab.FAMILY,
            onClick = { onTabSelected(BottomTab.FAMILY) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.FAMILY) Icons.Filled.Diversity3 else Icons.Outlined.Diversity3,
                    contentDescription = "Family"
                )
            },
            label = {
                Text(
                    text = "Family",
                    fontWeight = if (currentTab == BottomTab.FAMILY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberTealDark,
                selectedTextColor = CyberTeal,
                indicatorColor = CyberTeal,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_family")
        )

        NavigationBarItem(
            selected = currentTab == BottomTab.EXPIRING,
            onClick = { onTabSelected(BottomTab.EXPIRING) },
            icon = {
                BadgedBox(
                    badge = {
                        if (hasExpiringAlert) {
                            Badge(containerColor = AlertRed)
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == BottomTab.EXPIRING) Icons.Filled.Schedule else Icons.Outlined.Schedule,
                        contentDescription = "Expiring"
                    )
                }
            },
            label = {
                Text(
                    text = "Expiring",
                    fontWeight = if (currentTab == BottomTab.EXPIRING) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberTealDark,
                selectedTextColor = CyberTeal,
                indicatorColor = CyberTeal,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_expiring")
        )

        NavigationBarItem(
            selected = currentTab == BottomTab.SETTINGS,
            onClick = { onTabSelected(BottomTab.SETTINGS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = {
                Text(
                    text = "Settings",
                    fontWeight = if (currentTab == BottomTab.SETTINGS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyberTealDark,
                selectedTextColor = CyberTeal,
                indicatorColor = CyberTeal,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_settings")
        )
    }
}
