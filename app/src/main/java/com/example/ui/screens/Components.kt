package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SwitchLoader(loadingText: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")
    
    // Animate left joycon translation
    val leftOffset by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "left_joy"
    )

    // Animate right joycon translation
    val rightOffset by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "right_joy"
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(120.dp, 100.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = this.center
                val w = 32f
                val h = 64f
                val corner = 12f
                val spacing = 8f

                // Draw Left Joy-Con (Neon Blue)
                drawRoundRect(
                    color = NeonBlue,
                    topLeft = Offset(center.x - w - spacing + leftOffset, center.y - h/2),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(corner, corner)
                )
                // Analog stick on left (top)
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = Offset(center.x - w - spacing + w/2 + leftOffset, center.y - h/4)
                )

                // Draw Right Joy-Con (Neon Red)
                drawRoundRect(
                    color = NeonRed,
                    topLeft = Offset(center.x + spacing + rightOffset, center.y - h/2),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(corner, corner)
                )
                // Analog stick on right (bottom)
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = Offset(center.x + spacing + w/2 + rightOffset, center.y + h/4)
                )

                // Draw central console screen outline
                drawRoundRect(
                    color = Color.Gray,
                    topLeft = Offset(center.x - 12f, center.y - 20f),
                    size = Size(24f, 40f),
                    style = Stroke(width = 3f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = loadingText,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("loader_text")
        )
    }
}

@Composable
fun SwitchBannerAnim() {
    val infiniteTransition = rememberInfiniteTransition(label = "banner")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(NeonBlue.copy(alpha = 0.15f), NeonRed.copy(alpha = 0.15f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(1.dp, BorderSlate, RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1.3f)) {
                Text(
                    text = "SWITCH TRANSFORMATOR",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ajuste completo de ROMs (.nsz, .nsp, .xci) para 100% de compatibilidade no Yuzu, Sudachi & Skyline do Android.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val scaleW = size.width * pulseScale
                    val centerX = size.width / 2
                    val centerY = size.height / 2

                    // Console drawing with high detail
                    val consoleW = 50f
                    val consoleH = 65f
                    val joyconW = 18f
                    val corner = 8f

                    // Screen background
                    drawRect(
                        color = Color(0xFF1F232D),
                        topLeft = Offset(centerX - consoleW/2, centerY - consoleH/2),
                        size = Size(consoleW, consoleH)
                    )

                    // Left joycon (Blue)
                    drawRoundRect(
                        color = NeonBlue,
                        topLeft = Offset(centerX - consoleW/2 - joyconW, centerY - consoleH/2),
                        size = Size(joyconW, consoleH),
                        cornerRadius = CornerRadius(corner, corner)
                    )
                    // Left stick
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = Offset(centerX - consoleW/2 - joyconW/2, centerY - consoleH/4)
                    )

                    // Right joycon (Red)
                    drawRoundRect(
                        color = NeonRed,
                        topLeft = Offset(centerX + consoleW/2, centerY - consoleH/2),
                        size = Size(joyconW, consoleH),
                        cornerRadius = CornerRadius(corner, corner)
                    )
                    // Right stick
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = Offset(centerX + consoleW/2 + joyconW/2, centerY + consoleH/4)
                    )
                }
            }
        }
    }
}

@Composable
fun SwitchCardAction(
    title: String,
    desc: String,
    icon: @Composable () -> Unit,
    accentColor: Color = NeonBlue,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, BorderSlate, RoundedCornerShape(14.dp))
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = DarkGreySurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
