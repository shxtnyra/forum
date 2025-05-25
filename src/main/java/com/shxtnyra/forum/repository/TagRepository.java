package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT t FROM TagEntity t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TagEntity> searchByName(@Param("query") String query);
    
    @Query("SELECT t FROM TagEntity t JOIN t.posts p WHERE p.id = :postId")
    List<TagEntity> findByPostId(@Param("postId") Long postId);
} 