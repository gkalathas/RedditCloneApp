package com.example.redit_clone.repository;

import com.example.redit_clone.model.Comment;
import com.example.redit_clone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
}
