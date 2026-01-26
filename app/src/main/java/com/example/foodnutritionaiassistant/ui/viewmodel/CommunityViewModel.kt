package com.example.foodnutritionaiassistant.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodnutritionaiassistant.data.model.Post
import com.example.foodnutritionaiassistant.data.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommunityViewModel : ViewModel() {
    private val postRepository = PostRepository()

    // Post Creation State
    var postContent by mutableStateOf("")
    var selectedGroup by mutableStateOf(GroupCategory.FITNESS)
    var postImages = mutableStateListOf<String>() // List of image paths/uris
    var postTags = mutableStateListOf<String>()
    var currentTagInput by mutableStateOf("")

    // Status
    var isPublishing by mutableStateOf(false)
    var publishSuccess by mutableStateOf<Boolean?>(null)

    // Used Tags (Mock for now, or fetch from DB)
    val usedTags = listOf("减肥", "增肌", "早餐", "低卡", "高蛋白", "瑜伽", "宝宝辅食", "养生茶")

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

    fun publishPost(authorNickname: String) {
        if (postContent.isBlank() && postImages.isEmpty()) return // Basic validation

        isPublishing = true
        viewModelScope.launch(Dispatchers.IO) {
            val newPost = Post(
                content = postContent,
                images = postImages.toList(),
                targetGroup = selectedGroup,
                tags = postTags.toList(),
                authorNickname = authorNickname
            )
            val success = postRepository.createPost(newPost)
            
            withContext(Dispatchers.Main) {
                isPublishing = false
                publishSuccess = success
                if (success) {
                    // Reset form
                    postContent = ""
                    postImages.clear()
                    postTags.clear()
                    selectedGroup = GroupCategory.FITNESS
                }
            }
        }
    }
    
    fun resetPublishStatus() {
        publishSuccess = null
    }
}
