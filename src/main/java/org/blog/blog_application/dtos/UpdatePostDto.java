package org.blog.blog_application.dtos;

import lombok.Getter;
import lombok.Setter;
import org.blog.blog_application.models.PostTag;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UpdatePostDto {
        private Long id;
        private String title;
        private String content;
        private String author;
        private LocalDateTime publishedAt;
        private String tags;
}
