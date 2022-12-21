package com.example.redit_clone.controller;


import com.example.redit_clone.dto.SubredditDto;
import com.example.redit_clone.service.SubredditService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/subreddit")
public class SubredditController {

    private final SubredditService subredditService;

    @PostMapping
    public ResponseEntity<SubredditDto> creteSubreddit(@RequestBody SubredditDto subredditDto) {
        return new ResponseEntity<>(subredditService.save(subredditDto), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SubredditDto>> getAllSubreddits() {
        return new ResponseEntity<>(subredditService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/id")
    public ResponseEntity<SubredditDto> getSubredditById(@RequestParam Long id) {
        return new ResponseEntity<>(subredditService.getById(id), HttpStatus.OK);
    }

}
