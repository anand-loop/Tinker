package com.anandj.tinker.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimen(
    val contentPadding: Dp,
)

private val defaultDimen =
    Dimen(
        contentPadding = 16.dp,
    )

internal val LocalDimen = staticCompositionLocalOf { defaultDimen }
