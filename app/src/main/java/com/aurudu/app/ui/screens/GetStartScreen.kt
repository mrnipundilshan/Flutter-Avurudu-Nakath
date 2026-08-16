package com.aurudu.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.FillWidth
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurudu.app.R
import com.aurudu.app.ui.theme.AppColors
import com.aurudu.app.ui.theme.AppFonts

@Composable
fun GetStartScreen(onSinhalaSelected: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "sun")
    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
        ),
        label = "sunRotation",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(AppColors.GradientTop, AppColors.Primary),
                )
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        Image(
            painter = painterResource(R.drawable.bg3),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            contentScale = FillWidth,
        )

        Image(
            painter = painterResource(R.drawable.sun),
            contentDescription = null,
            modifier = Modifier
                .offset(y = 90.dp)
                .size(230.dp)
                .rotate(sunRotation),
            contentScale = ContentScale.Crop,
        )

        Image(
            painter = painterResource(R.drawable.sun_face),
            contentDescription = null,
            modifier = Modifier
                .offset(y = 90.dp)
                .size(230.dp),
            contentScale = ContentScale.Crop,
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "අපේ අවුරුදු නැකැත්",
                fontFamily = AppFonts.Disapamok,
                fontWeight = FontWeight.Medium,
                fontSize = 48.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 330.dp),
            )

            Text(
                text = "புத்தாண்டு வாழ்த்துக்கள்",
                fontFamily = AppFonts.Disapamok,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 548.dp),
            )

            Button(
                onClick = onSinhalaSelected,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 630.dp)
                    .size(width = 300.dp, height = 50.dp),
            ) {
                Text(text = "සිංහල", fontSize = 25.sp)
            }

            Button(
                onClick = { /* Tamil not yet supported — matches Flutter placeholder */ },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 694.dp)
                    .size(width = 300.dp, height = 50.dp),
            ) {
                Text(text = "தமிழ்", fontSize = 25.sp)
            }
        }
    }
}
