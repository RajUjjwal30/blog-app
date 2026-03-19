package org.blog.blog_application.services;

import org.blog.blog_application.models.Post;
import org.blog.blog_application.models.Tag;

import java.util.List;

public interface TagService {
    List<Tag> getAllTags();

    //Optional<Tag> getTagById(Long tagId);
    Tag getOrCreateTag(String tagName);

    void attachTags(Post post, String tags);
    void updateTags(Post post, String tags);

}
