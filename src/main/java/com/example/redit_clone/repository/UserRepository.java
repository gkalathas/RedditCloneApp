package com.example.redit_clone.repository;

import com.example.redit_clone.model.Comment;
import com.example.redit_clone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String userName);
}
