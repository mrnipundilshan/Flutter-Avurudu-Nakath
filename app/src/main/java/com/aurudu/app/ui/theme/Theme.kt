package com.aurudu.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    secondary = AppColors.SecondaryCard,
    background = AppColors.Primary,
    surface = AppColors.SecondaryCard,
)

@Composable
fun AvurudunakathTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}