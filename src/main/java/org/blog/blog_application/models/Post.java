package org.blog.blog_application.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity(name = "posts")
public class Post extends BaseModel{
    @Column(nullable = false)
    private String title;
    private String excerpt;
    @Column(nullable = false,columnDefinition = "LONGTEXT")
    private String content;
    private String author;
    private LocalDate published_at;
    private boolean is_published;
    @OneToMany(mappedBy = "post")
    private Set<PostTag> postTags = new HashSet<>();


}
