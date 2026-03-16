package org.blog.blog_application.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@MappedSuperclass
public abstract class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id(pk)")
    private Long id;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
