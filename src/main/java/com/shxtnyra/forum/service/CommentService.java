package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.comment.CommentCreateDTO;
import com.shxtnyra.forum.dto.comment.CommentDetailsDTO;
import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.entity.CommentEntity;
import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.enums.Role;
import com.shxtnyra.forum.exception.exceptions.AccessDeniedException;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.CommentMapper;
import com.shxtnyra.forum.repository.CommentRepository;
import com.shxtnyra.forum.repository.PostRepository;
import com.shxtnyra.forum.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
            throw new EntityNotFoundException("Пост не найден");

        // Если пост или удален, или скрыт, или черновик
        if (postRepository.hasAnyFlagById(createDTO.getPostId())) {
            throw new AccessDeniedException("Нельзя оставить комментарий");
        }

        CommentEntity parentComment = null;
        int level = 0;

        if (createDTO.getParentId() != null) {
            // Используем проекцию что бы не подтянуть лишнего
            CommentRepository.ParentInfo parentInfo = commentRepository.findParentInfoById(createDTO.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));

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
    public CommentDetailsDTO getCommentById(Long commentId, UserEntity user) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));

        // Если комментарий удален
        if (comment.isDeleted()) {
            if (user.getRole() == Role.ROLE_USER) {
                throw new AccessDeniedException("Нельзя получить комментарий");
            }
            return CommentMapper.toDetailsDTO(comment);
        }

        // Если пост или удален, или скрыт, или черновик
        if (postRepository.hasAnyFlagById(commentRepository.findPostByCommentId(commentId))) {
            if (user.getRole() == Role.ROLE_USER) {
                throw new AccessDeniedException("Нельзя получить комментарий");
            }
        }

        return CommentMapper.toDetailsDTO(comment);
    }

    // TODO добавить сортировку
    public List<CommentShortDTO> getCommentsByPost(Long postId, UserEntity user) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Пост не найден");
        }

        // Если пост или удален, или скрыт, или черновик, то доступно только для админов и модераторов
        if (postRepository.hasAnyFlagById(postId)) {
            if (user.getRole() == Role.ROLE_USER) {
                throw new AccessDeniedException("Нельзя получить комментарии");
            }
        }

        return commentRepository.findByPostId(postId)
                .stream()
                .map(CommentMapper::toShortDTO)
                .toList();
    }

    public List<CommentShortDTO> getCommentsByParent(Long parentId, UserEntity user) {
        CommentEntity comment = commentRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));

        // Если пост или удален, или скрыт, или черновик
        if (postRepository.hasAnyFlagById(commentRepository.findPostByCommentId(parentId))) {
            if (user.getRole() == Role.ROLE_USER) {
                throw new AccessDeniedException("Нельзя получить ответы на комментарий");
            }
        }

        return comment.getReplies().
                stream().map(CommentMapper::toShortDTO)
                .toList();
    }

    public Page<CommentShortDTO> getCommentsByAuthor(Long authorId, Pageable pageable) {
        if (!userRepository.existsById(authorId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        return commentRepository.findByAuthorId(authorId, pageable)
                .map(CommentMapper::toShortDTO);
    }

    public Page<CommentShortDTO> getCommentsByAuthorByVisibility(Long authorId, boolean includeInvisible, Pageable pageable) {
        if (!userRepository.existsById(authorId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        return commentRepository.findByAuthorByVisibility(authorId, includeInvisible, pageable)
                .map(CommentMapper::toShortDTO);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));

        if (comment.isDeleted()) {
            return;
        }

        comment.setDeleted(true);
    }

    @Transactional
    public void recoverComment(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));

        if (!comment.isDeleted()) {
            return;
        }

        comment.setDeleted(false);
    }
}
