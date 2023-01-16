package com.example.redit_clone.controller;


import com.example.redit_clone.dto.CommentDto;
import com.example.redit_clone.model.Post;
import com.example.redit_clone.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/comments/")
public class CommentController {

    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @PostMapping
    public ResponseEntity<String> createComment(@RequestBody CommentDto commentDto) {
        commentService.save(commentDto);
        return new ResponseEntity<>("Comment created", HttpStatus.CREATED);

    }

    @GetMapping("/by-post/{postId}")
    public ResponseEntity<List<CommentDto>> getAllCommentsForPost(@PathVariable Long postId) {

        return new ResponseEntity<>(commentService.getAllCommentsForPost(postId), HttpStatus.OK);
    }
    @GetMapping("/by-user/{userName}")
    public ResponseEntity<List<CommentDto>> getAllCommentsForUser(@PathVariable String userName) {
        return new ResponseEntity<>(commentService.getAllCommentsForUser(userName), HttpStatus.OK);
    }




}



