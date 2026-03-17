package org.blog.blog_application.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.blog.blog_application.models.Post;
@Getter
@Setter
public class CommentDto {
    private Long id;
    private String name;
    private String email;
    private String content;
    private Long postId;


}
