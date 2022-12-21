package com.example.redit_clone.mapper;

import com.example.redit_clone.dto.PostRequest;
import com.example.redit_clone.dto.PostResponse;
import com.example.redit_clone.model.Post;
import com.example.redit_clone.model.Subreddit;
import com.example.redit_clone.model.User;
import com.example.redit_clone.repository.CommentRepository;
import com.example.redit_clone.repository.VoteRepository;
import com.example.redit_clone.service.AuthService;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Mapper(componentModel = "spring")
public abstract class PostMapper {


    @Autowired
    CommentRepository commentRepository ;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    AuthService authService;

    @Mapping(target = "createDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit.name")
    @Mapping(target = "voteCount", constant = "0")
    @Mapping(target = "user", source = "user.userName")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "postName", source = "postName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "url", source = "url")
    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    @Mapping(target = "upVote", expression = "java(isPostUpVoted(post))")
    @Mapping(target = "downVote", expression = "java(isPostDownVoted(post))")
    public abstract PostResponse mapToDto(Post post);




}
