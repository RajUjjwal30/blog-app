package org.blog.blog_application.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;
    private LocalDateTime publishedAt;
    private boolean isPublished;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<PostTag> postTags = new HashSet<>();


    @PrePersist
    public void prePersist() {
        this.publishedAt = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.publishedAt = LocalDateTime.now();
    }

}
