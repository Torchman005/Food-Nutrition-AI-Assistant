package com.example.foodnutritionbackend.service;

import com.example.foodnutritionbackend.model.Post;
import com.example.foodnutritionbackend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import com.example.foodnutritionbackend.model.ViewHistory;
import com.example.foodnutritionbackend.repository.ViewHistoryRepository;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private ViewHistoryRepository viewHistoryRepository;

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        if (post.getStatus() == null) {
            post.setStatus("PUBLISHED");
        }
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByCategory(String category) {
        return postRepository.findByCategory(category);
    }
    
    public Post getPost(String id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public Post toggleLike(String postId, String userId) {
        Post post = getPost(postId);
        if (post.getLikedUserIds().contains(userId)) {
            post.getLikedUserIds().remove(userId);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        } else {
            post.getLikedUserIds().add(userId);
            post.setLikeCount(post.getLikeCount() + 1);
        }
        return postRepository.save(post);
    }

    public Post toggleFavorite(String postId, String userId) {
        Post post = getPost(postId);
        if (post.getFavoritedUserIds().contains(userId)) {
            post.getFavoritedUserIds().remove(userId);
            post.setFavoriteCount(Math.max(0, post.getFavoriteCount() - 1));
        } else {
            post.getFavoritedUserIds().add(userId);
            post.setFavoriteCount(post.getFavoriteCount() + 1);
        }
        return postRepository.save(post);
    }
    
    public List<Post> getFavoritePosts(String userId) {
        // Since we store favoritedUserIds in Post, we can find posts where this set contains userId
        // However, standard MongoRepository might not support 'contains' in Set directly via method name efficiently without @Query
        // But for small scale, findByFavoritedUserIdsContaining works.
        // Let's assume PostRepository has this method or we add it.
        return postRepository.findByFavoritedUserIdsContaining(userId);
    }
    
    public void recordView(String userId, String postId) {
        ViewHistory history = viewHistoryRepository.findByUserIdAndPostId(userId, postId)
                .orElse(new ViewHistory());
        
        history.setUserId(userId);
        history.setPostId(postId);
        history.setViewedAt(LocalDateTime.now());
        
        viewHistoryRepository.save(history);
    }
    
    public List<Post> getViewHistory(String userId) {
        // Get history sorted by viewedAt desc
        List<ViewHistory> historyList = viewHistoryRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "viewedAt"));
        
        if (historyList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> postIds = historyList.stream()
                .map(ViewHistory::getPostId)
                .collect(Collectors.toList());
        
        // Fetch posts
        List<Post> posts = postRepository.findAllById(postIds);
        
        // Re-sort posts based on history order (findAllById doesn't guarantee order)
        Map<String, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));
        
        List<Post> sortedPosts = new ArrayList<>();
        for (String postId : postIds) {
            if (postMap.containsKey(postId)) {
                sortedPosts.add(postMap.get(postId));
            }
        }
        
        return sortedPosts;
    }

    public void clearViewHistory(String userId) {
        viewHistoryRepository.deleteByUserId(userId);
    }

    public List<Post> getPostsByAuthor(String authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    public void deletePost(String id) {
        postRepository.deleteById(id);
    }
}
