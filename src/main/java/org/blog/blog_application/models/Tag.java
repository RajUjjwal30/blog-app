package org.blog.blog_application.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity (name="tags")
@EqualsAndHashCode(of = "name")
public class Tag extends BaseModel{
    @Column(unique=true, nullable=false, length=50)
    private String name;
}
