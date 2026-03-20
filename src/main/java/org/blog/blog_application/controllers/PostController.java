package org.blog.blog_application.controllers;

import org.blog.blog_application.dtos.CommentDto;
import org.blog.blog_application.dtos.PostCreateDto;
import org.blog.blog_application.dtos.PostResponseDto;
import org.blog.blog_application.dtos.PostUpdateDto;
import org.blog.blog_application.models.Post;
import org.blog.blog_application.repositories.TagRepository;
import org.blog.blog_application.services.CommentService;
import org.blog.blog_application.services.PostService;
import org.blog.blog_application.services.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/blog")
public class PostController {
    private PostService postService;
    private CommentService commentService;
    private TagRepository tagRepository;
    private TagService tagService;

    public PostController(PostService postService, CommentService commentService, TagRepository tagRepository, TagService tagService) {

        this.postService = postService;
        this.commentService = commentService;
        this.tagRepository = tagRepository;
        this.tagService = tagService;
    }
    @GetMapping("/posts")
    public String getAllPosts(@RequestParam(defaultValue = "1")int start,
                              @RequestParam(defaultValue="1")int limit,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false, defaultValue = "") Long authorId,
                              @RequestParam(required = false,defaultValue = "publishedAt") String sortField ,
                              @RequestParam(required = false,defaultValue = "asc") String direction,
                              @RequestParam(required = false) List<Long> tagIds,Model model){

        if (start < 1) start = 1;
        if (limit <= 0) limit = 10;

        int pageIndex = (start-1) / limit;

        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Page<PostResponseDto> postPage = postService.getPostPagination(search,tagIds, authorId, pageIndex, limit, sort);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("start", start);
        model.addAttribute("limit", limit);
        model.addAttribute("totalPages", postPage.getTotalPages());
        //model.addAttribute("posts", postService.getAllPosts());
        model.addAttribute("search", search);
        model.addAttribute("sortBy", sortField);
        model.addAttribute("direction", direction);

        model.addAttribute("allTags", tagRepository.findAll());
//        model.addAttribute("selectedTagId", tagIds);

        model.addAttribute("authors",        postService.getAllAuthors());
        model.addAttribute("tags",        tagService.getAllTagsDto());
        model.addAttribute("selectedAuthor", "");
        return "blog-home";
    }
    @GetMapping("/posts/create")
    public String createPostForm(Model model){
        model.addAttribute("post", new PostCreateDto());
        return "create-Form";
    }
    @PostMapping("/posts")
    public String createPost(@ModelAttribute PostCreateDto dto){
        postService.createPost(dto);
        System.out.println("qwertyuiop"+dto.getAuthorName());
        return "redirect:/blog/posts";
    }
    @GetMapping("/posts/{postId}")
    public String viewPost(@PathVariable Long postId, Model model){
        PostResponseDto post = postService.getPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("commentDto", new CommentDto());
        model.addAttribute("comments", commentService.getCommentsByPostId(postId));
        return "post-view";
    }
    @GetMapping("/posts/update/{postId}")
    public String editForm(@PathVariable Long postId, Model model) {

        PostUpdateDto dto = postService.getPostForUpdate(postId);

        model.addAttribute("post", dto);
        return "edit-post";
    }
    @PostMapping("/posts/update/{postId}")
    public String updatePost(@PathVariable Long postId,
                             @ModelAttribute PostUpdateDto dto) {

        postService.updatePost(postId, dto);

        return "redirect:/blog/posts/" + postId;
    }
    @PostMapping("/posts/delete/{postId}")
    public String deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return "redirect:/blog/posts";
    }
//    @GetMapping("/posts/by-author")
//    public String filterByAuthor(
//            @RequestParam(required = false, defaultValue = "") String author,
//            Model model) {
//
//        model.addAttribute("posts",postService.getPostsByAuthorName(author));
//        model.addAttribute("authors",postService.getAllAuthorNames());
//        model.addAttribute("selectedAuthor", author);
//
//        return "blog-home";
//    }
}
