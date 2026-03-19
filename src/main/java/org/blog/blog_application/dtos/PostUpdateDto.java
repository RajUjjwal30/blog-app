package org.blog.blog_application.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostUpdateDto {
        private Long id;
        private String title;
        private String excerpt;
        private String content;
        private String author;
        private LocalDateTime publishedAt;
        private String tags;
}
