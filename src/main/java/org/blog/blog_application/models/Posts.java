package org.blog.blog_application.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Posts extends BaseModel{
    @Column(nullable = false)
    private String title;
    private String excerpt;
    @Column(nullable = false,columnDefinition = "LONGTEXT")
    private String content;
    private String author;
    private LocalDate published_at;
    private boolean is_published;

}
