package org.blog.blog_application.controllers;

import org.blog.blog_application.dtos.CommentDto;
import org.blog.blog_application.dtos.PostCreateDto;
import org.blog.blog_application.dtos.PostResponseDto;
import org.blog.blog_application.dtos.PostUpdateDto;
import org.blog.blog_application.repositories.TagRepository;
import org.blog.blog_application.services.CommentService;
import org.blog.blog_application.services.PostService;
import org.blog.blog_application.services.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
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
    public String getAllPosts(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) Long authorId,
                              @RequestParam(required = false, defaultValue = "publishedAt") String sortField,
                              @RequestParam(required = false, defaultValue = "desc") String direction,
                              @RequestParam(required = false) List<Long> tagIds, Model model) {

        if (page < 1) page = 1;
        int pageSize = 10;
        int pageIndex = page - 1;

        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Page<PostResponseDto> postPage = postService.getPostPagination(search, tagIds, authorId, pageIndex, pageSize, sort);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("sortField", sortField);
        model.addAttribute("direction", direction);

        model.addAttribute("authors", postService.getAllAuthors());
        model.addAttribute("tags", tagService.getAllTagsDto());
        model.addAttribute("selectedTagIds", tagIds);
        model.addAttribute("selectedAuthorId", authorId);
        return "blog-home";
    }
    @GetMapping("/posts/create")
    public String createPostForm(Model model, Authentication auth){
        model.addAttribute("post", new PostCreateDto());
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            model.addAttribute("allUsers", postService.getAllAuthors());
        }
        return "create-Form";
    }
    @PostMapping("/posts")
    public String createPost(@ModelAttribute PostCreateDto dto, Authentication auth){
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        // Admin can pick any author; author always uses their own name
        if (!isAdmin || dto.getAuthorUsername() == null || dto.getAuthorUsername().isBlank()) {
            dto.setAuthorUsername(auth.getName());
        }
        postService.createPost(dto);
        return "redirect:/blog/posts";
    }
    @GetMapping("/posts/{postId}")
    public String viewPost(@PathVariable Long postId, Model model, Authentication authentication){
        PostResponseDto post = postService.getPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("commentDto", new CommentDto());
        model.addAttribute("comments", commentService.getCommentsByPostId(postId));
        boolean isOwner = authentication != null && authentication.getName().equals(post.getAuthorUsername());
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isAdmin", isAdmin);
        return "post-view";
    }
    @GetMapping("/posts/update/{postId}")
    public String editForm(@PathVariable Long postId, Model model, Authentication auth) {
        PostUpdateDto dto = postService.getPostForUpdate(postId);
        model.addAttribute("post", dto);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            model.addAttribute("allUsers", postService.getAllAuthors());
        }
        return "edit-post";
    }
    @PostMapping("/posts/update/{postId}")
    public String updatePost(@PathVariable Long postId,
                             @ModelAttribute PostUpdateDto dto,
                             Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        postService.updatePost(postId, dto, auth.getName(), isAdmin);
        return "redirect:/blog/posts/" + postId;
    }
    @PostMapping("/posts/delete/{postId}")
    public String deletePost(@PathVariable Long postId, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        postService.deletePost(postId, auth.getName(), isAdmin);
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
