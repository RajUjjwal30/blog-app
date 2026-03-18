package org.blog.blog_application.services;

import org.blog.blog_application.dtos.UpdatePostDto;
import org.blog.blog_application.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();

    void createPostWithTags(Post post, String customTags);

    Post getSinglePost(Long postId);

    UpdatePostDto getPostForUpdate(Long postId);
    void updatePost(Long postId, UpdatePostDto dto);

    void deletePost(Long postId);

    Page<Post> getPostPagination(int pageNumber, int pageSize, Sort sort);

}
