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

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    @GetMapping("/{slug}")
    public ResponseEntity<TopicDetailsDTO> getTopic(@PathVariable String slug) {
        return ResponseEntity.ok(topicService.getTopicBySlug(slug));
    }

    @GetMapping
    public ResponseEntity<List<TopicShortDTO>> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }
}
