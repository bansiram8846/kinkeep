package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.QuantumIndigo
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BottomTab

@Composable
fun StatusPill(status: String, daysRemaining: Int? = null) {
    val isAlert = status == "EXPIRED" || (daysRemaining != null && daysRemaining <= 30)
    val bgColor = if (isAlert) AlertRed.copy(alpha = 0.2f) else CyberTeal.copy(alpha = 0.15f)
    val textColor = if (isAlert) AlertRedLight else CyberTeal
    val borderColor = if (isAlert) AlertRed.copy(alpha = 0.5f) else CyberTeal.copy(alpha = 0.4f)
    val text = when {
        status == "EXPIRED" -> "EXPIRED"
        daysRemaining != null && daysRemaining <= 0 -> "EXPIRED TODAY"
        daysRemaining != null && daysRemaining <= 30 -> "$daysRemaining DAYS LEFT"
        else -> "ACTIVE"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
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
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
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
                modifier = Modifier.size(size * 0.48f)
            )
        }
    }
}

/**
 * Modern 8D Floating Box Button featuring multi-depth isometric extrusion,
 * glowing neon rim, elevated icon pedestal, and physical tactile click displacement.
 */
@Composable
fun Modern8DFloatingBoxButton(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    badgeText: String? = null,
    height: Dp = 68.dp,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffsetY by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "8dBoxPress"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 14.dp,
        label = "8dBoxShadow"
    )

    Box(
        modifier = modifier
            .height(height + 6.dp)
            .testTag(testTag)
    ) {
        // Deep 3D base extrusion (The pedestal)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = 6.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (isPrimary) Color(0xFF00382E) else Color(0xFF080B12)
                )
        )

        // Floating interactive face with isometric lighting & glowing border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = pressOffsetY)
                .shadow(
                    elevation = shadowElevation,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = if (isPrimary) CyberTeal.copy(alpha = 0.6f) else QuantumIndigo.copy(alpha = 0.5f),
                    ambientColor = if (isPrimary) CyberTeal.copy(alpha = 0.3f) else Color.Black
                )
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (isPrimary) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF143D36),
                                Color(0xFF092923),
                                Color(0xFF061A16)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1E2433),
                                Color(0xFF131722),
                                Color(0xFF0D1017)
                            )
                        )
                    }
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = if (isPrimary) {
                            listOf(
                                Color(0xFF5FFFEF),
                                CyberTeal.copy(alpha = 0.8f),
                                Color(0xFF005E4F)
                            )
                        } else {
                            listOf(
                                IndigoLight.copy(alpha = 0.7f),
                                QuantumIndigo.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        }
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = if (isPrimary) CyberTeal else IndigoLight),
                    onClick = onClick
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Elevated 3D Floating Icon Box
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(12.dp),
                                spotColor = if (isPrimary) CyberTeal else QuantumIndigo
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isPrimary) {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF5FFFEF), CyberTeal, Color(0xFF008975))
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF333E56), Color(0xFF202738))
                                    )
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isPrimary) CyberTealDark else TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (badgeText != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isPrimary) CyberTeal.copy(alpha = 0.2f) else QuantumIndigo.copy(alpha = 0.25f)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPrimary) CyberTeal else IndigoLight
                                    )
                                }
                            }
                        }
                        if (subtitle != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // 3D chevron arrow indicator
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.06f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = if (isPrimary) CyberTeal else TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Modern 8D Floating Stat Box with colored isometric elevation,
 * tactile displacement on click, and responsive status glow.
 */
@Composable
fun Modern8DFloatingStatBox(
    title: String,
    count: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isAlert: Boolean = false,
    subtitleColor: Color = CyberTeal,
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
        label = "statBoxPress"
    )

    Box(
        modifier = modifier
            .height(106.dp)
            .testTag(testTag)
    ) {
        // Pedestal base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .offset(y = 5.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isAlert) Color(0xFF380808) else Color(0xFF090D15)
                )
        )

        // Floating top face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .offset(y = pressOffsetY)
                .shadow(
                    elevation = if (isPressed) 3.dp else 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = if (isAlert) AlertRed.copy(alpha = 0.5f) else CyberTeal.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = if (isAlert) {
                            listOf(Color(0xFF2A1012), Color(0xFF1B0A0C))
                        } else {
                            listOf(Color(0xFF181C26), Color(0xFF0F121A))
                        }
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = if (isAlert) {
                            listOf(AlertRed.copy(alpha = 0.8f), AlertRed.copy(alpha = 0.2f))
                        } else {
                            listOf(CyberTeal.copy(alpha = 0.5f), Color.White.copy(alpha = 0.08f))
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(color = if (isAlert) AlertRed else CyberTeal),
                    onClick = onClick
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = count,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isAlert) AlertRedLight else TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = subtitleColor,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun KinKeepBottomNavigation(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    expiringCount: Int = 0
) {
    NavigationBar(
        containerColor = Color(0xFF0E121A),
        contentColor = TextPrimary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == BottomTab.DASHBOARD,
            onClick = { onTabSelected(BottomTab.DASHBOARD) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.DASHBOARD) Icons.Filled.Lock else Icons.Outlined.Lock,
                    contentDescription = "Vault"
                )
            },
            label = {
                Text(
                    text = "Vault",
                    fontWeight = if (currentTab == BottomTab.DASHBOARD) FontWeight.Bold else FontWeight.Normal
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
                        if (expiringCount > 0) {
                            Badge(
                                containerColor = AlertRed,
                                contentColor = Color.White
                            ) {
                                Text(expiringCount.toString())
                            }
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
