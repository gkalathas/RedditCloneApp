package com.example.redit_clone.mapper;

import com.example.redit_clone.dto.CommentDto;
import com.example.redit_clone.dto.SubredditDto;
import com.example.redit_clone.model.*;
import com.example.redit_clone.repository.CommentRepository;
import com.example.redit_clone.repository.VoteRepository;
import com.example.redit_clone.service.AuthService;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static com.example.redit_clone.model.VoteType.DOWNVOTE;
import static com.example.redit_clone.model.VoteType.UPVOTE;


@Service
@Data
@Slf4j
@NoArgsConstructor
@Builder
public class Mapper {

    private  CommentRepository commentRepository;

    private  VoteRepository voteRepository;

    private  AuthService authService;

    @Autowired
    public Mapper(CommentRepository commentRepository, VoteRepository voteRepository, AuthService authService) {
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
        this.authService = authService;
    }

    public Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }
    public String getDuration(Post post) {
        return TimeAgo.using(post.getCreateDate().toEpochMilli());
    }

    public boolean isPostUpVoted(Post post) {
        return checkVoteType(post, UPVOTE);
    }

    public boolean isPostDownVoted(Post post) {
        return checkVoteType(post, DOWNVOTE);
    }

    public boolean checkVoteType(Post post, VoteType voteType) {
        if (authService.isLoggedIn()) {
            Optional<Vote> voteForPostByUser =
                    voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
                            authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
                    .isPresent();
        }
        return false;
    }

//     COMMENT MAPPER
    //Comment dto to commnent
    public Comment CommentMap(CommentDto commentDto, Post post, User user) {
        return Comment.builder()
                .text(commentDto.getText())
                .createDate(Instant.now())
                .post(post)
                .user(user)
                .build();
    }
    //CommentDto to comment
    public CommentDto commentMapToDto(Comment comment) {
        return CommentDto.builder()
                .postId(comment.getPost().getPostId())
                .userName(comment.getUser().getUserName())
                .build();
    }


//       SUBREDDIT MAPPER
    public Subreddit mapSubredditDto(SubredditDto subredditDto) {
        return Subreddit.builder().name(subredditDto.getName())
                .description(subredditDto.getDescription())
                .build();
    }


    public SubredditDto mapToDto(Subreddit subreddit) {
        return SubredditDto.builder().name(subreddit.getName())
                .description(subreddit.getDescription())
                .build();
    }
    public SubredditDto mapSubredditToDto(Subreddit subreddit) {
        return SubredditDto.builder()
                .name(subreddit.getName())
                .description(subreddit.getDescription())
                .numberOfPosts(mapPosts(subreddit.getPosts()))
                .build();
    }

    Integer mapPosts(@NotNull List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }


}
