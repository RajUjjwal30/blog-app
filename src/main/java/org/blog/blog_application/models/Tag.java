package org.blog.blog_application.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity (name="tags")
public class Tag extends BaseModel{
    private String name;
}
