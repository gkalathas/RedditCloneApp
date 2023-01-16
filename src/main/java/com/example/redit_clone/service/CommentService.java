package com.example.redit_clone.service;


import com.example.redit_clone.dto.CommentDto;
import com.example.redit_clone.exceptions.PostNotFoundException;
import com.example.redit_clone.exceptions.SpringRedditException;
import com.example.redit_clone.exceptions.UserNotFoundException;
import com.example.redit_clone.mapper.Mapper;
import com.example.redit_clone.model.Comment;
import com.example.redit_clone.model.Post;
import com.example.redit_clone.model.User;
import com.example.redit_clone.repository.CommentRepository;
import com.example.redit_clone.repository.PostRepository;
import com.example.redit_clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import static java.util.stream.Collectors.toList;

@Service
public class CommentService {


    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final AuthService authService;

    private final Mapper mapper;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(PostRepository postRepository, UserRepository userRepository, AuthService authService, Mapper mapper,
                          CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.mapper = mapper;
        this.commentRepository = commentRepository;
    }


    public void save(CommentDto commentDto) {
        Post post = postRepository.findById(commentDto.getPostId()).orElseThrow(() ->
                new PostNotFoundException(commentDto.getPostId().toString()));

        Comment comment = mapper.CommentMap(commentDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

    }

    public List<CommentDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException(postId.toString()));

        return commentRepository.findByPost(post)
                .stream()
                .map(mapper::commentMapToDto)
                .collect(toList());
    }


    public List<CommentDto> getAllCommentsForUser(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException(userName));

        return commentRepository.findAllByUser(user)
                .stream()
                .map(mapper::commentMapToDto)
                .collect(toList());
    }

    public boolean containsSwearWords(String comment) {
        if (comment.contains("shit")) {
            throw new SpringRedditException("Comments contains unacceptable language");
        }
        return false;
    }
}
