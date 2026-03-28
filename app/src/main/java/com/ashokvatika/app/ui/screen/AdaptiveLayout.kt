package com.ashokvatika.app.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private enum class WidthTier {
    Compact,
    Medium,
    Expanded
}

@Immutable
data class AdaptiveLayoutInfo(
    val screenWidthDp: Int,
    val horizontalPadding: Dp,
    val contentMaxWidth: Dp,
    val imagePanelHeight: Dp,
    val isCompact: Boolean,
    val isMedium: Boolean,
    val isExpanded: Boolean
)

@Composable
fun rememberAdaptiveLayoutInfo(): AdaptiveLayoutInfo {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val tier = remember(screenWidthDp) {
        when {
            screenWidthDp < 600 -> WidthTier.Compact
            screenWidthDp < 900 -> WidthTier.Medium
            else -> WidthTier.Expanded
        }
    }

    return remember(screenWidthDp, tier) {
        AdaptiveLayoutInfo(
            screenWidthDp = screenWidthDp,
            horizontalPadding = when (tier) {
                WidthTier.Compact -> 16.dp
                WidthTier.Medium -> 24.dp
                WidthTier.Expanded -> 32.dp
            },
            contentMaxWidth = when (tier) {
                WidthTier.Compact -> 640.dp
                WidthTier.Medium -> 840.dp
                WidthTier.Expanded -> 1180.dp
            },
            imagePanelHeight = when (tier) {
                WidthTier.Compact -> 240.dp
                WidthTier.Medium -> 280.dp
                WidthTier.Expanded -> 320.dp
            },
            isCompact = tier == WidthTier.Compact,
            isMedium = tier == WidthTier.Medium,
            isExpanded = tier == WidthTier.Expanded
        )
    }
}
