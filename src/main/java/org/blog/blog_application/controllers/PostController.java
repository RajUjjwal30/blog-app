package org.blog.blog_application.controllers;

import org.blog.blog_application.models.Post;
import org.blog.blog_application.services.PostService;
import org.blog.blog_application.services.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blog")
public class PostController {
    private PostService postService;
    private TagService tagService;

    public PostController(PostService postService, TagService tagService) {

        this.postService = postService;
        this.tagService = tagService;
    }
    @GetMapping("/posts")
    public String getAllPosts(Model model){
        model.addAttribute("posts", postService.getAllPosts());
        return "blog-home";
    }
    @GetMapping("/posts/create")
    public String createPostForm(Model model){
        model.addAttribute("post", new Post());
        return "create-Form";
    }
    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post, @RequestParam(value = "customTags",required = false) String customTags){
        System.out.println("######## AUTHOR FROM FORM ######## " + post.getAuthor());
        postService.createPostWithTags(post, customTags);
        return "redirect:/blog/posts";
    }
    @GetMapping("/posts/{postId}")
    public String viewPost(@PathVariable("postId") Long postId, Model model){
        Post post = postService.getSinglePost(postId);
        model.addAttribute("post", post);
        return "post-view";
    }
}
