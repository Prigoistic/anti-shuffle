package com.gallery_app.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Background gradient for the entire app
 */
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassColors.DarkBlueStart,
                        GlassColors.MidBlue,
                        GlassColors.DarkBlueEnd
                    )
                )
            )
    ) {
        content()
    }
}

/**
 * Glassmorphic card with blur effect and subtle border
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    backgroundColor: Color = GlassColors.GlassDark.copy(alpha = 0.6f),
    borderColor: Color = GlassColors.GlassBorder.copy(alpha = 0.3f),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
    ) {
        content()
    }
}

/**
 * Folder card matching the design - dark glass with rounded corners
 */
@Composable
fun GlassFolderCard(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier
            .aspectRatio(1.2f),
        cornerRadius = 16.dp,
        backgroundColor = GlassColors.FolderDark.copy(alpha = 0.8f),
        borderColor = GlassColors.GlassBorder.copy(alpha = 0.2f),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            // Folder tab at top
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 8.dp)
                    .width(48.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                    .background(GlassColors.GlassBorder.copy(alpha = 0.3f))
            )
            
            // Label
            Text(
                text = label,
                color = GlassColors.TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

/**
 * Circular glass button with icon
 */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    backgroundColor: Color = GlassColors.GlassDark.copy(alpha = 0.6f),
    iconTint: Color = GlassColors.TextPrimary
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, GlassColors.GlassBorder.copy(alpha = 0.3f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

/**
 * Menu/hamburger button as shown in design
 */
@Composable
fun GlassMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(GlassColors.GlassDark.copy(alpha = 0.6f))
            .border(1.dp, GlassColors.GlassBorder.copy(alpha = 0.3f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(GlassColors.TextPrimary)
                )
            }
        }
    }
}

/**
 * Floating action button with glow effect (like the mic button in design)
 */
@Composable
fun GlassFloatingButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GlassColors.MicButton.copy(alpha = 0.6f),
                            GlassColors.MicButton.copy(alpha = 0.0f)
                        )
                    )
                )
        )
        // Button
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GlassColors.MicButton,
                            GlassColors.AccentPurple
                        )
                    )
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = GlassColors.TextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Large display text (like the date in design)
 */
@Composable
fun GlassDisplayText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 48
) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Bold,
        color = GlassColors.TextPrimary,
        modifier = modifier,
        letterSpacing = (-1).sp
    )
}

/**
 * Subtitle text
 */
@Composable
fun GlassSubtitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 40.sp,
        fontWeight = FontWeight.Light,
        color = GlassColors.TextSecondary.copy(alpha = 0.7f),
        modifier = modifier
    )
}

/**
 * Headline text
 */
@Composable
fun GlassHeadline(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GlassColors.TextPrimary
) {
    Text(
        text = text,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier,
        lineHeight = 34.sp
    )
}

/**
 * Body text
 */
@Composable
fun GlassBodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = GlassColors.TextSecondary
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = color,
        modifier = modifier
    )
}

/**
 * Quote card as shown in design
 */
@Composable
fun GlassQuoteCard(
    quote: String,
    author: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        cornerRadius = 16.dp,
        backgroundColor = GlassColors.GlassDark.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "✦",
                color = GlassColors.TextSecondary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = quote,
                color = GlassColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = author,
                color = GlassColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Loading indicator with glass style
 */
@Composable
fun GlassLoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = GlassColors.AccentBlue,
            strokeWidth = 3.dp,
            modifier = Modifier.size(48.dp)
        )
    }
}

/**
 * Shimmer loading placeholder
 */
@Composable
fun GlassShimmer(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        GlassColors.GlassDark.copy(alpha = 0.3f),
        GlassColors.GlassLight.copy(alpha = 0.5f),
        GlassColors.GlassDark.copy(alpha = 0.3f)
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
    )
}

/**
 * Top bar with glass effect
 */
@Composable
fun GlassTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (navigationIcon != null) {
            navigationIcon()
        } else {
            Spacer(modifier = Modifier.width(44.dp))
        }
        
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            title()
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            actions()
        }
    }
}

/**
 * Image card with glass overlay
 */
@Composable
fun GlassImageCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        content()
    }
}
