package com.example.redit_clone.exceptions;

public class SubredditNotFoundException extends RuntimeException {
    public SubredditNotFoundException(String NotFoundMessage) {
        super(NotFoundMessage);
    }

}
