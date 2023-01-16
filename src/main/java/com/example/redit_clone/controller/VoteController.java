package com.example.redit_clone.controller;


import com.example.redit_clone.dto.VoteDto;
import com.example.redit_clone.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/votes/")
public class VoteController {


    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public ResponseEntity<String> vote(@RequestBody VoteDto voteDto) {
        voteService.vote(voteDto);

        return new ResponseEntity<>("Vote successfully added", HttpStatus.OK);
    }
}
