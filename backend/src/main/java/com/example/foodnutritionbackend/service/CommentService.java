package com.example.foodnutritionbackend.service;

import com.example.foodnutritionbackend.model.Comment;
import com.example.foodnutritionbackend.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment addComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());
        // Ensure likedUserIds is initialized
        if (comment.getLikedUserIds() == null) {
            comment.setLikedUserIds(new java.util.ArrayList<>());
        }
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPostId(String postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment toggleLike(String commentId, String userId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment != null) {
            // Ensure likedUserIds is initialized
            if (comment.getLikedUserIds() == null) {
                comment.setLikedUserIds(new java.util.ArrayList<>());
            }
            
            if (comment.getLikedUserIds().contains(userId)) {
                comment.getLikedUserIds().remove(userId);
                comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
            } else {
                comment.getLikedUserIds().add(userId);
                comment.setLikeCount(comment.getLikeCount() + 1);
            }
            return commentRepository.save(comment);
        }
        return null;
    }

    public void deleteComment(String commentId) {
        // 1. Delete the comment itself
        commentRepository.deleteById(commentId);
        
        // 2. Recursively delete all replies
        // Note: replyToUserId field is actually used to store Parent Comment ID in this architecture
        List<Comment> allComments = commentRepository.findAll();
        deleteRepliesRecursively(commentId, allComments);
    }

    private void deleteRepliesRecursively(String parentId, List<Comment> allComments) {
        List<Comment> directReplies = allComments.stream()
                .filter(c -> parentId.equals(c.getReplyToUserId()))
                .collect(java.util.stream.Collectors.toList());
        
        for (Comment reply : directReplies) {
            // Recurse first
            deleteRepliesRecursively(reply.getId(), allComments);
            // Then delete
            commentRepository.deleteById(reply.getId());
        }
    }
}
