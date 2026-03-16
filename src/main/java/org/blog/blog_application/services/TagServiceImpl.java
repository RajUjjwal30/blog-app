package org.blog.blog_application.services;

import org.blog.blog_application.models.Tag;
import org.blog.blog_application.repositories.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("TagServiceBasic")
public class TagServiceImpl implements TagService {
    private TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getOrCreateTag(String tagName) {
        Optional<Tag> optionalTag = tagRepository.findByNameIgnoreCase(tagName);
        if(optionalTag.isPresent()) {
           return optionalTag.get();
        }else{
            Tag newTag = new Tag();
            newTag.setName(tagName);
            return tagRepository.save(newTag);
        }
    }

//    @Override
//    public Optional<Tag> getTagById(Long tagId) {
//        return tagRepository.findById(tagId);
//    }
}
