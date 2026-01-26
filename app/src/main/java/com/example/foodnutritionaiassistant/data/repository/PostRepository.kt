package com.example.foodnutritionaiassistant.data.repository

import com.example.foodnutritionaiassistant.data.db.MongoDBHelper
import com.example.foodnutritionaiassistant.data.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class PostRepository {
    private val database = MongoDBHelper.getDatabase()
    private val collection = database.getCollection<Post>("posts")

    suspend fun createPost(post: Post): Boolean {
        return try {
            val result = collection.insertOne(post)
            result.wasAcknowledged()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getPosts(): List<Post> {
        return try {
            collection.find().toList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
