package com.aurudu.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurudu.app.ui.theme.AppColors
import com.aurudu.app.ui.theme.AppFonts

@Composable
fun CountdownBox(value: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(AppColors.CountdownBoxBg, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = value, fontFamily = AppFonts.Arundathee, fontSize = 25.sp)
    }
}
