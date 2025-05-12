package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.comment.CommentCreateDTO;
import com.shxtnyra.forum.dto.comment.CommentDetailsDTO;
import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.entity.CommentEntity;
import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.CommentMapper;
import com.shxtnyra.forum.repository.PostRepository;
import com.shxtnyra.forum.repository.UserRepository;
import org.springframework.data.domain.Page;
import com.shxtnyra.forum.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentShortDTO createComment(CommentCreateDTO createDTO, UserEntity user) {
        if (!postRepository.existsById(createDTO.getPostId()))
            throw new EntityNotFoundException("такого поста нету");

        CommentEntity parentComment = null;
        int level = 0;

        if (createDTO.getParentId() != null) {
            CommentRepository.ParentInfo parentInfo  = commentRepository.findParentInfoById(createDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Такого комментария нету"));

            if (!createDTO.getPostId().equals(parentInfo.getPostId()))
                throw new IllegalArgumentException("Указанный пост и пост комментария для ответа расходятся");

            // Создаем прокси для родительского комментария
            parentComment = CommentEntity.builder().id(createDTO.getParentId()).build();
            level = parentInfo.getLevel() + 1;
        }

        CommentEntity comment = CommentEntity.builder()
                .text(createDTO.getText())
                .author(user)
                .post(PostEntity.builder().id(createDTO.getPostId()).build()) // прокси объект поста
                .parent(parentComment)
                .level(level)
                .build();
        comment = commentRepository.save(comment);

        return CommentMapper.toShortDTO(comment);
    }

    // Получение комментария
    @Transactional
    public CommentDetailsDTO getCommentById(Long id) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));

        return CommentMapper.toDetailsDTO(comment);
    }

    // TODO добавить сортировку
    public List<CommentShortDTO> getCommentsByPost(Long postId){
        if (!postRepository.existsById(postId))
            throw new EntityNotFoundException("Пост не найден");

        return commentRepository.findByPostId(postId)
                .stream()
                .map(CommentMapper::toShortDTO)
                .toList();
    }

    public List<CommentShortDTO> getCommentsByParent(Long parentId){
        CommentEntity comment = commentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Нету такого коммента"));

        return comment.getReplies().
                stream().map(CommentMapper::toShortDTO)
                .toList();
    }

    public Page<CommentShortDTO> getCommentsByAuthor(Long authorId, Pageable pageable){
        if (!userRepository.existsById(authorId))
            throw new EntityNotFoundException("Комментарий не найден");

        return commentRepository.findByAuthorId(authorId, pageable)
                .map(CommentMapper::toShortDTO);
    }

    // TODO переработать на мягкое удаление
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found");
        }

        commentRepository.deleteById(id);
    }
}
