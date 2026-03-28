package com.ashokvatika.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MainDashboardBackground = Color(0xFFF7F7F8)
private val MainDashboardTitle = Color(0xFFFF8A18)
private val MainDashboardText = Color(0xFF374055)
private val MainDashboardHint = Color(0xFFA1A7B6)
private val MainDashboardCardTop = Color(0xFFF7BF77)
private val MainDashboardCardBottom = Color(0xFFEA964D)
private val MainDashboardFont = FontFamily.SansSerif

data class MainDashboardModule(
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

@Composable
fun MainDashboardScreen(
    onLogout: () -> Unit,
    onPlantsClick: () -> Unit,
    onSuppliesClick: () -> Unit,
    onToolsClick: () -> Unit,
    onPotsClick: () -> Unit
) {
    val adaptive = rememberAdaptiveLayoutInfo()
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val modules = listOf(
        MainDashboardModule("Plants", "Live inventory", onPlantsClick),
        MainDashboardModule("Pot", "Containers", onPotsClick),
        MainDashboardModule("Tools", "Watering gear", onToolsClick),
        MainDashboardModule("Supplies", "General stock", onSuppliesClick)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainDashboardBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = if (adaptive.isExpanded) 1100.dp else adaptive.contentMaxWidth)
                .padding(
                    start = adaptive.horizontalPadding,
                    end = adaptive.horizontalPadding,
                    top = topInset + 16.dp,
                    bottom = bottomInset + 12.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Logout",
                    color = MainDashboardText,
                    fontSize = if (adaptive.isCompact) 16.sp else 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .clickable { onLogout() }
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp, bottom = 22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Ashokvatika",
                    color = MainDashboardTitle,
                    fontSize = if (adaptive.isCompact) 34.sp else 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1.2).sp,
                    fontFamily = MainDashboardFont
                )
                Text(
                    text = "Nursery Inventory Management",
                    color = MainDashboardText,
                    fontSize = if (adaptive.isCompact) 24.sp else 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1.0).sp,
                    fontFamily = MainDashboardFont,
                    lineHeight = if (adaptive.isCompact) 28.sp else 40.sp
                )
                Text(
                    text = "Plants, pot, tools, and supplies now use the same inventory flow.",
                    color = MainDashboardHint,
                    fontSize = if (adaptive.isCompact) 15.sp else 18.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MainDashboardFont,
                    lineHeight = if (adaptive.isCompact) 21.sp else 24.sp
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(if (adaptive.isCompact) 12.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (adaptive.isCompact) 12.dp else 16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(modules) { module ->
                    MainDashboardTile(
                        title = module.title,
                        subtitle = module.subtitle,
                        isCompact = adaptive.isCompact,
                        onClick = module.onClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MainDashboardTile(
    title: String,
    subtitle: String,
    isCompact: Boolean,
    onClick: () -> Unit
) {
    val titleSize = if (isCompact) {
        when {
            title.length >= 9 -> 24.sp
            title.length >= 7 -> 26.sp
            else -> 30.sp
        }
    } else {
        when {
            title.length >= 9 -> 42.sp
            title.length >= 7 -> 46.sp
            else -> 52.sp
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isCompact) 190.dp else 280.dp)
            .shadow(10.dp, RoundedCornerShape(30.dp), ambientColor = Color(0x22E29149), spotColor = Color(0x22E29149))
            .clip(RoundedCornerShape(30.dp))
            .background(Brush.verticalGradient(colors = listOf(MainDashboardCardTop, MainDashboardCardBottom)))
            .clickable { onClick() }
            .padding(if (isCompact) 16.dp else 22.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 10.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = titleSize,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1.0).sp,
                fontFamily = MainDashboardFont,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = titleSize
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.95f),
                fontSize = if (isCompact) 14.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = MainDashboardFont,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = if (isCompact) 18.sp else 22.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White.copy(alpha = 0.16f))
                .padding(horizontal = if (isCompact) 16.dp else 18.dp, vertical = if (isCompact) 14.dp else 16.dp)
        ) {
            Text(
                text = "open",
                color = Color.White,
                fontSize = if (isCompact) 14.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = MainDashboardFont
            )
        }
    }
}
