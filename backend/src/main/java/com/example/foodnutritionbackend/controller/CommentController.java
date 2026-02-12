package com.example.foodnutritionbackend.controller;

import com.example.foodnutritionbackend.model.Comment;
import com.example.foodnutritionbackend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.addComment(comment));
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@RequestParam String postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Comment> toggleLike(@PathVariable String id, @RequestParam String userId) {
        return ResponseEntity.ok(commentService.toggleLike(id, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
}
