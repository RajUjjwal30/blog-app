package org.blog.blog_application.services;

import org.blog.blog_application.models.Post;
import org.blog.blog_application.repositories.PostRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("PostServiceImplBasic")
public class PostServiceImpl implements PostService{
    private PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}
