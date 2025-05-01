package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.post.PostCreateDTO;
import com.shxtnyra.forum.dto.post.PostDTO;
import com.shxtnyra.forum.dto.post.PostPreviewDTO;
import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.mapper.PostMapper;
import com.shxtnyra.forum.repository.PostRepository;
import com.shxtnyra.forum.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // Create
    @Transactional
    public PostDTO createPost(PostCreateDTO dto, UserEntity user) {
//        UserEntity author = userRepository.findByUsername(username)
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        PostEntity post = PostEntity.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .build();

        post = postRepository.save(post);
        return PostMapper.toDTO(post);
    }

    // получение поста
    @Transactional
    public PostDTO getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Увеличиваем число просмотров
        postRepository.incrementViewCount(id);
        return PostMapper.toDTO(post);
    }

    // Получение всех постов
    public Page<PostPreviewDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(PostMapper::toPreviewDTO);
    }

    // Получение всех постов для ленты
    public Slice<PostPreviewDTO> getNewsFeed(Long lastSeenId, Pageable pageable) {
        Slice<PostEntity> posts = postRepository.findForNewsFeed(lastSeenId, pageable);
        return posts.map(PostMapper::toPreviewDTO);
    }

    // Получение всех постов конкретного автора
    public Page<PostPreviewDTO> getPostsByAuthor(Long authorId, Pageable pageable) {
        return postRepository.findByAuthorId(authorId, pageable)
                .map(PostMapper::toPreviewDTO);
    }

    public Page<PostDTO> getTodayPosts(Pageable pageable) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return postRepository.findTodayPosts(startOfDay, pageable)
                .map(PostMapper::toDTO);
    }

    // Обновление пока put, надо ещё path добавить будет
    @Transactional
    public PostDTO updatePost(Long id, PostCreateDTO dto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        return PostMapper.toDTO(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
