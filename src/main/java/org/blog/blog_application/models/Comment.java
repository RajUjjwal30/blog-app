package org.blog.blog_application.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jdk.jshell.Snippet;
import lombok.*;

@Getter
@Setter
@Entity (name = "comments")
public class Comment extends BaseModel{
    private String name;
    private String email;
    private String content;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;


}
