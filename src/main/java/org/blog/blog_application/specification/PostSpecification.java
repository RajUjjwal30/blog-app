package org.blog.blog_application.specification;

import jakarta.persistence.criteria.*;
import org.blog.blog_application.models.Post;
import org.blog.blog_application.models.PostTag;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> build(String search, Long authorId, List<Long> tagIds) {
        return Specification.where(buildSearch(search))
                .and(buildAuthor(authorId))
                .and(buildTags(tagIds));
    }

    private static Specification<Post> buildSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isEmpty()) return cb.conjunction();
            String pattern = "%" + search.toLowerCase() + "%";
            Join<Object, Object> author = root.join("author", JoinType.LEFT);
            Join<Object, Object> tag = root.join("postTags", JoinType.LEFT).join("tag", JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("content")), pattern),
                    cb.like(cb.lower(root.get("excerpt")), pattern),
                    cb.like(cb.lower(author.get("name")), pattern),
                    cb.like(cb.lower(tag.get("name")), pattern)
            );
        };
    }

    private static Specification<Post> buildAuthor(Long authorId) {
        return (root, query, cb) -> {
            if (authorId == null) return cb.conjunction();
            return cb.equal(root.join("author", JoinType.INNER).get("id"), authorId);
        };
    }

    private static Specification<Post> buildTags(List<Long> tagIds) {
        return (root, query, cb) -> {
            if (tagIds == null || tagIds.isEmpty()) return cb.conjunction();
            List<Predicate> predicates = new ArrayList<>();
            for (Long tagId : tagIds) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<PostTag> ptRoot = sub.from(PostTag.class);
                sub.select(ptRoot.get("post").get("id")).where(
                        cb.equal(ptRoot.get("post").get("id"), root.get("id")),
                        cb.equal(ptRoot.get("tag").get("id"), tagId));
                predicates.add(cb.exists(sub));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}