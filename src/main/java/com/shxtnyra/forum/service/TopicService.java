package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.topic.TopicCreateDTO;
import com.shxtnyra.forum.dto.topic.TopicDetailsDTO;
import com.shxtnyra.forum.dto.topic.TopicShortDTO;
import com.shxtnyra.forum.entity.TopicEntity;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.TopicMapper;
import com.shxtnyra.forum.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;

    @Transactional
    public TopicDetailsDTO createTopic(TopicCreateDTO topicDTO) {
        if (topicRepository.existsBySlug(topicDTO.getSlug())) {
            throw new IllegalArgumentException("Тема с таким названием уже существует");
        }

        TopicEntity topic = TopicEntity.builder()
                .name(topicDTO.getName())
                .slug(topicDTO.getSlug())
                .description(topicDTO.getDescription())
                .build();

        topic = topicRepository.save(topic);

        return TopicMapper.toDetailsDTO(topic);
    }

    public List<TopicShortDTO> getAllTopics(){
        return topicRepository.findAll()
                .stream()
                .map(TopicMapper::toShortDTO)
                .toList();
    }

    public TopicDetailsDTO getTopicById(Long id){
        TopicEntity topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Такая тема не найдена"));
        return TopicMapper.toDetailsDTO(topic);
    }

    public TopicDetailsDTO getTopicBySlug(String slug) {
        TopicEntity topic = topicRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Такая тема не найдена"));
        return TopicMapper.toDetailsDTO(topic);
    }

    @Transactional
    public TopicDetailsDTO updateTopic(Long id, TopicDetailsDTO request) {
        TopicEntity topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Такая тема не найдена"));

        // TODO Исправить логику

        return TopicMapper.toDetailsDTO(topic);
    }

    @Transactional
    public void deleteTopicById(Long id) {
         topicRepository.deleteById(id);
    }
}
