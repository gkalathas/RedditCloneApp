package com.example.redit_clone.service;


import com.example.redit_clone.dto.PostRequest;
import com.example.redit_clone.dto.PostResponse;
import com.example.redit_clone.exceptions.PostNotFoundException;
import com.example.redit_clone.exceptions.SubredditNotFoundException;
import com.example.redit_clone.exceptions.UserNotFoundException;
import com.example.redit_clone.mapper.Mapper;
import com.example.redit_clone.model.Post;
import com.example.redit_clone.model.Subreddit;
import com.example.redit_clone.model.User;
import com.example.redit_clone.repository.CommentRepository;
import com.example.redit_clone.repository.PostRepository;
import com.example.redit_clone.repository.SubredditRepository;
import com.example.redit_clone.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
public class PostService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    private final AuthService authService;


    private final SubredditRepository subredditRepository;
    private final PostRepository postRepository;

    private final Mapper mapper;

    @Autowired
    public PostService(UserRepository userRepository, AuthService authService, SubredditRepository subredditRepository, PostRepository postRepository, Mapper mapper,
                       CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.subredditRepository = subredditRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));
//        Post post = new Post();
//        post.setDescription(postRequest.getDescription());
//        post.setCreateDate(Instant.now());
//        post.setSubreddit(subreddit);
//        post.setVoteCount(0);
//        post.setUser(authService.getCurrentUser(postRequest.getUserName()));

        postRepository.save(map(postRequest, subreddit, authService.getCurrentUser()));
    }

    @Transactional(readOnly = true)
    public PostResponse getById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));


        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getPostId());
        postResponse.setSubredditName(post.getSubreddit().getName());
        postResponse.setUserName(post.getUser().getUserName());
        postResponse.setCommentCount(mapper.commentCount(post));
        postResponse.setDuration(mapper.getDuration(post));
        postResponse.setVoteCount(post.getVoteCount());
        postResponse.setCommentCount(mapper.commentCount(post));
        postResponse.setDuration(mapper.getDuration(post));
        postResponse.setUpVote(mapper.isPostUpVoted(post));
        postResponse.setDownVote(mapper.isPostDownVoted(post));

        return  postResponse;

    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAll() {
        return postRepository.findAll().stream().map(this::mapToDto)
                .collect(toList());

    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(this::mapToDto).collect(toList());
    }


    @Transactional(readOnly = true)
    public List<PostResponse> getByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));

        return postRepository.findByUser(user).stream()
                .map(this::mapToDto).collect(toList());
    }



    private Post map(PostRequest postRequest, Subreddit subreddit, User user) {

        if(postRequest == null && subreddit == null && user == null) {
            return null;
        }

        return Post.builder()
                .createDate(Instant.now())
                .description(postRequest.getDescription())
                .postId(postRequest.getPostId())
                .postName(postRequest.getPostName())
                .url(postRequest.getUrl())
                .subreddit(subreddit)
                .voteCount(0)
                .user(user)
                .build();
    }


    private PostResponse mapToDto(Post post) {
        return PostResponse.builder()
                .id(post.getPostId())
                .postName(post.getPostName())
                .description(post.getDescription())
                .subredditName(post.getSubreddit().getName())
                .userName(post.getUser().getUserName())
                .voteCount(post.getVoteCount())
                .commentCount(mapper.commentCount(post))
                .duration(mapper.getDuration(post))
                .url(post.getUrl())
                .upVote(mapper.isPostUpVoted(post))
                .downVote(mapper.isPostDownVoted(post))
                .build();
    }



}
