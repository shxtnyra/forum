package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.tag.TagDTO;
import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.TagEntity;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.TagMapper;
import com.shxtnyra.forum.repository.PostRepository;
import com.shxtnyra.forum.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    @Transactional
    public TagDTO createTag(String name, String description) {
        if (tagRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tag with this name already exists");
        }

        TagEntity tag = TagEntity.builder()
                .name(name)
                .description(description)
                .build();

        tag = tagRepository.save(tag);
        return TagMapper.toDTO(tag);
    }

    @Transactional
    public TagDTO updateTag(Long id, String name, String description) {
        TagEntity tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));

        if (!tag.getName().equals(name) && tagRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tag with this name already exists");
        }

        tag.setName(name);
        tag.setDescription(description);
        tag = tagRepository.save(tag);

        return TagMapper.toDTO(tag);
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag not found");
        }
        tagRepository.deleteById(id);
    }

    public List<TagDTO> searchTags(String query) {
        return tagRepository.searchByName(query)
                .stream()
                .map(TagMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TagDTO> getTagsByPost(Long postId) {
        return tagRepository.findByPostId(postId)
                .stream()
                .map(TagMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Set<TagEntity> processTags(Set<String> tagNames) {
        Set<TagEntity> tags = new HashSet<>();
        
        for (String name : tagNames) {
            TagEntity tag = tagRepository.findByName(name)
                    .orElseGet(() -> TagEntity.builder()
                            .name(name)
                            .build());
            tags.add(tag);
        }
        
        return tags;
    }

    @Transactional
    public void updatePostTags(Long postId, Set<String> tagNames) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        Set<TagEntity> tags = processTags(tagNames);
        post.setTags(tags);
        postRepository.save(post);
    }
} 