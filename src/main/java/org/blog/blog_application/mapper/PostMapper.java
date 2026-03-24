package org.blog.blog_application.mapper;

import org.blog.blog_application.dtos.PostResponseDto;
import org.blog.blog_application.models.Post;
import org.blog.blog_application.models.PostTag;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostMapper {
    public static PostResponseDto convertToDto(Post post) {

        PostResponseDto dto = new PostResponseDto();

        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setExcerpt(post.getExcerpt());
        dto.setContent(post.getContent());
        dto.setAuthor(post.getAuthor() != null ? post.getAuthor().getName() : "Unknown");
        dto.setAuthorUsername(post.getAuthor() != null ? post.getAuthor().getUsername() : null);
        dto.setPublishedAt(post.getPublishedAt());

        List<String> tagNames = new ArrayList<>();
        for (PostTag postTag : post.getPostTags()) {

//            if (tags.length() > 0) {
//                tags.append(",");
//            }
            tagNames.add(postTag.getTag().getName());
        }
        dto.setTags(tagNames);
        return dto;
    }
}
