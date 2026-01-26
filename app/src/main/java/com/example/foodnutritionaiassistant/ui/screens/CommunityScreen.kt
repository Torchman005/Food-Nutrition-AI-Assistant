package com.example.foodnutritionaiassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommunityScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(2) } // Default to "幼儿" as in image
    val tabs = listOf("养生", "健身", "幼儿")
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Search Bar (reused style, maybe simplified)
        Box(modifier = Modifier.padding(16.dp)) {
             Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(1.dp, Color(0xFF42A5F5), RoundedCornerShape(20.dp))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("请输入内容", color = Color.LightGray, fontSize = 14.sp)
            }
        }
        
        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            divider = {},
            indicator = { tabPositions ->
                // Custom indicator or just hide default
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) Color.Black else Color.Gray,
                            modifier = if (selectedTabIndex == index) {
                                Modifier
                                    .background(Color(0xFFA5D6A7), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            } else {
                                Modifier
                            }
                        )
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Content Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(10) { index ->
                CommunityPostItem(index)
            }
        }
    }
}

@Composable
fun CommunityPostItem(index: Int) {
    Column {
        // Image Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Varying height in real staggered grid, fixed for now
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (index % 2 == 0) "老年人低糖/低盐/低脂食谱" else "补钙/控压饮食搭配",
            fontSize = 14.sp,
            maxLines = 2
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.Gray, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("小黄花", fontSize = 12.sp, color = Color.Gray)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(2.dp))
                Text("30", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
