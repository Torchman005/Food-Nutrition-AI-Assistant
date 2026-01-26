package com.example.foodnutritionaiassistant.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    onLoginClick: () -> Unit
) {
    if (!isLoggedIn) {
        LoginPrompt(onLoginClick)
    } else {
        HomeContent()
    }
}

@Composable
fun LoginPrompt(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("请先登录", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFAED581))
        ) {
            Text("去登录", color = Color.White)
        }
    }
}

@Composable
fun HomeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search Bar
        SearchBar()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Calorie Progress
        CalorieProgress()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Weekly Stats
        WeeklyStats()
    }
}

@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, Color(0xFF42A5F5), RoundedCornerShape(25.dp))
            .background(Color.White, RoundedCornerShape(25.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Text("即刻了解你想吃的食材", color = Color.LightGray, fontSize = 14.sp)
    }
}

@Composable
fun CalorieProgress() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(240.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFE8F5E9), // Light Green Background
                radius = size.minDimension / 2,
                style = Stroke(width = 40.dp.toPx())
            )
            drawArc(
                color = Color(0xFFAED581), // Green Progress
                startAngle = -90f,
                sweepAngle = 240f,
                useCenter = false,
                style = Stroke(width = 40.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("0.0", fontSize = 48.sp, fontWeight = FontWeight.Bold)
                Text(" 千卡", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF1F8E9)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Create, // Placeholder for pen
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("点击记录卡路里", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun WeeklyStats() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("01/19 - 01/25", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("饮食记录", color = Color.Gray, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val days = listOf("一", "二", "三", "四", "五", "六", "今")
            days.forEach { day ->
                DayStat(day)
            }
        }
    }
}

@Composable
fun DayStat(day: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Face Icon Placeholder
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color(0xFFE0E0E0), CircleShape)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        Text("0.0", fontSize = 10.sp, color = Color(0xFF7986CB))
        Spacer(modifier = Modifier.height(4.dp))
        
        // Bar
        Box(
            modifier = Modifier
                .width(20.dp)
                .height(80.dp)
                .background(Color(0xFFE8EAF6), RoundedCornerShape(4.dp))
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(day, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3949AB))
    }
}
