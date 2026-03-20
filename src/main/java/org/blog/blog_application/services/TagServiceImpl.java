package org.blog.blog_application.services;

import org.blog.blog_application.dtos.AuthorDto;
import org.blog.blog_application.dtos.TagDto;
import org.blog.blog_application.models.Post;
import org.blog.blog_application.models.PostTag;
import org.blog.blog_application.models.Tag;
import org.blog.blog_application.repositories.PostRepository;
import org.blog.blog_application.repositories.PostTagRepository;
import org.blog.blog_application.repositories.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("TagServiceBasic")
public class TagServiceImpl implements TagService {
    private PostRepository postRepository;
    private TagRepository tagRepository;
    private PostTagRepository postTagRepository;

    public TagServiceImpl(TagRepository tagRepository, PostRepository postRepository,
                          PostTagRepository postTagRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.postTagRepository = postTagRepository;
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getOrCreateTag(String tagName) {
        Optional<Tag> optionalTag = tagRepository.findByNameIgnoreCase(tagName);
        if(optionalTag.isPresent()) {
           return optionalTag.get();
        }else{
            Tag newTag = new Tag();
            newTag.setName(tagName);
            return tagRepository.save(newTag);
        }
    }

    @Override
    public void attachTags(Post post, String tags) {

        if (tags == null || tags.trim().isEmpty()) return;

        String[] tagArray = tags.split(",");
        Set<String> uniqueTags = new HashSet<>();
        for (String tagName : tagArray) {
            tagName = tagName.trim().toLowerCase();
            if (tagName.isEmpty() || uniqueTags.contains(tagName)) continue;
            uniqueTags.add(tagName);
            Tag tag = getOrCreateTag(tagName);
            PostTag postTag = new PostTag();
            postTag.setPost(post);
            postTag.setTag(tag);
            post.getPostTags().add(postTag);
        }
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void updateTags(Post post, String tags) {
        post.setPostTags(new HashSet<>());
        postTagRepository.deleteAllByPost(post);
        postRepository.save(post);
        attachTags(post, tags);
    }

    public List<TagDto> getAllTagsDto() {

        return tagRepository.findAll().stream().map( tag -> {
            TagDto tagDto = new TagDto();
            tagDto.setId(tag.getId());
            tagDto.setName(tag.getName());
            return tagDto;
        }).toList();
    }

}
