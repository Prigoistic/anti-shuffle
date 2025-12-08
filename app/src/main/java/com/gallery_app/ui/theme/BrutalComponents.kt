package com.gallery_app.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Neo-Brutalism style button with thick border and heavy shadow
 */
@Composable
fun BrutalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrutalColors.Yellow,
    contentColor: Color = BrutalColors.Black,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .offset(x = 4.dp, y = 4.dp)
            .background(BrutalColors.Black)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.offset(x = (-4).dp, y = (-4).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor
            ),
            shape = RoundedCornerShape(0.dp),
            border = BorderStroke(3.dp, BrutalColors.Black),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            content()
        }
    }
}

/**
 * Neo-Brutalism style outlined button
 */
@Composable
fun BrutalOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrutalColors.White,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .offset(x = 4.dp, y = 4.dp)
            .background(BrutalColors.Black)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.offset(x = (-4).dp, y = (-4).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = BrutalColors.Black
            ),
            shape = RoundedCornerShape(0.dp),
            border = BorderStroke(3.dp, BrutalColors.Black),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            content()
        }
    }
}

/**
 * Neo-Brutalism style card with thick border and shadow
 */
@Composable
fun BrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrutalColors.White,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .offset(x = 6.dp, y = 6.dp)
            .background(BrutalColors.Black)
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-6).dp, y = (-6).dp)
                .background(backgroundColor)
                .border(3.dp, BrutalColors.Black)
                .then(
                    if (onClick != null) Modifier.clickable(onClick = onClick)
                    else Modifier
                )
        ) {
            content()
        }
    }
}

/**
 * Neo-Brutalism style top app bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrutalTopAppBar(
    title: String,
    backgroundColor: Color = BrutalColors.Cyan,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(BrutalColors.Black)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .offset(y = (-4).dp),
            color = backgroundColor,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 3.dp, color = BrutalColors.Black)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (navigationIcon != null) {
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        navigationIcon()
                    }
                }
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = BrutalColors.Black,
                    modifier = Modifier.weight(1f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    actions()
                }
            }
        }
    }
}

/**
 * Neo-Brutalism style icon button
 */
@Composable
fun BrutalIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrutalColors.White
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .offset(x = 3.dp, y = 3.dp)
            .background(BrutalColors.Black)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .offset(x = (-3).dp, y = (-3).dp)
                .background(backgroundColor)
                .border(2.dp, BrutalColors.Black)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = BrutalColors.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Neo-Brutalism style text with heavy styling
 */
@Composable
fun BrutalTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = BrutalColors.Black
) {
    Text(
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.Black,
        color = color,
        modifier = modifier,
        letterSpacing = (-0.5).sp
    )
}

@Composable
fun BrutalHeadline(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = BrutalColors.Black
) {
    Text(
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        color = color,
        modifier = modifier
    )
}

@Composable
fun BrutalBody(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = BrutalColors.Black
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}

/**
 * Neo-Brutalism style image container
 */
@Composable
fun BrutalImageContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .offset(x = 5.dp, y = 5.dp)
            .background(BrutalColors.Black)
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-5).dp, y = (-5).dp)
                .border(3.dp, BrutalColors.Black)
        ) {
            content()
        }
    }
}

/**
 * Neo-Brutalism style loading indicator
 */
@Composable
fun BrutalLoadingBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrutalColors.OffWhite),
        contentAlignment = Alignment.Center
    ) {
        BrutalCard(
            modifier = Modifier.size(120.dp),
            backgroundColor = BrutalColors.Yellow
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = BrutalColors.Black,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                BrutalBody("LOADING...")
            }
        }
    }
}
