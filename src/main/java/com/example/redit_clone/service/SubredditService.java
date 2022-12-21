package com.example.redit_clone.service;

import com.example.redit_clone.dto.SubredditDto;
import com.example.redit_clone.mapper.SubredditMapper;
import com.example.redit_clone.model.Subreddit;
import com.example.redit_clone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class SubredditService {

    private final SubredditRepository subredditRepository;

    private final SubredditMapper subredditMapper;
    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit savedSubreddit = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
        subredditDto.setId(savedSubreddit.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream().map(subredditMapper::mapSubredditToDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public SubredditDto getById(Long id) {
        Subreddit subreddit = subredditRepository.getReferenceById(id);
        SubredditDto subredditDto = subredditMapper.mapSubredditToDto(subreddit);
        return subredditDto;
    }


    //    USING MAPPER FOR CONVERTING ENTITY TO DTO
//    private Subreddit mapSubredditDto(SubredditDto subredditDto) {
//        return Subreddit.builder().name(subredditDto.getName())
//                .description(subredditDto.getDescription())
//                .build();
//    }

//
//    private SubredditDto mapToDto(Subreddit subreddit) {
//        return SubredditDto.builder().name(subreddit.getName())
//                .description(subreddit.getDescription())
//                .build();
//    }
}
