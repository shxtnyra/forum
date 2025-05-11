package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findById(Long id);
    void deleteById(Long id);
}
