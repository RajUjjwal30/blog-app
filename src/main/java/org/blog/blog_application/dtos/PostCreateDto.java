package org.blog.blog_application.dtos;

import lombok.Getter;
import lombok.Setter;
import org.blog.blog_application.models.User;

@Getter
@Setter
public class PostCreateDto {
    private String title;
    private String excerpt;
    private String content;
    private String authorName;
    private Boolean isPublished;
    private String tags;

}
