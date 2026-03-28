package com.ashokvatika.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ashokvatika.app.model.InventoryRecord
import java.text.NumberFormat
import java.util.Locale

private val ListBackground = Color(0xFFF7F7F8)
private val ListSurface = Color(0xFFFFFFFF)
private val ListTitle = Color(0xFFFF8A18)
private val ListText = Color(0xFF374055)
private val ListHint = Color(0xFFA1A7B6)
private val ListCardTop = Color(0xFFF7BF77)
private val ListCardBottom = Color(0xFFEA964D)
private val ListFont = FontFamily.SansSerif

@Composable
fun InventoryListScreen(
    title: String,
    items: List<InventoryRecord>,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onAddItem: () -> Unit,
    onItemClick: (InventoryRecord) -> Unit
) {
    val adaptive = rememberAdaptiveLayoutInfo()
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val itemSummary = if (items.isEmpty()) {
        "No items yet. Tap add items to create the first one."
    } else {
        "${items.size} ${if (items.size == 1) "item" else "items"} saved in $title."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ListBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = if (adaptive.isExpanded) 960.dp else adaptive.contentMaxWidth)
                .padding(
                    start = adaptive.horizontalPadding,
                    end = adaptive.horizontalPadding,
                    top = topInset + 16.dp,
                    bottom = bottomInset + 12.dp
                )
        ) {
            InventoryListTopBar(onBack = onBack, onLogout = onLogout, isCompact = adaptive.isCompact)

            Spacer(modifier = Modifier.height(if (adaptive.isCompact) 18.dp else 24.dp))

            Text(
                text = "Ashokvatika",
                color = ListTitle,
                fontSize = if (adaptive.isCompact) 32.sp else 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1.2).sp,
                fontFamily = ListFont
            )
            Text(
                text = "$title inventory list",
                color = ListText,
                fontSize = if (adaptive.isCompact) 24.sp else 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp,
                fontFamily = ListFont
            )
            Text(
                text = itemSummary,
                color = ListHint,
                fontSize = if (adaptive.isCompact) 15.sp else 17.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = ListFont,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(if (adaptive.isCompact) 18.dp else 24.dp))

            AddItemButton(isCompact = adaptive.isCompact, onClick = onAddItem)

            Spacer(modifier = Modifier.height(if (adaptive.isCompact) 16.dp else 20.dp))

            if (items.isEmpty()) {
                EmptyInventoryState(title = title, isCompact = adaptive.isCompact)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(if (adaptive.isCompact) 12.dp else 16.dp)
                ) {
                    items(items.sortedByDescending { it.id }) { item ->
                        InventoryListCard(item = item, isCompact = adaptive.isCompact, onClick = { onItemClick(item) })
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryListTopBar(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    isCompact: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Back",
            color = ListHint,
            fontSize = if (isCompact) 16.sp else 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = ListFont,
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .clickable { onBack() }
                .padding(horizontal = 4.dp, vertical = 8.dp)
        )
        Text(
            text = "List",
            color = ListHint,
            fontSize = if (isCompact) 16.sp else 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = ListFont
        )
        Text(
            text = "Logout",
            color = ListHint,
            fontSize = if (isCompact) 16.sp else 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = ListFont,
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .clickable { onLogout() }
                .padding(horizontal = 4.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun AddItemButton(
    isCompact: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(28.dp), ambientColor = Color(0x22E29149), spotColor = Color(0x22E29149))
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.horizontalGradient(listOf(ListCardTop, ListCardBottom)))
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = if (isCompact) 16.dp else 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Add items",
            color = Color.White,
            fontSize = if (isCompact) 20.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.8).sp,
            fontFamily = ListFont
        )
        Text(
            text = "+",
            color = Color.White,
            fontSize = if (isCompact) 28.sp else 34.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = ListFont
        )
    }
}

@Composable
private fun EmptyInventoryState(
    title: String,
    isCompact: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(30.dp), ambientColor = Color(0x14000000), spotColor = Color(0x14000000))
            .clip(RoundedCornerShape(30.dp))
            .background(ListSurface)
            .padding(horizontal = 22.dp, vertical = if (isCompact) 28.dp else 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "No $title items yet",
            color = ListText,
            fontSize = if (isCompact) 24.sp else 30.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.8).sp,
            fontFamily = ListFont
        )
        Text(
            text = "Add your first product from this panel and it will appear here automatically.",
            color = ListHint,
            fontSize = if (isCompact) 15.sp else 17.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = ListFont
        )
    }
}

@Composable
private fun InventoryListCard(
    item: InventoryRecord,
    isCompact: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp), ambientColor = Color(0x14000000), spotColor = Color(0x14000000))
            .clip(RoundedCornerShape(28.dp))
            .background(ListSurface)
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.photoUri != null) {
            AsyncImage(
                model = item.photoUri,
                contentDescription = item.title,
                modifier = Modifier
                    .size(if (isCompact) 74.dp else 90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF6EEE4)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 74.dp else 90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF9F2E8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.title.take(1).uppercase(),
                    color = ListTitle,
                    fontSize = if (isCompact) 28.sp else 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = ListFont
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.title,
                color = ListText,
                fontSize = if (isCompact) 20.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp,
                fontFamily = ListFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Instock",
                    color = ListText,
                    fontSize = if (isCompact) 18.sp else 22.sp,
                    fontWeight = FontWeight.Thin,
                    letterSpacing = (-0.4).sp,
                    fontFamily = ListFont,
                    lineHeight = if (isCompact) 18.sp else 20.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = formatCount(item.stock),
                    color = ListText,
                    fontSize = if (isCompact) 36.sp else 44.sp,
                    fontWeight = FontWeight.Thin,
                    letterSpacing = (-0.4).sp,
                    fontFamily = ListFont,
                    lineHeight = if (isCompact) 36.sp else 42.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { InventoryStatPill(label = "batch", value = formatCount(item.batch)) }
                item { InventoryStatPill(label = "sale", value = formatCount(item.sale)) }
                item { InventoryStatPill(label = "rent", value = formatCount(item.rent)) }
            }
        }
    }
}

@Composable
private fun InventoryStatPill(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFBF6EF))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            color = ListHint,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = ListFont
        )
        Text(
            text = value,
            color = ListText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = ListFont
        )
    }
}

private fun formatCount(value: Int): String = NumberFormat.getIntegerInstance(Locale.getDefault()).format(value)


