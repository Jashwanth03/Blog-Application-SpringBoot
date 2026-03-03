package com.jash.blog.mappers;


import com.jash.blog.domain.Dto.TagResponse;
import com.jash.blog.domain.PostStatus;
import com.jash.blog.domain.entities.Post;
import com.jash.blog.domain.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;


import java.util.Set;

@Mapper(componentModel = "spring",
unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
    @Mapping(target ="postCount" , source = "posts" , qualifiedByName = "calculatePostCount")
    TagResponse toTagResponse(Tag tag);

    @Named("calculatePostCount")
    default Integer calculatePostCount(Set<Post> posts) {
        if(posts == null || posts.isEmpty()) {
            return 0;
        }

        return (int)posts.stream()
                .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                .count();
    }

}
