package com.example.foodnutritionaiassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.foodnutritionaiassistant.ui.viewmodel.GroupCategory

import com.example.foodnutritionaiassistant.ui.viewmodel.CommunityViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.LaunchedEffect
import com.example.foodnutritionaiassistant.data.model.Post
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyPostsScreen(
    userProfile: com.example.foodnutritionaiassistant.ui.viewmodel.UserProfile,
    communityViewModel: CommunityViewModel,
    viewModel: UserViewModel,
    onBack: () -> Unit
) {
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedPosts = remember { androidx.compose.runtime.mutableStateListOf<String>() }
    // Explicitly specify the type to avoid inference errors
    val selectedPostForDetailState = remember { mutableStateOf<Post?>(null) }
    var selectedPostForDetail by selectedPostForDetailState
    
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    // Trigger loading my posts whenever the screen is composed
    LaunchedEffect(userProfile.id) {
        userProfile.id?.let { communityViewModel.loadMyPosts(it) }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelectionMode) {
                    IconButton(onClick = { 
                        isSelectionMode = false 
                        selectedPosts.clear()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel Selection")
                    }
                    Text(
                        text = "已选择 ${selectedPosts.size} 项",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (selectedPosts.isNotEmpty()) {
                        IconButton(onClick = { showDeleteConfirmDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Selected", tint = Color.Red)
                        }
                    }
                } else {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "我的发布",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (communityViewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(communityViewModel.myPosts) { post ->
                        val isSelected = selectedPosts.contains(post.id)
                        
                        Box(modifier = Modifier.fillMaxWidth()) {
                            CommunityPostItem(
                                post = post,
                                onClick = {
                                    if (isSelectionMode) {
                                        if (isSelected) {
                                            post.id?.let { selectedPosts.remove(it) }
                                        } else {
                                            post.id?.let { selectedPosts.add(it) }
                                        }
                                    } else {
                                        selectedPostForDetail = post
                                    }
                                },
                                onLike = {}, // Disable like in this view or handle it
                                onFavorite = {}, // Disable favorite in this view or handle it
                                currentUserId = userProfile.id ?: "",
                                showFavoriteIcon = false
                            )
                            
                            // Selection Overlay/Long Press Handler
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .combinedClickable(
                                        onClick = {
                                            if (isSelectionMode) {
                                                if (isSelected) {
                                                    post.id?.let { selectedPosts.remove(it) }
                                                } else {
                                                    post.id?.let { selectedPosts.add(it) }
                                                }
                                            } else {
                                                selectedPostForDetail = post
                                            }
                                        },
                                        onLongClick = {
                                            if (!isSelectionMode) {
                                                isSelectionMode = true
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                post.id?.let { selectedPosts.add(it) }
                                            }
                                        }
                                    )
                                    .background(if (isSelected) Color.Black.copy(alpha = 0.3f) else Color.Transparent, RoundedCornerShape(8.dp))
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = Color(0xFF66BB6A),
                                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        selectedPostForDetail?.let { post ->
            val latestPost = communityViewModel.myPosts.find { it.id == post.id } ?: post
            PostDetailDialog(
                post = latestPost,
                viewModel = communityViewModel,
                userViewModel = viewModel,
                onDismiss = { selectedPostForDetail = null }
            )
        }

        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("确认删除") },
                text = { Text("确定要删除这 ${selectedPosts.size} 条帖子吗？删除后无法恢复。") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            communityViewModel.deletePosts(selectedPosts.toList())
                            showDeleteConfirmDialog = false
                            isSelectionMode = false
                            selectedPosts.clear()
                        }
                    ) {
                        Text("删除", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: UserViewModel,
    communityViewModel: CommunityViewModel = viewModel(),
    onLoginClick: () -> Unit
) {
    if (!viewModel.isLoggedIn) {
        LoginPrompt(onLoginClick)
    } else {
        ProfileContent(viewModel, communityViewModel)
    }
}



@Composable
fun ProfileContent(viewModel: UserViewModel, communityViewModel: CommunityViewModel) {
    val userProfile = viewModel.userProfile
    val age = Period.between(userProfile.birthDate, LocalDate.now()).years
    var showGroupSelectionDialog by remember { mutableStateOf(false) }
    var showFavorites by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showMyPosts by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    // Explicitly specify the type to avoid inference errors
    val selectedFavoritePostState = remember { mutableStateOf<Post?>(null) }
    var selectedFavoritePost by selectedFavoritePostState
    
    // State for selected history post
    val selectedHistoryPostState = remember { mutableStateOf<Post?>(null) }
    var selectedHistoryPost by selectedHistoryPostState

    if (showFavorites) {
        // Simple Favorites List Screen
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showFavorites = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text("我的收藏", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                
                LaunchedEffect(userProfile.id) {
                    userProfile.id?.let { communityViewModel.loadFavoritePosts(it) }
                }
                
                if (communityViewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        // Filter out posts that are no longer favorited by the user locally
                        val currentUserId = userProfile.id ?: ""
                        val displayPosts = communityViewModel.favoritePosts.filter { 
                            it.favoritedUserIds.contains(currentUserId)
                        }

                        items(displayPosts.size, key = { index -> displayPosts[index].id ?: index }) { index ->
                            val post = displayPosts[index]
                            CommunityPostItem(
                                post = post,
                                onClick = { selectedFavoritePost = post },
                                onLike = { p -> 
                                    communityViewModel.toggleLike(p, currentUserId) 
                                },
                                onFavorite = { p -> 
                                    communityViewModel.toggleFavorite(p, currentUserId)
                                },
                                currentUserId = currentUserId,
                                showFavoriteIcon = false // Hide favorite icon in favorites list
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            
            selectedFavoritePost?.let { post ->
                // Ensure we get the latest state of the post from the view model list
                // This is crucial because toggleFavorite updates the post in viewModel.posts / viewModel.favoritePosts
                // but 'post' here might be a stale copy if selectedFavoritePost isn't updated.
                // However, since we are observing viewModel.favoritePosts, we can find the updated post there.
                val latestPost = communityViewModel.favoritePosts.find { it.id == post.id } ?: post

                PostDetailDialog(
                    post = latestPost,
                    viewModel = communityViewModel,
                    userViewModel = viewModel,
                    onDismiss = { selectedFavoritePost = null }
                )
            }
        }
        return // Return early to show favorites screen
    }
    
    if (showHistory) {
        // History List Screen
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showHistory = false }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text("浏览历史", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    if (communityViewModel.historyPosts.isNotEmpty()) {
                        IconButton(onClick = {
                            showClearHistoryDialog = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear History", tint = Color.Gray)
                        }
                    }
                }
                
                LaunchedEffect(userProfile.id) {
                    userProfile.id?.let { communityViewModel.loadViewHistory(it) }
                }
                
                if (communityViewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        val currentUserId = userProfile.id ?: ""
                        items(communityViewModel.historyPosts.size) { index ->
                            val post = communityViewModel.historyPosts[index]
                            CommunityPostItem(
                                post = post,
                                onClick = { selectedHistoryPost = post },
                                onLike = { p -> communityViewModel.toggleLike(p, currentUserId) },
                                onFavorite = { p -> communityViewModel.toggleFavorite(p, currentUserId) },
                                currentUserId = currentUserId,
                                showFavoriteIcon = true // Show icon, state managed by view model
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            
            selectedHistoryPost?.let { post ->
                val latestPost = communityViewModel.historyPosts.find { it.id == post.id } ?: post
                PostDetailDialog(
                    post = latestPost,
                    viewModel = communityViewModel,
                    userViewModel = viewModel,
                    onDismiss = { selectedHistoryPost = null }
                )
            }

            if (showClearHistoryDialog) {
                AlertDialog(
                    onDismissRequest = { showClearHistoryDialog = false },
                    title = { Text("确认清空") },
                    text = { Text("确定要清空所有浏览历史吗？") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                userProfile.id?.let { communityViewModel.clearViewHistory(it) }
                                showClearHistoryDialog = false
                            }
                        ) {
                            Text("确定")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearHistoryDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }
        }
        return
    }

    if (showMyPosts) {
        MyPostsScreen(
            userProfile = userProfile,
            communityViewModel = communityViewModel,
            viewModel = viewModel,
            onBack = { showMyPosts = false }
        )
        return
    }

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
        
        // Group Field (Editable)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showGroupSelectionDialog = true }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("所属群体", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = when(userProfile.groupCategory) {
                            GroupCategory.HEALTH -> Color(0xFFA5D6A7)
                            GroupCategory.FITNESS -> Color(0xFF90CAF9)
                            GroupCategory.TODDLER -> Color(0xFFFFF59D)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = userProfile.groupCategory.displayName,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = if (userProfile.groupCategory == GroupCategory.TODDLER) Color.Black else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp)) // Added right padding
                }
            }
        }
        
        if (showGroupSelectionDialog) {
            AlertDialog(
                onDismissRequest = { showGroupSelectionDialog = false },
                title = { Text("修改所属群体") },
                text = {
                    Column {
                        GroupCategory.entries.forEach { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateUserGroupInDb(category)
                                        showGroupSelectionDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = userProfile.groupCategory == category,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(category.displayName)
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showGroupSelectionDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Common Tools
        Text("常用工具", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        ToolItem("我的发布", Icons.Default.Edit, onClick = { showMyPosts = true })
        ToolItem("我的设置", Icons.Default.Settings)
        ToolItem("我的收藏", Icons.Default.Star, onClick = { showFavorites = true })
        ToolItem("浏览历史", Icons.Default.DateRange, onClick = { showHistory = true })
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
fun ToolItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
