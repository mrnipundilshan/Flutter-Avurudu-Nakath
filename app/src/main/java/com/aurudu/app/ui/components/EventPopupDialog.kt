package com.aurudu.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aurudu.app.data.Event
import com.aurudu.app.ui.theme.AppColors
import com.aurudu.app.ui.theme.AppFonts
import com.aurudu.app.util.DateTimeUtils
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@Composable
fun EventPopupDialog(event: Event, onDismiss: () -> Unit) {
    var countdown by remember {
        mutableStateOf(
            DateTimeUtils.countdownParts(DateTimeUtils.parseDateTime(event.date, event.time))
        )
    }

    androidx.compose.runtime.LaunchedEffect(event.id) {
        while (true) {
            countdown = DateTimeUtils.countdownParts(
                DateTimeUtils.parseDateTime(event.date, event.time),
                LocalDateTime.now(),
            )
            delay(1000)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(24.dp))
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black,
                        modifier = Modifier
                            .background(AppColors.CountdownBoxBg, CircleShape)
                            .padding(8.dp),
                    )
                }
            }

            Image(
                painter = painterResource(event.drawableRes),
                contentDescription = event.name,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .size(240.dp)
                    .align(Alignment.CenterHorizontally),
            )

            Text(
                text = event.name,
                fontFamily = AppFonts.Indeewaree,
                fontWeight = FontWeight.Medium,
                fontSize = 26.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            )

            Text(
                text = "${event.date} ${event.time}",
                fontFamily = AppFonts.Arundathee,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .background(AppColors.SecondaryCard, RoundedCornerShape(16.dp))
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PopupCountdownItem("දින:", countdown.days)
                PopupCountdownItem("පැය:", countdown.hours)
                PopupCountdownItem("මිනි:", countdown.minutes)
                PopupCountdownItem("තත්:", countdown.seconds)
            }

            Text(
                text = event.description,
                fontFamily = AppFonts.Ganganee,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            )
        }
    }
}

@Composable
private fun PopupCountdownItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontFamily = AppFonts.Indeewaree,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.Black,
            maxLines = 1,
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .size(54.dp)
                .background(AppColors.CountdownBoxBg, RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                fontFamily = AppFonts.Arundathee,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black,
            )
        }
    }
}
