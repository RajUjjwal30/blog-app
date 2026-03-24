package org.blog.blog_application.services;

import org.blog.blog_application.dtos.AuthorDto;
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

    Page<PostResponseDto> getPostPagination(String search,List<Long> tagIds, Long authorId, int pageNumber, int pageSize, Sort sort);

    PostUpdateDto getPostForUpdate(Long postId);

    void updatePost(Long postId, PostUpdateDto dto, String currentUsername, boolean isAdmin);

    void deletePost(Long postId, String currentUsername, boolean isAdmin);

    List<PostResponseDto> getPostsByAuthorName(String authorName);

    List<String> getAllAuthorNames();

    public List<AuthorDto> getAllAuthors();

    String getUsernameByPostId(Long postId);

}