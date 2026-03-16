package org.blog.blog_application.repositories;

import org.blog.blog_application.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAll();

    Optional<Tag> findByNameIgnoreCase(String tagName);
    //Optional<Tag> findById(Long id);
}
