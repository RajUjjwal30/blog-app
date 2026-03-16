package org.blog.blog_application.mapper;

import org.blog.blog_application.dtos.UpdatePostDto;
import org.blog.blog_application.models.Post;
import org.blog.blog_application.models.PostTag;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostMapper {
    public UpdatePostDto toUpdateDTO(Post post) {

        UpdatePostDto dto = new UpdatePostDto();

        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthor(post.getAuthor());
        dto.setPublishedAt(post.getPublishedAt());

        StringBuilder tags = new StringBuilder();

        for (PostTag pt : post.getPostTags()) {

            if (tags.length() > 0) {
                tags.append(",");
            }

            tags.append(pt.getTag().getName());
        }
        dto.setTags(tags.toString());
        return dto;
    }
}
