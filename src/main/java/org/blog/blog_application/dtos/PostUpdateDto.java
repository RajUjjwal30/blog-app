package org.blog.blog_application.dtos;

import lombok.Getter;
import lombok.Setter;
import org.blog.blog_application.models.User;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostUpdateDto {
        private Long id;
        private String title;
        private String excerpt;
        private String content;
        private Long authorId;
        private String authorName;
        private String authorUsername;
        private LocalDateTime publishedAt;
        private String tags;
}
