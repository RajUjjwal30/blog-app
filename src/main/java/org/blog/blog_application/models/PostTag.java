package org.blog.blog_application.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "post_tags")
public class PostTag extends BaseModel{
    @ManyToOne
    @JoinColumn(name = "(foreign_key)post_id")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "(foreign_key)tag_id")
    private Tag tag;
}
