package com.aurudu.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurudu.app.data.Event
import com.aurudu.app.ui.theme.AppColors
import com.aurudu.app.ui.theme.AppFonts

@Composable
fun EventListItem(event: Event, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(AppColors.SecondaryCard, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(
            text = event.name,
            fontFamily = AppFonts.Indeewaree,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            color = Color.Black,
        )
        Text(
            text = event.description,
            fontFamily = AppFonts.Ganganee,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}
