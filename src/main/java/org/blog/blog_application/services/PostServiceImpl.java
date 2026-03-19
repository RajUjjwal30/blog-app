package org.blog.blog_application.services.impl;

import org.blog.blog_application.dtos.PostCreateDto;
import org.blog.blog_application.dtos.PostResponseDto;
import org.blog.blog_application.dtos.PostUpdateDto;
import org.blog.blog_application.mapper.PostMapper;
import org.blog.blog_application.models.Post;
import org.blog.blog_application.models.PostTag;
import org.blog.blog_application.models.User;
import org.blog.blog_application.repositories.PostRepository;
import org.blog.blog_application.repositories.UserRepository;
import org.blog.blog_application.services.PostService;
import org.blog.blog_application.services.TagService;
import org.blog.blog_application.specification.PostSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private TagService tagService;
    private UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository,TagService tagService, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.userRepository=userRepository;
    }

    @Override
    public void createPost(PostCreateDto dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setExcerpt(dto.getExcerpt());
        post.setContent(dto.getContent());
        User author = userRepository.findByName(dto.getAuthorName())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName(dto.getAuthorName());
                    return userRepository.save(newUser);
                });
        post.setAuthor(author);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);
        tagService.attachTags(post, dto.getTags());
    }
    @Override
    public List<PostResponseDto> getAllPosts() {

        List<Post> posts = postRepository.findAll();
        List<PostResponseDto> dtoList = new ArrayList<>();

        for (Post post : posts) {
            dtoList.add(PostMapper.convertToDto(post));
        }
        return dtoList;
    }
    @Override
    public PostResponseDto getPostById(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return PostMapper.convertToDto(post);
    }
    @Override
    public Page<PostResponseDto> getPostPagination(String search, int pageNumber,
                                                   int pageSize,
                                                   Sort sort) {
        Specification<Post> specification = PostSpecification.getSpecification(search);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Post> postPage = postRepository.findAll(specification, pageable);

        List<PostResponseDto> dtoList = new ArrayList<>();

        for (Post post : postPage.getContent()) {
            dtoList.add(PostMapper.convertToDto(post));
        }
        return new PageImpl<>(dtoList, pageable,
                postPage.getTotalElements());
    }
    @Override
    public PostUpdateDto getPostForUpdate(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostUpdateDto dto = new PostUpdateDto();

        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setExcerpt(post.getExcerpt());
        dto.setContent(post.getContent());
        dto.setAuthorId(post.getAuthor().getId());
        dto.setAuthorName(post.getAuthor().getName());
        dto.setPublishedAt(post.getPublishedAt());

        // tags → string
        StringBuilder tagsBuilder = new StringBuilder();

        for (PostTag postTag : post.getPostTags()) {
            tagsBuilder.append(postTag.getTag().getName()).append(",");
        }
        if (tagsBuilder.length() > 0) {
            tagsBuilder.deleteCharAt(tagsBuilder.length() - 1);
        }
        dto.setTags(tagsBuilder.toString());
        return dto;
    }
    @Override
    public void updatePost(Long postId, PostUpdateDto dto) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        post.setTitle(dto.getTitle());
        post.setExcerpt(dto.getExcerpt());
        post.setContent(dto.getContent());
        post.setAuthor(author);
        tagService.updateTags(post, dto.getTags());
        //post.setPublishedAt(dto.getPublishedAt());
        postRepository.save(post);


    }
    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}