package com.example.foodnutritionaiassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.foodnutritionaiassistant.ui.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.Period

@Composable
fun ProfileScreen(
    viewModel: UserViewModel,
    onLoginClick: () -> Unit
) {
    if (!viewModel.isLoggedIn) {
        LoginPrompt(onLoginClick)
    } else {
        ProfileContent(viewModel)
    }
}

@Composable
fun ProfileContent(viewModel: UserViewModel) {
    val userProfile = viewModel.userProfile
    val age = Period.between(userProfile.birthDate, LocalDate.now()).years

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF81C784)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Hello, ${userProfile.nickname}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("开始今天的美好生活之旅吧~", fontSize = 14.sp, color = Color.Gray)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Personal Data Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF66BB6A))
            Spacer(modifier = Modifier.width(8.dp))
            Text("个人数据", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard("${userProfile.height}cm", "身高")
            StatCard("${userProfile.weight}kg", "体重")
            StatCard("${age}岁", "年龄")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Group Field (New Requirement)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("所属群体", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = Color(0xFFA5D6A7),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "健身",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // My Posts
        Text("我的发布", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Common Tools
        Text("常用工具", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        ToolItem("我的设置", Icons.Default.Settings)
        ToolItem("我的收藏", Icons.Default.FavoriteBorder)
        ToolItem("浏览历史", Icons.Default.DateRange) // Placeholder icon
        ToolItem("帮助中心", Icons.Default.Info)
        ToolItem("问题反馈", Icons.Default.Email) // Placeholder icon
    }
}

@Composable
fun StatCard(value: String, label: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(100.dp)
            .height(80.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ToolItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 16.sp)
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}
