package com.jash.blog.mappers;


import com.jash.blog.domain.Dto.CategoryDto;
import com.jash.blog.domain.Dto.CreateCategoryRequest;
import com.jash.blog.domain.PostStatus;
import com.jash.blog.domain.entities.Category;
import com.jash.blog.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
//unmappedTargetPolicy --> ensures that custom field in dto which entity does not have is mapped
//3 option --> ERROR , WARN & IGNORE
public interface CategoryMapper {

    //Since postCount is Ignored---> it is explicitly mapped using this annotation
    @Mapping(target = "postCount", source = "posts",qualifiedByName = "calculatePostCount")
    //postCount ---> Dto field && posts ---> Category field
    CategoryDto mapToDto(Category category);

    Category maptoEntity(CreateCategoryRequest createCategoryRequest);

    @Named("calculatePostCount")
    default  long calculatePostCount(List<Post> posts){

        if(posts==null) return 0;

        return posts.stream()
                .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                .count();

    }
}
