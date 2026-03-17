package org.blog.blog_application.controllers;

import org.blog.blog_application.dtos.CommentDto;
import org.blog.blog_application.services.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
