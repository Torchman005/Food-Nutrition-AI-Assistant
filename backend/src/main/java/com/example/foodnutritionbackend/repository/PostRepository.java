package com.example.foodnutritionbackend.repository;

import com.example.foodnutritionbackend.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByCategory(String category);
    List<Post> findByAuthorId(String authorId);
    List<Post> findByFavoritedUserIdsContaining(String userId);
}
