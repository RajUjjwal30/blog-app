package org.blog.blog_application.specification;

import jakarta.persistence.criteria.*;
import org.blog.blog_application.models.Post;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PostSpecification {
    public static Specification<Post>getSpecification(String search){
        return new Specification<Post>() {
            @Override
            public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if(search == null || search.isEmpty()){
                    return criteriaBuilder.conjunction();
                }
                String likePattern = "%" + search.toLowerCase() + "%";

                List<Predicate> conditions = new ArrayList<>();
                conditions.add(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern)
                );

                conditions.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), likePattern));

                conditions.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), likePattern)
                );
                conditions.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("excerpt")), likePattern)
                );

                Join<Object, Object> tagJoin = root.join("postTags").join("tag");

                conditions.add(
                        criteriaBuilder.like(criteriaBuilder.lower(tagJoin.get("name")), likePattern)
                );

                query.distinct(true);

                return criteriaBuilder.or(conditions.toArray(new Predicate[0]));
            }
        };
    }
}
