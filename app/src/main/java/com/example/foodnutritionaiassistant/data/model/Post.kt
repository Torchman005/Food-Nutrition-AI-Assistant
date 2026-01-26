package com.example.foodnutritionaiassistant.data.model

import com.example.foodnutritionaiassistant.ui.viewmodel.GroupCategory
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.Instant

data class Post(
    @BsonId
    val id: ObjectId = ObjectId(),
    val content: String,
    val images: List<String>, // Paths or URLs
    val targetGroup: GroupCategory,
    val tags: List<String>,
    val createdAt: Long = Instant.now().toEpochMilli(),
    val authorNickname: String = "Anonymous", // Optional: Store author name
    val authorAvatar: String = "" // Optional: Store author avatar
)
