package com.example.foodnutritionaiassistant.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.foodnutritionaiassistant.data.model.GroupCategory
import com.example.foodnutritionaiassistant.data.model.Post
import com.example.foodnutritionaiassistant.data.model.Comment
import com.example.foodnutritionaiassistant.ui.viewmodel.CommunityViewModel
import com.example.foodnutritionaiassistant.ui.viewmodel.UserViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import java.text.SimpleDateFormat
import java.util.Locale

import android.widget.Toast
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: CommunityViewModel = viewModel(),
    userViewModel: UserViewModel
) {
    val tabs = GroupCategory.entries
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    val refreshState = rememberPullToRefreshState()
    
    // Auto-select tab based on user's group if available, but let user switch
    LaunchedEffect(Unit) {
        if (!viewModel.isCategoryInitialized) {
            val userGroup = userViewModel.userProfile.groupCategory
            val targetCategory = when (userGroup.name) {
                "HEALTH" -> GroupCategory.WELLNESS
                "FITNESS" -> GroupCategory.FITNESS
                "TODDLER" -> GroupCategory.TODDLER
                else -> GroupCategory.WELLNESS
            }
            viewModel.loadPosts(targetCategory)
            viewModel.isCategoryInitialized = true
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar
            Box(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("搜索感兴趣的内容", color = Color.Gray, fontSize = 14.sp)
                }
            }
            
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOf(viewModel.currentCategory),
                containerColor = Color.Transparent,
                edgePadding = 16.dp,
                divider = {},
                indicator = {}
            ) {
                tabs.forEach { category ->
                    val isSelected = viewModel.currentCategory == category
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.loadPosts(category) },
                        text = {
                            Text(
                                text = category.displayName,
                                fontSize = if (isSelected) 18.sp else 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else Color.Gray
                            )
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Content Grid with Pull to Refresh
            PullToRefreshBox(
                isRefreshing = viewModel.isLoading,
                onRefresh = { viewModel.loadPosts(viewModel.currentCategory) },
                state = refreshState,
                modifier = Modifier.weight(1f)
            ) {
                if (viewModel.posts.isEmpty() && viewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF66BB6A))
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(viewModel.posts) { post ->
                            CommunityPostItem(
                                post = post, 
                                onClick = { selectedPost = post },
                                onLike = { p -> viewModel.toggleLike(p, userViewModel.userProfile.id ?: "") },
                                onFavorite = { p -> viewModel.toggleFavorite(p, userViewModel.userProfile.id ?: "") },
                                currentUserId = userViewModel.userProfile.id ?: ""
                            )
                        }
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showCreatePostDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF66BB6A),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Post")
        }

        if (showCreatePostDialog) {
            CreatePostDialog(
                viewModel = viewModel,
                userViewModel = userViewModel,
                onDismiss = { 
                    // Save draft logic could be here
                    if (viewModel.postTitle.isNotBlank() || viewModel.postContent.isNotBlank()) {
                         viewModel.saveDraft(
                             authorId = userViewModel.userProfile.id ?: "temp_id",
                             authorNickname = userViewModel.userProfile.nickname
                         )
                    }
                    showCreatePostDialog = false 
                },
                onSuccess = { showCreatePostDialog = false }
            )
        }
        
        selectedPost?.let { post ->
            // Ensure we use the reactive post object from the ViewModel list
            val latestPost = viewModel.posts.find { it.id == post.id } ?: post
            
            // Record View History
            LaunchedEffect(latestPost.id) {
                latestPost.id?.let { viewModel.recordView(it, userViewModel.userProfile.id ?: "") }
            }
            
            PostDetailDialog(
                post = latestPost,
                viewModel = viewModel,
                userViewModel = userViewModel,
                onDismiss = { selectedPost = null }
            )
        }
    }
}

