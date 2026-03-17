package org.blog.blog_application.repositories;

import org.blog.blog_application.models.Post;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAll();

    Optional<Post> findById(Long postId);

    void deleteById(Long postId);

//    Page<Post> findAll(Pageable pageable);

}
