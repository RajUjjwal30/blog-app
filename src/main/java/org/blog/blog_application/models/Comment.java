package org.blog.blog_application.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity (name = "comments")
public class Comment extends BaseModel{
    private String name;
    private String email;
    private String comment;
    @ManyToOne
    @JoinColumn(name = "(foreign_key)post_id")
    private Post post;
}
