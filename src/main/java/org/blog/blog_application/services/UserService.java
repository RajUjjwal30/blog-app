package org.blog.blog_application.services;

import org.blog.blog_application.dtos.RegistrationDto;
import org.blog.blog_application.models.User;

public interface UserService {
    User register(RegistrationDto dto);
}
