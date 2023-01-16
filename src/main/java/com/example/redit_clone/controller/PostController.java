package com.example.redit_clone.controller;


import com.example.redit_clone.dto.PostRequest;
import com.example.redit_clone.dto.PostResponse;
import com.example.redit_clone.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;




    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody PostRequest postRequest) {
        postService.save(postRequest);
        return new ResponseEntity<>("Post created", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return new ResponseEntity<>(postService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostResponse>> getAll() {
        return new ResponseEntity<>(postService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/by-user/{username}")
    public ResponseEntity<List<PostResponse>> getPostByName(@PathVariable String username) {
        return new ResponseEntity<>(postService.getByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/by-subreddit/{id}")
    public ResponseEntity<List<PostResponse>> getPostBySubreddit(@PathVariable Long id) {
        return new ResponseEntity<>(postService.getPostsBySubreddit(id), HttpStatus.OK);
    }

}
