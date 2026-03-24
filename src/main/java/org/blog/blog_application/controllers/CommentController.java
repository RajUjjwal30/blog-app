package org.blog.blog_application.controllers;

import org.blog.blog_application.dtos.CommentDto;
import org.blog.blog_application.services.CommentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blog/comments")
public class CommentController {
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public String createComment(@RequestParam Long postId, @ModelAttribute CommentDto commentDto) {
        commentDto.setPostId(postId);
        commentService.createComment(commentDto);
        return "redirect:/blog/posts/" + commentDto.getPostId();
    }

    @PostMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId,
                                @RequestParam Long postId,
                                Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        commentService.deleteComment(commentId, auth.getName(), isAdmin);
        return "redirect:/blog/posts/" + postId;
    }

    @PostMapping("/update/{commentId}")
    public String updateComment(@PathVariable Long commentId,
                                @RequestParam Long postId,
                                @RequestParam String content,
                                Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        commentService.updateComment(commentId, content, auth.getName(), isAdmin);
        return "redirect:/blog/posts/" + postId;
    }
}
