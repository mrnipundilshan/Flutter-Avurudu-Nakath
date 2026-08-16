package com.aurudu.app.ui.screens

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.delay

@Composable
fun GetStartScreen(onContinue: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "sun")
    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
        ),
        label = "sunRotation",
    )
    val sunBreathe by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sunBreathe",
    )

    val pageOpacity = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        pageOpacity.animateTo(1f, animationSpec = tween(durationMillis = 800))
        delay(2000)
        onContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(pageOpacity.value)
            .background(
                Brush.radialGradient(
                    colors = listOf(AppColors.GradientTop, AppColors.Primary),
                )
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        Image(
            painter = painterResource(R.drawable.bg1),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.4f)
                .alpha(0.5f),
            contentScale = ContentScale.Crop,
        )

        Image(
            painter = painterResource(R.drawable.bg2),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.4f)
                .alpha(0.5f),
            contentScale = ContentScale.Crop,
        )

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
                .rotate(sunRotation)
                .scale(sunBreathe),
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

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // The offset-positioned children below don't contribute to measured
            // content height (Modifier.offset is draw/placement-only), so without
            // this explicit min height the verticalScroll above would have zero
            // scrollable range and the title text could be clipped off-screen.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 780.dp),
            ) {
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
            }
        }
    }
}
