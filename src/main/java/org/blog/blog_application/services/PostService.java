package org.blog.blog_application.services;

import org.blog.blog_application.dtos.PostCreateDto;
import org.blog.blog_application.dtos.PostResponseDto;
import org.blog.blog_application.dtos.PostUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PostService {

    void createPost(PostCreateDto dto);

    List<PostResponseDto> getAllPosts();

    PostResponseDto getPostById(Long postId);

    Page<PostResponseDto> getPostPagination(String search,
                                            int pageNumber,
                                            int pageSize,
                                            Sort sort);

    PostUpdateDto getPostForUpdate(Long postId);

    void updatePost(Long postId, PostUpdateDto dto);

    void deletePost(Long postId);
}