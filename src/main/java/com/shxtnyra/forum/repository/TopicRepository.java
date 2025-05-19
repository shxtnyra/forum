package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
    Optional<TopicEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);

    Long findIdBySlug(String topicSlug);
}
