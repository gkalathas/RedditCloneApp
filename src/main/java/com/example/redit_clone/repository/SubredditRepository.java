package com.example.redit_clone.repository;

import com.example.redit_clone.model.Comment;
import com.example.redit_clone.model.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Long> {
}
