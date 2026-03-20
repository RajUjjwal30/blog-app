package org.blog.blog_application.repositories;

import org.blog.blog_application.dtos.AuthorDto;
import org.blog.blog_application.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>,
        JpaSpecificationExecutor<Post> {
    List<Post> findAll();

    Optional<Post> findById(Long postId);

    void deleteById(Long postId);

    List<Post> findByAuthor_NameIgnoreCase(String name);

    @Query("SELECT DISTINCT u.name FROM posts p JOIN p.author u ORDER BY u.name ASC")
    List<String> findDistinctAuthorNames();
    //Page<Post> findAll(Specification<Post> specification, PageRequest pageRequest, Sort sort);

//    Page<Post> findAll(Pageable pageable);




}
