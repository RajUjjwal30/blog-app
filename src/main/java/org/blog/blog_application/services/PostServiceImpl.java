package org.blog.blog_application.services;

import org.blog.blog_application.models.Post;
import org.blog.blog_application.models.PostTag;
import org.blog.blog_application.models.Tag;
import org.blog.blog_application.repositories.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service("PostServiceImplBasic")
public class PostServiceImpl implements PostService{
    private PostRepository postRepository;
    private TagService tagService;

    public PostServiceImpl(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    @Transactional
    public void createPostWithTags(Post post, String customTags) {
        //Post savedPost = postRepository.save(post);

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
        postRepository.save(post);
    }

    @Override
    public Post getSinglePost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.get();
    }


}
