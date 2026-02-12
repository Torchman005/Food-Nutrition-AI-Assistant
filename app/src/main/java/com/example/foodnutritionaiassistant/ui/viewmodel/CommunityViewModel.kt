package com.example.foodnutritionaiassistant.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodnutritionaiassistant.data.model.GroupCategory
import com.example.foodnutritionaiassistant.data.model.Post
import com.example.foodnutritionaiassistant.data.model.PostStatus
import com.example.foodnutritionaiassistant.data.repository.CommunityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.foodnutritionaiassistant.data.model.Comment

class CommunityViewModel : ViewModel() {
    private val communityRepository = CommunityRepository()

    // Feed State
    var posts = mutableStateListOf<Post>()
        private set
    var isLoading by mutableStateOf(false)
    var currentCategory by mutableStateOf(GroupCategory.WELLNESS)
    var isCategoryInitialized by mutableStateOf(false)

    // Post Creation State
    var postTitle by mutableStateOf("")
    var postContent by mutableStateOf("")
    var selectedGroup by mutableStateOf(GroupCategory.WELLNESS)
    var postImages = mutableStateListOf<String>() // List of image URIs (local or remote)
    var postTags = mutableStateListOf<String>()
    var currentTagInput by mutableStateOf("")
    
    // Status
    var isPublishing by mutableStateOf(false)
    var publishSuccess by mutableStateOf<Boolean?>(null)

    // Comments State
    var comments = mutableStateListOf<Comment>()
        private set
    var commentContent by mutableStateOf("")
    var isSendingComment by mutableStateOf(false)
    var replyToComment by mutableStateOf<Comment?>(null)
    
    // Comment Organization
    var rootComments = mutableStateListOf<Comment>()
    private set
    var repliesMap = mutableMapOf<String, MutableList<Comment>>()
    var expandedComments = mutableStateListOf<String>() // Set of IDs of expanded root comments

    // Used Tags
    val usedTags = listOf("减肥", "增肌", "早餐", "低卡", "高蛋白", "瑜伽", "宝宝辅食", "养生茶")

    init {
        loadPosts(currentCategory)
    }

    fun loadPosts(category: GroupCategory) {
        currentCategory = category
        viewModelScope.launch {
            isLoading = true
            val result = withContext(Dispatchers.IO) {
                communityRepository.getPostsByCategory(category)
            }
            posts.clear()
            posts.addAll(result)
            isLoading = false
        }
    }

    fun addImage(uri: String) {
        if (postImages.size < 9) {
            postImages.add(uri)
        }
    }

    fun removeImage(uri: String) {
        postImages.remove(uri)
    }

    fun addTag(tag: String) {
        if (tag.isNotBlank() && !postTags.contains(tag)) {
            postTags.add(tag)
        }
        currentTagInput = ""
    }

    fun removeTag(tag: String) {
        postTags.remove(tag)
    }

