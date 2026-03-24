package org.blog.blog_application.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDto {
    private String displayName;
    private String username;
    private String email;
    private String password;
    private String role;
}
