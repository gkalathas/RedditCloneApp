package com.example.redit_clone.repository;

import com.example.redit_clone.model.Comment;
import com.example.redit_clone.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
