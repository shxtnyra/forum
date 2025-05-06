package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.comment.CommentCreateDTO;
import com.shxtnyra.forum.dto.comment.CommentDetailsDTO;
import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.entity.CommentEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.CommentMapper;
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
    private final UserRepository userRepository;

    // Создание
    @Transactional
    public CommentDetailsDTO createComment(CommentCreateDTO createDTO, UserEntity user) {

        CommentEntity comment = CommentEntity.builder()
                .content(createDTO.getContent())
                .author(user)
                .build();
        comment = commentRepository.save(comment);

        return CommentMapper.toDetailsDTO(comment);
    }

    // Получение комментария
    @Transactional
    public CommentDetailsDTO getCommentById(Long id) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        return CommentMapper.toDetailsDTO(comment);
    }

    // Получение всех комментариев к посту
    public Page<CommentShortDTO> getAllComments(Long id, Pageable pageable) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found");
        }

        return commentRepository.findByPostId(id, pageable)
                .map(CommentMapper::toShortDTO);
    }

    // Получение всех ответов на комментарий
    public Page<CommentShortDTO> getAllReplies(Long id, Pageable pageable) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found");
        }

        return commentRepository.findByParentId(id, pageable)
                .map(CommentMapper::toShortDTO);
    }

    // Получение всех комментариев конкретного пользователя
    public Page<CommentShortDTO> getCommentsByAuthor(Long id, Pageable pageable) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }

        return commentRepository.findByAuthorId(id, pageable)
                .map(CommentMapper::toShortDTO);
    }

    // Получение комментариев с подстрокой
    public List<CommentShortDTO> getCommentsByText(String text) {
        return commentRepository.findByTextContainingIgnoreCase(text)
                .stream()
                .map(CommentMapper::toShortDTO)
                .toList();
    }

    // Изменение счетчиков оценок
    public void incrementLikes(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found");
        }

        commentRepository.incrementLikes(id);
    }

    public void incrementDislikes(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found");
        }

        commentRepository.incrementDislikes(id);
    }

    // Удаление комментария
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found");
        }

        commentRepository.deleteById(id);
    }
}