@Composable
fun CommunityPostItem(
    post: Post,
    onClick: () -> Unit,
    onLike: (Post) -> Unit = {},
    onFavorite: (Post) -> Unit = {},
    currentUserId: String = "",
    showFavoriteIcon: Boolean = true
) {
    val isLiked = post.likedUserIds.contains(currentUserId)
    val isFavorited = post.favoritedUserIds.contains(currentUserId)

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            // Cover Image
            if (post.images.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.images.firstOrNull())
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp, max = 250.dp) // Dynamic height simulation
                )
            }
            
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = post.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        // Author Avatar
                         AsyncImage(
                            model = post.authorAvatar ?: "https://via.placeholder.com/20", // Placeholder
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                             contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = post.authorName, 
                            fontSize = 10.sp, 
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Like
                        Icon(
                            if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null, 
                            modifier = Modifier.size(16.dp).clickable { onLike(post) }, 
                            tint = if (isLiked) Color.Red else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(post.likeCount.toString(), fontSize = 10.sp, color = Color.Gray)
                        
                        if (showFavoriteIcon) {
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // Favorite
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp).clickable { onFavorite(post) },
                                tint = if (isFavorited) Color(0xFFFFC107) else Color.Gray // Amber for favorited
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePostDialog(
    viewModel: CommunityViewModel,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    
    // Auto-set the category based on current tab selection when dialog opens
    LaunchedEffect(Unit) {
        viewModel.selectedGroup = viewModel.currentCategory
        viewModel.resetPublishStatus()
    }
    
    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 9)
    ) { uris ->
        uris.forEach { uri ->
            viewModel.addImage(uri.toString())
        }
    }
    
    // Handle success
    LaunchedEffect(viewModel.publishSuccess) {
        if (viewModel.publishSuccess == true) {
            Toast.makeText(context, "发布成功", Toast.LENGTH_SHORT).show()
            onSuccess()
        } else if (viewModel.publishSuccess == false) {
             Toast.makeText(context, "发布失败，请重试", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                    Text("发布笔记", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    TextButton(
                        onClick = { 
                             viewModel.publishPost(
                                 context = context,
                                 authorId = userViewModel.userProfile.id ?: "temp_id",
                                 authorNickname = userViewModel.userProfile.nickname,
                                 authorAvatar = null // Pass real avatar if available
                             )
                        },
                        enabled = !viewModel.isPublishing
                    ) {
                        if (viewModel.isPublishing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("发布", color = Color(0xFF66BB6A), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 16.dp)
                ) {
                    // Images
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                         // Real Image Add Button
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .clickable { 
                                     photoPickerLauncher.launch(
                                         PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                     )
                                }
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Image", tint = Color.Gray)
                        }
                        
                        viewModel.postImages.forEach { uri ->
                            Box(modifier = Modifier.size(100.dp)) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                                )
                                IconButton(
                                    onClick = { viewModel.removeImage(uri) },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Title
                    OutlinedTextField(
                        value = viewModel.postTitle,
                        onValueChange = { viewModel.postTitle = it },
                        placeholder = { Text("填写标题会有更多人赞哦~") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    
                    Divider(color = Color(0xFFEEEEEE))

                    // Content
                    OutlinedTextField(
                        value = viewModel.postContent,
                        onValueChange = { viewModel.postContent = it },
                        placeholder = { Text("添加正文") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        )
                    )
                    
                    Divider(color = Color(0xFFEEEEEE))
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Tags
                    Text("添加标签", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.usedTags.forEach { tag ->
                            val isSelected = viewModel.postTags.contains(tag)
                            SuggestionChip(
                                onClick = { 
                                    if (isSelected) viewModel.removeTag(tag) else viewModel.addTag(tag)
                                },
                                label = { Text(tag) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                                    labelColor = if (isSelected) Color(0xFF66BB6A) else Color.Black
                                ),
                                border = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostDetailDialog(
    post: Post,
    viewModel: CommunityViewModel,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var commentToDelete by remember { mutableStateOf<Comment?>(null) }

    // Load comments on enter
    LaunchedEffect(post.id) {
        if (post.id != null) {
            viewModel.loadComments(post.id)
        }
    }

    if (showDeleteDialog && commentToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除评论") },
            text = { Text("确定要删除这条评论吗？") },
            confirmButton = {
                TextButton(onClick = {
                    commentToDelete?.id?.let { viewModel.deleteComment(it) }
                    showDeleteDialog = false
                    commentToDelete = null
                }) {
                    Text("删除", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    // Author info
                    AsyncImage(
                        model = post.authorAvatar ?: "https://via.placeholder.com/32",
                        contentDescription = null,
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.Gray),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(post.authorName, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF66BB6A)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("关注", color = Color(0xFF66BB6A), fontSize = 12.sp)
                    }
                }

                Divider()

                // Main Content (Scrollable)
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // 1. Images
                    if (post.images.isNotEmpty()) {
                        item {
                             AsyncImage(
                                model = post.images.firstOrNull(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // 2. Title & Content
                    item {
                        Text(post.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(post.content, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 3. Tags & Date
                    item {
                        Row {
                            post.tags.forEach { tag ->
                                Text("#$tag ", color = Color(0xFF1E88E5))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("发布于 ${post.createdAt ?: "未知时间"}", color = Color.Gray, fontSize = 12.sp)
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                    }

                    // 4. Comments Header
                    item {
                        Text("共 ${viewModel.comments.size} 条评论", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 5. Comments List (Using Root Comments)
                    items(viewModel.rootComments) { comment ->
                        val isCommentLiked = userViewModel.userProfile.id?.let { userId -> 
                            comment.likedUserIds?.contains(userId) == true 
                        } == true
                        val isAuthor = comment.authorId == userViewModel.userProfile.id
                        val replies = viewModel.repliesMap[comment.id] ?: emptyList()
                        val isExpanded = viewModel.expandedComments.contains(comment.id)
                        
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Root Comment Item
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = { viewModel.replyToComment = comment },
                                        onLongClick = {
                                            if (isAuthor) {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                commentToDelete = comment
                                                showDeleteDialog = true
                                            }
                                        }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                AsyncImage(
                                    model = comment.authorAvatar ?: "https://via.placeholder.com/32",
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.Gray),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(comment.authorName, fontSize = 12.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(comment.content, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(comment.createdAt ?: "", fontSize = 10.sp, color = Color.LightGray)
                                        Spacer(modifier = Modifier.weight(1f))
                                        
                                        // Like Button
                                    Icon(
                                        imageVector = if (isCommentLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Like Comment",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { 
                                                 userViewModel.userProfile.id?.let { userId ->
                                                     // Optimistic Update
                                                     val newLiked = !isCommentLiked
                                                     val newCount = if (newLiked) comment.likeCount + 1 else maxOf(0, comment.likeCount - 1)
                                                     val newLikedIds = if (newLiked) {
                                                         (comment.likedUserIds ?: emptyList()) + userId
                                                     } else {
                                                         (comment.likedUserIds ?: emptyList()) - userId
                                                     }
                                                     
                                                     // Update local list directly for immediate UI feedback
                                                     val updatedComment = comment.copy(likeCount = newCount, likedUserIds = newLikedIds)
                                                     val index = viewModel.comments.indexOfFirst { it.id == comment.id }
                                                     if (index != -1) {
                                                         viewModel.comments[index] = updatedComment
                                                     }
                                                     
                                                     // Trigger API
                                                     viewModel.toggleCommentLike(comment, userId)
                                                 }
                                            },
                                        tint = if (isCommentLiked) Color.Red else Color.Gray
                                    )
                                        if (comment.likeCount > 0) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(comment.likeCount.toString(), fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }
                                    
                                    // Expand/Collapse Replies Button
                                    if (replies.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier
                                                .clickable { viewModel.toggleCommentExpand(comment.id ?: "") }
                                                .padding(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isExpanded) "收起回复" else "展开 ${replies.size} 条回复",
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Replies List
                            if (isExpanded && replies.isNotEmpty()) {
                                Column(modifier = Modifier.padding(start = 40.dp)) { // Indent
                                    replies.forEach { reply ->
                                        val isReplyLiked = userViewModel.userProfile.id?.let { userId -> 
                                            reply.likedUserIds?.contains(userId) == true 
                                        } == true
                                        val isReplyAuthor = reply.authorId == userViewModel.userProfile.id
                                        
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .combinedClickable(
                                                    onClick = { viewModel.replyToComment = reply },
                                                    onLongClick = {
                                                        if (isReplyAuthor) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            commentToDelete = reply
                                                            showDeleteDialog = true
                                                        }
                                                    }
                                                )
                                                .padding(vertical = 6.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            AsyncImage(
                                                model = reply.authorAvatar ?: "https://via.placeholder.com/24",
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.Gray), // Smaller avatar
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(reply.authorName, fontSize = 11.sp, color = Color.Gray) // Smaller font
                                                    if (!reply.replyToUserName.isNullOrEmpty()) {
                                                        Text(" 回复 ", fontSize = 11.sp, color = Color.Gray)
                                                        Text("@${reply.replyToUserName}", fontSize = 11.sp, color = Color(0xFF1E88E5), fontWeight = FontWeight.Bold) // Highlight
                                                    }
                                                }
                                                
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(reply.content, fontSize = 13.sp) // Smaller content font
                                                Spacer(modifier = Modifier.height(2.dp))
                                                
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(reply.createdAt ?: "", fontSize = 10.sp, color = Color.LightGray)
                                                    Spacer(modifier = Modifier.weight(1f))
                                                    
                                                    Icon(
                                                        imageVector = if (isReplyLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                        contentDescription = "Like Reply",
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .clickable { 
                                                                 userViewModel.userProfile.id?.let { userId ->
                                                                     // Optimistic Update
                                                                     val newLiked = !isReplyLiked
                                                                     val newCount = if (newLiked) reply.likeCount + 1 else maxOf(0, reply.likeCount - 1)
                                                                     val newLikedIds = if (newLiked) {
                                                                         (reply.likedUserIds ?: emptyList()) + userId
                                                                     } else {
                                                                         (reply.likedUserIds ?: emptyList()) - userId
                                                                     }
                                                                     
                                                                     val updatedReply = reply.copy(likeCount = newCount, likedUserIds = newLikedIds)
                                                                     val index = viewModel.comments.indexOfFirst { it.id == reply.id }
                                                                     if (index != -1) {
                                                                         viewModel.comments[index] = updatedReply
                                                                     }
                                                                     
                                                                     viewModel.toggleCommentLike(reply, userId)
                                                                 }
                                                            },
                                                        tint = if (isReplyLiked) Color.Red else Color.Gray
                                                    )
                                                    if (reply.likeCount > 0) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(reply.likeCount.toString(), fontSize = 10.sp, color = Color.Gray)
                                                    }
                                                }
                                            }
                                        }
                                        Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(start = 32.dp))
                                    }
                                }
                            }
                            
                            Divider(color = Color(0xFFEEEEEE))
                        }
                    }
                }
                
                // Reply Indicator
                if (viewModel.replyToComment != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "回复 @${viewModel.replyToComment?.authorName}", 
                            fontSize = 12.sp, 
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.Default.Close, 
                            contentDescription = "Cancel Reply", 
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { viewModel.replyToComment = null }
                        )
                    }
                }

                // Bottom Bar (Comment Input)
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Input Field
                    OutlinedTextField(
                        value = viewModel.commentContent,
                        onValueChange = { viewModel.commentContent = it },
                        placeholder = { Text("说点什么...", color = Color.Gray) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 40.dp, max = 100.dp), // Auto-expand logic handled by TextField
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5)
                        )
                    )
                    
                    // Send Button (only show if has content)
                    if (viewModel.commentContent.isNotBlank()) {
                         Spacer(modifier = Modifier.width(8.dp))
                         IconButton(
                             onClick = { 
                                 if (post.id != null) {
                                     viewModel.sendComment(
                                         postId = post.id,
                                         authorId = userViewModel.userProfile.id ?: "temp_id",
                                         authorName = userViewModel.userProfile.nickname,
                                         authorAvatar = null // Pass real avatar
                                     )
                                 }
                             },
                             enabled = !viewModel.isSendingComment
                         ) {
                             if (viewModel.isSendingComment) {
                                 CircularProgressIndicator(modifier = Modifier.size(24.dp))
                             } else {
                                 Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF66BB6A))
                             }
                         }
                    } else {
                        // Show actions when no text
                        val currentUserId = userViewModel.userProfile.id ?: ""
                        // Use local state to check if liked/favorited, but post object might be stale.
                        // Ideally, post object should be reactive. 
                        // Since post is passed as parameter, we need to check viewModel's list for updated post or rely on recomposition.
                        // If PostDetailDialog is recomposed with updated post, it works.
                        
                        val isLiked = post.likedUserIds.contains(currentUserId)
                        val isFavorited = post.favoritedUserIds.contains(currentUserId)

                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.clickable { viewModel.toggleLike(post, currentUserId) },
                            tint = if (isLiked) Color.Red else Color.Gray // Changed Black to Gray for consistency
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(post.likeCount.toString())
                        Spacer(modifier = Modifier.width(16.dp))
                        val context = LocalContext.current
                        Icon(
                            Icons.Default.Star, // Always use Star shape
                            contentDescription = null, 
                            tint = if (isFavorited) Color(0xFFFFC107) else Color.Gray, // Amber for favorited, Gray for not
                            modifier = Modifier.clickable { 
                                viewModel.toggleFavorite(post, currentUserId) 
                                Toast.makeText(context, if (isFavorited) "已取消收藏" else "收藏成功", Toast.LENGTH_SHORT).show()
                            }
                        )
                        // No favorite count
                    }
                }
            }
        }
    }
}
