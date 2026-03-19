package org.blog.blog_application.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateDto {
    private String title;
    private String excerpt;
    private String content;
    private String author;
    private Boolean isPublished;
    private String tags;

}
