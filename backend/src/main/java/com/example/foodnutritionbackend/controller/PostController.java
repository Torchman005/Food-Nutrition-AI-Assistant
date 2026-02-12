package com.example.foodnutritionbackend.controller;

import com.example.foodnutritionbackend.model.Post;
import com.example.foodnutritionbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam(required = false) String category) {
        if (category != null) {
            return ResponseEntity.ok(postService.getPostsByCategory(category));
        }
        return ResponseEntity.ok(postService.getAllPosts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable String id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Post> likePost(@PathVariable String id, @RequestParam String userId) {
        return ResponseEntity.ok(postService.toggleLike(id, userId));
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Post> favoritePost(@PathVariable String id, @RequestParam String userId) {
        return ResponseEntity.ok(postService.toggleFavorite(id, userId));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<Post>> getFavoritePosts(@RequestParam String userId) {
        return ResponseEntity.ok(postService.getFavoritePosts(userId));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> recordView(@PathVariable String id, @RequestParam String userId) {
        postService.recordView(userId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<Post>> getViewHistory(@RequestParam String userId) {
        return ResponseEntity.ok(postService.getViewHistory(userId));
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> clearViewHistory(@RequestParam String userId) {
        postService.clearViewHistory(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByAuthor(@PathVariable String userId) {
        System.out.println("Received request for user posts: " + userId);
        List<Post> posts = postService.getPostsByAuthor(userId);
        System.out.println("Found " + posts.size() + " posts for user " + userId);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}
