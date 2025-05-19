package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.topic.TopicCreateDTO;
import com.shxtnyra.forum.dto.topic.TopicDetailsDTO;
import com.shxtnyra.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdminController {
    private final TopicService topicService;

    // === Topic Management ===
    @PostMapping("/topics")
    public ResponseEntity<TopicDetailsDTO> createTopic(@RequestBody TopicCreateDTO request) {
        return ResponseEntity.ok(topicService.createTopic(request));
    }

    @DeleteMapping("/topics/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopicById(id);
        return ResponseEntity.noContent().build();
    }
}
