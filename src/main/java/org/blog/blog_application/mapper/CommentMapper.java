package org.blog.blog_application.mapper;

import org.blog.blog_application.dtos.CommentDto;
import org.blog.blog_application.models.Comment;
import org.blog.blog_application.models.Post;

public class CommentMapper {
    //  Entity => DTO
      public static CommentDto toDto(Comment comment) {

        if (comment == null) return null;

        CommentDto dto = new CommentDto();

        dto.setId(comment.getId());
        dto.setName(comment.getName());
        dto.setEmail(comment.getEmail());
        dto.setContent(comment.getContent());

        // Convert Post => postId
        if (comment.getPost() != null) {
            dto.setPostId(comment.getPost().getId());
        }

        return dto;
    }

    //DTO → Entity
    public static Comment toEntity(CommentDto dto) {

        if (dto == null) return null;

        Comment comment = new Comment();

        comment.setId(dto.getId());
        comment.setName(dto.getName());
        comment.setEmail(dto.getEmail());
        comment.setContent(dto.getContent());

        // Convert postId → Post (only ID reference)
        if (dto.getPostId() != null) {
            Post post = new Post();
            post.setId(dto.getPostId());
            comment.setPost(post);
        }

        return comment;
    }
}