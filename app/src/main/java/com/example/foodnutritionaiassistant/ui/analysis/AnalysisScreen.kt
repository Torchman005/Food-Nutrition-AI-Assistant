package com.example.foodnutritionaiassistant.ui.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.foodnutritionaiassistant.ui.viewmodel.UserViewModel

@Composable
fun AnalysisScreen(viewModel: UserViewModel) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E9), // Light Green Top
                        Color(0xFFFFFFFF)  // White Bottom
                    )
                )
            )
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Header Title
        Box(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "分析",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // User Info Section
        UserInfoSection(viewModel)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Metrics Grid
        MetricsGrid()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Filter Tabs
        FilterTabs()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Chart Section
        ChartSection()
    }
}

@Composable
fun UserInfoSection(viewModel: UserViewModel) {
    val userProfile = viewModel.userProfile
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar Placeholder
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFF81C784)),
            contentAlignment = Alignment.Center
        ) {
            Text("User", color = Color.White, fontSize = 12.sp)
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (userProfile.nickname.isNotBlank()) userProfile.nickname else "请先登录",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (viewModel.isLoggedIn) "${userProfile.weight}kg" else "-- kg",
                        color = Color(0xFFEF5350),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = "已减重0.0斤，距离目标16.0斤",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text("体重周报", color = Color.Black, fontSize = 12.sp)
            // Red dot
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }
    }
}

@Composable
fun MetricsGrid() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricCard(
            title = "目标达成率",
            value = "0%",
            subtitle = "138天",
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            title = "BMI",
            value = "19.6",
            tag = "理想",
            tagColor = Color(0xFF66BB6A),
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            title = "腰围",
            value = "--",
            modifier = Modifier.weight(0.8f)
        )
        MetricCard(
            title = "基础代谢",
            value = "1267",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    tag: String? = null,
    tagColor: Color = Color.Green,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.height(80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 12.sp, color = Color.Gray)
                if (tag != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        color = tagColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = tag,
                            color = Color.White,
                            fontSize = 8.sp,
                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterTabs() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilterItem(color = Color(0xFFF48FB1), text = "我的")
            FilterItem(color = Color(0xFF9FA8DA), text = "同基数平均")
            FilterItem(color = Color(0xFF80CBC4), text = "同基数优秀")
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("最近7天", fontSize = 12.sp, color = Color.Gray)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun FilterItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ChartSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Chart Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("2026/01/19", fontSize = 12.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).background(Color(0xFFF48FB1), CircleShape))
                        Text(" 我的 116.0", fontSize = 10.sp, modifier = Modifier.padding(start=4.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).background(Color(0xFF9FA8DA), CircleShape))
                        Text(" 平均 116.0", fontSize = 10.sp, modifier = Modifier.padding(start=4.dp))
                    }
                     Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).background(Color(0xFF80CBC4), CircleShape))
                        Text(" 优秀 116.0", fontSize = 10.sp, modifier = Modifier.padding(start=4.dp))
                    }
                }
                Text("单位：斤", fontSize = 12.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Actual Chart Drawing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    // Draw grid lines
                    val steps = 5
                    val stepHeight = height / steps
                    for (i in 0..steps) {
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(0f, i * stepHeight),
                            end = Offset(width, i * stepHeight),
                            strokeWidth = 1f
                        )
                    }
                    
                    // Draw My Weight Line (Pink)
                    val myWeightPath = Path().apply {
                        moveTo(0f, height * 0.1f)
                        quadraticBezierTo(width * 0.5f, height * 0.15f, width, height * 0.2f)
                    }
                    drawPath(
                        path = myWeightPath,
                        color = Color(0xFFF48FB1),
                        style = Stroke(width = 4f)
                    )
                    
                    // Draw Average Line (Purple)
                    val avgPath = Path().apply {
                        moveTo(0f, height * 0.1f)
                        quadraticBezierTo(width * 0.5f, height * 0.12f, width, height * 0.15f)
                    }
                    drawPath(
                        path = avgPath,
                        color = Color(0xFF9FA8DA),
                        style = Stroke(width = 4f)
                    )
                    
                    // Draw Excellent Line (Green)
                    val excPath = Path().apply {
                        moveTo(0f, height * 0.1f)
                        quadraticBezierTo(width * 0.5f, height * 0.4f, width, height * 0.6f)
                    }
                    drawPath(
                        path = excPath,
                        color = Color(0xFF80CBC4),
                        style = Stroke(width = 4f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // X Axis Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("01/19", "01/20", "01/21", "01/22", "01/23", "01/24", "01/25").forEach {
                    Text(it, fontSize = 10.sp, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Stats Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("最近7天", fontSize = 12.sp, color = Color.Gray)
                    Text("-- 斤", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("减重速度", fontSize = 12.sp, color = Color.Gray)
                    Text("--", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            
             // Colored Dots Footer (as seen in image)
             Spacer(modifier = Modifier.height(16.dp))
             Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
             ) {
                val colors = listOf(Color(0xFFFF8A65), Color(0xFFFFD54F), Color(0xFF9575CD), Color(0xFF4DB6AC))
                colors.forEach { 
                    Box(modifier = Modifier.padding(horizontal = 4.dp).size(32.dp).background(it, CircleShape))
                }
             }
        }
    }
}
