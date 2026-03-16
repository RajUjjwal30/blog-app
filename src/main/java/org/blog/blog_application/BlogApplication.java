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


//    @Bean
//    public CommandLineRunner loadDummyData(PostRepository postRepository, TagRepository tagRepository) {
//        return args -> {
//            if (postRepository.count() == 0) {
//
//                Tag javaTag = new Tag();
//                javaTag.setName("Java");
//                tagRepository.save(javaTag);
//
//                // POST 1
//                Post post = new Post();
//                post.setTitle("My First Spring Boot Blog");
//                post.setContent("First blog.......");
//                post.setExcerpt("Learning how to build a blog...");
//
//                PostTag pt = new PostTag();
//                pt.setPost(post);
//                pt.setTag(javaTag);
//
//                post.getPostTags().add(pt);
//
//                // POST 2
//                Post post2 = new Post();
//                post2.setTitle("My Second Spring Boot Blog");
//                post2.setContent("Second blog.......");
//                post2.setExcerpt("Learning more about Spring Boot...");
//
//                PostTag pt2 = new PostTag();
//                pt2.setPost(post2);
//                pt2.setTag(javaTag);
//
//                post2.getPostTags().add(pt2);
//
//                postRepository.save(post);
//                postRepository.save(post2);
//            }
//        };
//    }

}

