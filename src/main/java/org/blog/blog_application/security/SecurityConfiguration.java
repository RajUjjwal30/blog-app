package org.blog.blog_application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfiguration(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ── Public: anyone can read posts, search, filter ──
                        .requestMatchers("/blog/posts", "/blog/posts/{id}").permitAll()
                        .requestMatchers("/register", "/login").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // ── Create post: AUTHOR or ADMIN only ──
                        .requestMatchers("/blog/posts/create").hasAnyRole("AUTHOR", "ADMIN")

                        // ── Update/delete: AUTHOR or ADMIN (ownership checked in service) ──
                        .requestMatchers("/blog/posts/update/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers("/blog/posts/delete/**").hasAnyRole("AUTHOR", "ADMIN")

                        // ── Comments: creating is public; delete/update checked in controller ──
                        .requestMatchers("/blog/comments").permitAll()
                        .requestMatchers("/blog/comments/**").permitAll()

                        // ── Everything else: must be logged in ──
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")            // our custom login page
                        .defaultSuccessUrl("/blog/posts", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/blog/posts")
                        .permitAll()
                );

        return http.build();
    }

}
