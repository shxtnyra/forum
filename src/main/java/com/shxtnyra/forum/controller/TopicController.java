package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.topic.TopicDetailsDTO;
import com.shxtnyra.forum.dto.topic.TopicShortDTO;
import com.shxtnyra.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для работы с темами.
 * Предоставляет API для получения тем.
 */
@RestController
@RequestMapping("/v1/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    /**
     * Получить подробную информацию о теме по slug.
     *
     * @param slug уникальный идентификатор темы (slug)
     * @return TopicDetailsDTO подробная информация о теме
     */
    @GetMapping("/{slug}")
    public ResponseEntity<TopicDetailsDTO> getTopic(@PathVariable String slug) {
        return ResponseEntity.ok(topicService.getTopicBySlug(slug));
    }   

    /**
     * Получить список всех тем (короткая информация).
     *
     * @return List<TopicShortDTO> список всех тем
     */
    @GetMapping
    public ResponseEntity<List<TopicShortDTO>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }
}
