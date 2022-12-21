package com.example.redit_clone.service;


import com.example.redit_clone.dto.PostRequest;
import com.example.redit_clone.dto.PostResponse;
import com.example.redit_clone.exceptions.SubredditNotFoundException;
import com.example.redit_clone.exceptions.UserNotFoundException;
import com.example.redit_clone.mapper.PostMapper;
import com.example.redit_clone.model.Post;
import com.example.redit_clone.model.Subreddit;
import com.example.redit_clone.model.User;
import com.example.redit_clone.repository.PostRepository;
import com.example.redit_clone.repository.SubredditRepository;
import com.example.redit_clone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class PostService {
    private final UserRepository userRepository;


    private AuthService authService;
    private SubredditRepository subredditRepository;
    private PostRepository postRepository;
    private PostMapper postMapper;

    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));

        postRepository.save(postMapper.map(postRequest, subreddit,authService.getCurrentUser(postRequest.getUserName())));
    }

    @Transactional(readOnly = true)
    public PostResponse getById(Long id) {
        Post post = postRepository.getReferenceById(id);
        return postMapper.mapToDto(post);

    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAll() {
        return postRepository.findAll().stream().map(postMapper::mapToDto)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<PostResponse> getByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        return postRepository.findByUser(user).stream()
                .map(PostMapper::mapToDto).collect(Collectors.toList());
    }

    public Object getBySubreddit(Long id) {

    }


}
