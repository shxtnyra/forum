package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.post.PostCreateDTO;
import com.shxtnyra.forum.dto.post.PostDetailsDTO;
import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.TopicEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.mapper.PostMapper;
import com.shxtnyra.forum.repository.PostRepository;
import com.shxtnyra.forum.repository.TopicRepository;
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
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    // Create
    @Transactional
    public PostDetailsDTO createPost(PostCreateDTO createDTO, UserEntity user) {
//        UserEntity author = userRepository.findByUsername(username)
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        PostEntity post = PostEntity.builder()
                .title(createDTO.getTitle())
                .content(createDTO.getContent())
                .author(user)
                .build();

        post = postRepository.save(post);
        return PostMapper.toDetailsDTO(post);
    }

    // получение поста
    @Transactional
    public PostDetailsDTO getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Увеличиваем число просмотров
        postRepository.incrementViewCount(id);
        return PostMapper.toDetailsDTO(post);
    }

    // Получение всех постов
    public Page<PostShortDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(PostMapper::toShortDTO);
    }

    // Получение всех постов для ленты
    public Slice<PostShortDTO> getNewsFeed(Long lastSeenId, Pageable pageable) {
        Slice<PostEntity> posts = postRepository.findForNewsFeed(lastSeenId, pageable);
        return posts.map(PostMapper::toShortDTO);
    }

    // Получение всех постов конкретного автора
    public Page<PostShortDTO> getPostsByAuthor(Long authorId, Pageable pageable) {
        return postRepository.findByAuthorId(authorId, pageable)
                .map(PostMapper::toShortDTO);
    }

    // TODO Заменить на получение постов з определённый промежуток времени
    public Page<PostDetailsDTO> getTodayPosts(Pageable pageable) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return postRepository.findTodayPosts(startOfDay, pageable)
                .map(PostMapper::toDetailsDTO);
    }

    @Transactional(readOnly = true)
    public Page<PostShortDTO> getPostsByTopic(
            String topicSlug,
            Pageable pageable
    ) {
//        TopicEntity topic = topicRepository.findBySlug(topicSlug)
//                .orElseThrow(() -> new EntityNotFoundException("Topic not found"));

        if (!topicRepository.existsBySlug(topicSlug)) {
            throw new EntityNotFoundException("Topic not found");
        }

        return postRepository.findByTopicSlug(topicSlug, pageable)
                .map(PostMapper::toShortDTO);
    }

    // Обновление пока put, надо ещё path добавить будет
    @Transactional
    public PostDetailsDTO updatePost(Long id, PostCreateDTO dto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        return PostMapper.toDetailsDTO(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
