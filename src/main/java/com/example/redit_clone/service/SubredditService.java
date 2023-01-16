package com.example.redit_clone.service;

import com.example.redit_clone.dto.SubredditDto;
import com.example.redit_clone.mapper.Mapper;
import com.example.redit_clone.model.Subreddit;
import com.example.redit_clone.repository.SubredditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;



@Slf4j
@Service
public class SubredditService {

    private final SubredditRepository subredditRepository;

    private final Mapper mapper;

    @Autowired
    public SubredditService(SubredditRepository subredditRepository, Mapper mapper) {
        this.subredditRepository = subredditRepository;
        this.mapper = mapper;
    }

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit savedSubreddit = subredditRepository.save(mapper.mapSubredditDto(subredditDto));
        subredditDto.setId(savedSubreddit.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream().map(this::mapToDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public SubredditDto getById(Long id) {
        Subreddit subreddit = subredditRepository.getReferenceById(id);
        SubredditDto subredditDto = mapper.mapToDto(subreddit);
        return subredditDto;
    }


//        USING MAPPER FOR CONVERTING ENTITY TO DTO
    private Subreddit mapSubredditDtoToSubreddit(SubredditDto subredditDto) {
        return Subreddit.builder().name(subredditDto.getName())
                .description(subredditDto.getDescription())
                .build();
    }


    private SubredditDto mapToDto(Subreddit subreddit) {
        return SubredditDto.builder().name(subreddit.getName())
                .description(subreddit.getDescription())
                .build();
    }
}
