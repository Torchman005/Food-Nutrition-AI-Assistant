package com.example.foodnutritionaiassistant.data.network

import com.example.foodnutritionaiassistant.data.model.Comment
import com.example.foodnutritionaiassistant.data.model.Post
import com.example.foodnutritionaiassistant.ui.viewmodel.UserProfile
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // User APIs
    @POST("/api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<UserProfile>

    @POST("/api/users/register")
    suspend fun register(@Body user: UserProfile): Response<UserProfile>

    @PUT("/api/users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: UserProfile): Response<UserProfile>
    
    @GET("/api/users/{id}")
    suspend fun getUser(@Path("id") id: String): Response<UserProfile>

    // Community APIs
    @GET("/api/posts")
    suspend fun getPosts(@Query("category") category: String? = null): Response<List<Post>>

    @POST("/api/posts")
    suspend fun createPost(@Body post: Post): Response<Post>
    
    @GET("/api/posts/{id}")
    suspend fun getPost(@Path("id") id: String): Response<Post>

    @POST("/api/posts/{id}/like")
    suspend fun likePost(@Path("id") id: String, @Query("userId") userId: String): Response<Post>

    @POST("/api/posts/{id}/favorite")
    suspend fun favoritePost(@Path("id") id: String, @Query("userId") userId: String): Response<Post>

    @GET("/api/posts/favorites")
    suspend fun getFavoritePosts(@Query("userId") userId: String): Response<List<Post>>

    @POST("/api/posts/{id}/view")
    suspend fun recordView(@Path("id") id: String, @Query("userId") userId: String): Response<Void>

    @GET("/api/posts/history")
    suspend fun getViewHistory(@Query("userId") userId: String): Response<List<Post>>

    @DELETE("/api/posts/history")
    suspend fun clearViewHistory(@Query("userId") userId: String): Response<Void>

    @GET("/api/posts/user/{userId}")
    suspend fun getPostsByAuthor(@Path("userId") userId: String): Response<List<Post>>

    @DELETE("/api/posts/{id}")
    suspend fun deletePost(@Path("id") id: String): Response<Void>

    // Comment APIs
    @GET("/api/comments")
    suspend fun getComments(@Query("postId") postId: String): Response<List<Comment>>

    @POST("/api/comments")
    suspend fun addComment(@Body comment: Comment): Response<Comment>

    @POST("/api/comments/{id}/like")
    suspend fun likeComment(@Path("id") id: String, @Query("userId") userId: String): Response<Comment>

    @DELETE("/api/comments/{id}")
    suspend fun deleteComment(@Path("id") commentId: String): Response<Void>
}
