package org.blog.blog_application.controllers;

import org.blog.blog_application.dtos.RegistrationDto;
import org.blog.blog_application.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthenticationController {
    private UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) model.addAttribute("loginError", true);
        return "authentication/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegistrationDto());
        return "authentication/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegistrationDto dto, Model model) {
        try {
            userService.register(dto);
            return "redirect:/login?registered=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registerDto", dto);
            return "authentication/register";
        }
    }
}
