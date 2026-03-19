package org.blog.blog_application.repositories;

import org.blog.blog_application.models.Post;
import org.blog.blog_application.models.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    void deleteAllByPost(Post post);
}
