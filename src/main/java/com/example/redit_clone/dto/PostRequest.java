package com.example.redit_clone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequest {
    private String postId;
    private String userName;
    private String subredditName;
    private String url;
    private String description;
}
