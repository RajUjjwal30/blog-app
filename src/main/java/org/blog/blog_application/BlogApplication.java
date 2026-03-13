package org.blog.blog_application;

import org.blog.blog_application.models.Post;
import org.blog.blog_application.models.PostTag;
import org.blog.blog_application.models.Tag;
import org.blog.blog_application.repositories.PostRepository;
import org.blog.blog_application.repositories.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }


    @Bean
    public CommandLineRunner loadDummyData(PostRepository postRepository, TagRepository tagRepository) {
        return args -> {
            if (postRepository.count() == 0) {
                System.out.println("Database is empty. Loading dummy data...");

                // Create tags
                Tag javaTag = new Tag();
                javaTag.setName("Java");
                tagRepository.save(javaTag);

                // Create post
                Post post = new Post();
                post.setTitle("My First Spring Boot Blog");
                post.setContent("First blog.......");
                post.setExcerpt("Learning how to build a blog...");

                // Link them
                PostTag pt = new PostTag();
                pt.setPost(post);
                pt.setTag(javaTag);
                post.getPostTags().add(pt);


                Post post2 = new Post();
                post2.setTitle("My First Spring Boot Blog");
                post2.setContent("First blog.......");
                post2.setExcerpt("Learning how to build a blog...");

                // Link them
                PostTag pt2 = new PostTag();
                pt2.setPost(post);
                pt2.setTag(javaTag);
                post2.getPostTags().add(pt);

                postRepository.save(post);
                postRepository.save(post2);
                System.out.println("Dummy data loaded successfully!");
            }
        };
    }
}
