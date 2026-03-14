package org.blog.blog_application.controllers;

import org.blog.blog_application.services.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog")
public class PostController {
    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }
    @GetMapping("/posts")
    public String getAllPosts(Model model){
        model.addAttribute("posts", postService.getAllPosts());
        return "blog-home";
    }

}
