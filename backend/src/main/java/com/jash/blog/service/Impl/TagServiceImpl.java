package com.jash.blog.service.Impl;


import com.jash.blog.domain.entities.Tag;
import com.jash.blog.repository.TagRepository;
import com.jash.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAllWithPostCount() ;
    }
}