    fun publishPost(context: Context, authorId: String, authorNickname: String, authorAvatar: String?) {
        if (postTitle.isBlank() || postContent.isBlank()) return

        isPublishing = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Upload Images
                val uploadedImageUrls = mutableListOf<String>()
                if (postImages.isNotEmpty()) {
                    for (uriString in postImages) {
                        val uri = Uri.parse(uriString)
                        if (uri.scheme?.startsWith("http") == true) {
                            uploadedImageUrls.add(uriString)
                        } else {
                            try {
                                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                    val extension = context.contentResolver.getType(uri)?.split("/")?.lastOrNull() ?: "jpg"
                                    val url = communityRepository.uploadImage(inputStream, extension)
                                    if (url != null) {
                                        uploadedImageUrls.add(url)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    
                    // If images were selected but none uploaded successfully, maybe fail? 
                    // Or just continue with what succeeded. 
                    // For now, if user selected images and all failed, we might want to warn, 
                    // but simple logic is just proceed with whatever uploaded.
                    if (uploadedImageUrls.isEmpty()) {
                         // Optional: Handle all upload failure if strict
                    }
                }

                // 2. Create Post
                val newPost = Post(
                    authorId = authorId,
                    authorName = authorNickname,
                    authorAvatar = authorAvatar,
                    title = postTitle,
                    content = postContent,
                    images = uploadedImageUrls,
                    category = selectedGroup.value,
                    tags = postTags.toList(),
                    status = PostStatus.PUBLISHED
                )

                val success = communityRepository.createPost(newPost)
                
                withContext(Dispatchers.Main) {
                    isPublishing = false
                    publishSuccess = success
                    if (success) {
                        resetCreateForm()
                        loadPosts(currentCategory)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    isPublishing = false
                    publishSuccess = false
                    println("Publish Post Exception: ${e.message}")
                }
            }
        }
    }
    
    fun saveDraft(authorId: String, authorNickname: String) {
        viewModelScope.launch(Dispatchers.IO) {
             val newPost = Post(
                authorId = authorId,
                authorName = authorNickname,
                authorAvatar = null,
                title = postTitle,
                content = postContent,
                images = emptyList(), 
                category = selectedGroup.value,
                tags = postTags.toList(),
                status = PostStatus.DRAFT
            )
            communityRepository.saveDraft(newPost)
        }
    }

    private fun resetCreateForm() {
        postTitle = ""
        postContent = ""
        postImages.clear()
        postTags.clear()
        selectedGroup = GroupCategory.WELLNESS
    }
    
    fun resetPublishStatus() {
        publishSuccess = null
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                communityRepository.getComments(postId)
            }
            comments.clear()
            comments.addAll(result)
            organizeComments(result)
        }
    }

    private fun organizeComments(allComments: List<Comment>) {
        rootComments.clear()
        repliesMap.clear()
        
        // 1. Identify direct roots (those without replyToUserId or replyToUserId not found in list)
        val commentMap = allComments.associateBy { it.id }
        
        // 2. Grouping
        allComments.forEach { comment ->
            // If replyToUserId is null, it's a root.
            // If replyToUserId is not in map, it's a root (orphan or deleted parent).
            if (comment.replyToUserId == null || !commentMap.containsKey(comment.replyToUserId)) {
                rootComments.add(comment)
            } else {
                // It is a reply, find its root
                var current = comment
                var depth = 0
                // Trace up to find the root
                while (current.replyToUserId != null && commentMap.containsKey(current.replyToUserId) && depth < 20) {
                    val parent = commentMap[current.replyToUserId]
                    if (parent == null) break 
                    
                    // Check if parent is root
                    if (parent.replyToUserId == null || !commentMap.containsKey(parent.replyToUserId)) {
                        current = parent // found root
                        break
                    }
                    current = parent
                    depth++
                }
                
                // 'current' is the root comment object
                val rootId = current.id ?: ""
                if (rootId.isNotEmpty()) {
                    if (!repliesMap.containsKey(rootId)) {
                        repliesMap[rootId] = mutableListOf()
                    }
                    repliesMap[rootId]?.add(comment)
                } else {
                    rootComments.add(comment)
                }
            }
        }
    }

    fun toggleCommentExpand(commentId: String) {
        if (expandedComments.contains(commentId)) {
            expandedComments.remove(commentId)
        } else {
            expandedComments.add(commentId)
        }
    }
    
    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                communityRepository.deleteComment(commentId)
            }
            if (success) {
                // Remove from local list and reorganize
                // We need to remove the comment AND all its descendants
                // First, build a map to find children easily
                val commentMap = comments.associateBy { it.id }
                val childrenMap = comments.groupBy { it.replyToUserId } // Group by parent ID
                
                val toDelete = mutableSetOf<String>()
                toDelete.add(commentId)
                
                // Recursively find descendants
                val stack = mutableListOf(commentId)
                while (stack.isNotEmpty()) {
                    val currentId = stack.removeAt(0)
                    val children = childrenMap[currentId]
                    if (children != null) {
                        for (child in children) {
                            if (child.id != null) {
                                toDelete.add(child.id)
                                stack.add(child.id)
                            }
                        }
                    }
                }
                
                val updatedList = comments.filter { it.id !in toDelete }
                comments.clear()
                comments.addAll(updatedList)
                organizeComments(updatedList)
            }
        }
    }

    fun sendComment(postId: String, authorId: String, authorName: String, authorAvatar: String?) {
        if (commentContent.isBlank()) return
        isSendingComment = true
        viewModelScope.launch {
             val newComment = Comment(
                 postId = postId,
                 authorId = authorId,
                 authorName = authorName,
                 authorAvatar = authorAvatar,
                 content = commentContent,
                 replyToUserName = replyToComment?.authorName,
                 replyToUserId = replyToComment?.id
             )
             val success = withContext(Dispatchers.IO) {
                 communityRepository.addComment(newComment)
             }
             if (success) {
                 commentContent = ""
                 replyToComment = null
                 loadComments(postId) // Reload comments
             }
             isSendingComment = false
        }
    }

    fun toggleCommentLike(comment: Comment, userId: String) {
        viewModelScope.launch {
            val updatedComment = withContext(Dispatchers.IO) {
                communityRepository.toggleCommentLike(comment.id ?: return@withContext null, userId)
            }
            if (updatedComment != null) {
                val index = comments.indexOfFirst { it.id == updatedComment.id }
                if (index != -1) {
                    comments[index] = updatedComment
                    // Re-organize to update UI grouping
                    organizeComments(comments.toList())
                }
            }
        }
    }

    fun toggleLike(post: Post, userId: String) {
        viewModelScope.launch {
            val updatedPost = withContext(Dispatchers.IO) {
                communityRepository.toggleLike(post.id ?: return@withContext null, userId)
            }
            if (updatedPost != null) {
                updatePostInList(updatedPost)
            }
        }
    }

    fun toggleFavorite(post: Post, userId: String) {
        viewModelScope.launch {
            val updatedPost = withContext(Dispatchers.IO) {
                communityRepository.toggleFavorite(post.id ?: return@withContext null, userId)
            }
            if (updatedPost != null) {
                updatePostInList(updatedPost)
            }
        }
    }

    private fun updatePostInList(updatedPost: Post) {
        // Update main feed list
        val index = posts.indexOfFirst { it.id == updatedPost.id }
        if (index != -1) {
            posts[index] = updatedPost
        }
        
        // Update favorite posts list
        // Check if the updated post should be in favorites or removed/updated
        val favIndex = favoritePosts.indexOfFirst { it.id == updatedPost.id }
        if (favIndex != -1) {
             // It was in favorites, update it
             favoritePosts[favIndex] = updatedPost
             // Note: ProfileScreen filtering will handle removal if it's no longer favorited
        } else {
             // It wasn't in favorites locally.
             // If we are currently viewing favorites screen, we might want to add it?
             // But usually we toggle from main feed.
             // If the updatedPost indicates it IS favorited by current user, we could add it.
             // But 'updatedPost' doesn't easily tell us "current user" without passing userId.
             // However, toggleFavorite logic implies we toggled it.
             // For simplicity, we can let loadFavoritePosts refresh it, or just update if exists.
             // The critical part for "Cancel Favorite" is that it MUST be updated in the list so UI can filter it out.
        }
    }
    
    // For My Favorites Screen
    var favoritePosts = mutableStateListOf<Post>()
        private set
        
    fun loadFavoritePosts(userId: String) {
        viewModelScope.launch {
            isLoading = true
            val result = withContext(Dispatchers.IO) {
                communityRepository.getFavoritePosts(userId)
            }
            favoritePosts.clear()
            favoritePosts.addAll(result)
            isLoading = false
        }
    }
    
    // For View History
    var historyPosts = mutableStateListOf<Post>()
        private set
        
    fun loadViewHistory(userId: String) {
        viewModelScope.launch {
            isLoading = true
            val result = withContext(Dispatchers.IO) {
                communityRepository.getViewHistory(userId)
            }
            historyPosts.clear()
            historyPosts.addAll(result)
            isLoading = false
        }
    }
    
    fun recordView(postId: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            communityRepository.recordView(postId, userId)
        }
    }
    
    fun clearViewHistory(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            communityRepository.clearViewHistory(userId)
            withContext(Dispatchers.Main) {
                historyPosts.clear()
            }
        }
    }
    
    // For My Posts
    var myPosts = mutableStateListOf<Post>()
        private set
        
    fun loadMyPosts(userId: String) {
        viewModelScope.launch {
            isLoading = true
            val result = withContext(Dispatchers.IO) {
                communityRepository.getPostsByAuthor(userId)
            }
            myPosts.clear()
            myPosts.addAll(result)
            isLoading = false
        }
    }
    
    fun deletePosts(postIds: List<String>) {
        viewModelScope.launch {
            // Optimistic update for UI
            val toRemove = postIds.toSet()
            myPosts.removeIf { toRemove.contains(it.id) }
            posts.removeIf { toRemove.contains(it.id) } // Also remove from main feed if present
            
            withContext(Dispatchers.IO) {
                postIds.forEach { id ->
                    communityRepository.deletePost(id)
                }
            }
        }
    }
}
