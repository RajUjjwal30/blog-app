package org.blog.blog_application.controllers;

import org.blog.blog_application.dtos.CommentDto;
import org.blog.blog_application.dtos.UpdatePostDto;
import org.blog.blog_application.models.Post;
import org.blog.blog_application.services.CommentService;
import org.blog.blog_application.services.PostService;
import org.blog.blog_application.services.TagService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blog")
public class PostController {
    private PostService postService;
    private TagService tagService;
    private CommentService commentService;

    public PostController(PostService postService, TagService tagService, CommentService commentService) {

        this.postService = postService;
        this.tagService = tagService;
        this.commentService=commentService;
    }
    @GetMapping("/posts")
    public String getAllPosts(@RequestParam(defaultValue = "1")int pageNumber,
                              @RequestParam(defaultValue="5")int pageSize,Model model){
        int pageIndex = pageNumber - 1;
        Page<Post> postPage = postService.getPostPagination(pageIndex, 5);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", postPage.getTotalPages());
        //model.addAttribute("posts", postService.getAllPosts());
        return "blog-home";
    }
    @GetMapping("/posts/create")
    public String createPostForm(Model model){
        model.addAttribute("post", new Post());
        return "create-Form";
    }
    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post, @RequestParam(value = "customTags",required = false) String customTags){
        postService.createPostWithTags(post, customTags);
        return "redirect:/blog/posts";
    }
    @GetMapping("/posts/{postId}")
    public String viewPost(@PathVariable("postId") Long postId, Model model){
        Post post = postService.getSinglePost(postId);
        model.addAttribute("post", post);
        model.addAttribute("commentDto", new CommentDto());
        model.addAttribute("comments", commentService.getCommentsByPostId(postId));
        return "post-view";
    }
    @GetMapping("/posts/update/{postId}")
    public String editForm(@PathVariable Long postId, Model model) {

        UpdatePostDto dto = postService.getPostForUpdate(postId);

        model.addAttribute("post", dto);
        model.addAttribute("allTags", tagService.getAllTags());

        return "edit-post";
    }
    @PostMapping("/posts/update/{postId}")
    public String updatePost(@PathVariable Long postId,
                             @ModelAttribute UpdatePostDto dto) {

        postService.updatePost(postId, dto);

        return "redirect:/blog/posts/" + postId;
    }
    @PostMapping("/posts/delete/{postId}")
    public String deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return "redirect:/blog/posts";
    }

}
