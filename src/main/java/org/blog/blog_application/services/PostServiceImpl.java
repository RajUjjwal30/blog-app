package org.blog.blog_application.services;

import org.blog.blog_application.dtos.UpdatePostDto;
import org.blog.blog_application.mapper.PostMapper;
import org.blog.blog_application.models.Post;
import org.blog.blog_application.models.PostTag;
import org.blog.blog_application.models.Tag;
import org.blog.blog_application.repositories.PostRepository;
import org.blog.blog_application.repositories.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service("PostServiceImplBasic")
public class PostServiceImpl implements PostService{
    private final TagRepository tagRepository;
    private PostRepository postRepository;
    private TagService tagService;
    private PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, TagService tagService,
                           PostMapper postMapper, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.postMapper=postMapper;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    @Transactional
    public void createPostWithTags(Post post, String customTags) {
        Set<String> validTags = new HashSet<>();
        if(customTags != null && ! customTags.trim().isEmpty()){
            String[] tags = customTags.split(",");
            for(String tag : tags){
                String trimmedTag = tag.trim().toLowerCase();
                if(! trimmedTag.isEmpty()){
                    validTags.add(trimmedTag);
                }
            }
        }
        for(String tag : validTags){
            Tag finalTag = tagService.getOrCreateTag(tag);

            PostTag postTagConnector = new PostTag();
            postTagConnector.setPost(post);
            postTagConnector.setTag(finalTag);

            post.getPostTags().add(postTagConnector);
        }
        post.setPublished(true);
        postRepository.save(post);
    }

    @Override
    public Post getSinglePost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.get();
    }
    @Override
    public UpdatePostDto getPostForUpdate(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return postMapper.toUpdateDTO(post);
    }
    @Override
    @Transactional
    public void updatePost(Long id, UpdatePostDto dto) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(dto.getAuthor());
        post.setPublishedAt(dto.getPublishedAt());

        post.getPostTags().clear();

        if (dto.getTags() != null) {

            String[] tags = dto.getTags().split(",");

            for (String tagName : tags) {

                Tag tag = tagService.getOrCreateTag(tagName.trim());

                PostTag pt = new PostTag();
                pt.setPost(post);
                pt.setTag(tag);

                post.getPostTags().add(pt);
            }
        }
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.deleteById(postId);
    }


}
