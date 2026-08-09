package com.example.shopapi.category;

import com.example.shopapi.category.dto.CategoryRequest;
import com.example.shopapi.category.dto.CategoryResponse;
import com.example.shopapi.category.dto.CategoryTreeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {


    @Mapping(
            target = "parentId",
            source = "parent.id"
    )
    CategoryResponse toResponse(Category category);

    default CategoryTreeResponse toTreeResponse(
            Category category
    ) {
        return new CategoryTreeResponse(
                category.getId(),
                category.getName(),
                category.getChildren()
                        .stream()
                        .map(this::toTreeResponse)
                        .toList()
        );
    }

    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "parent",
            ignore = true
    )
    @Mapping(
            target = "children",
            ignore = true
    )
    @Mapping(
            target = "products",
            ignore = true
    )
    Category toEntity(CategoryRequest request);


    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "parent",
            ignore = true
    )
    @Mapping(
            target = "children",
            ignore = true
    )
    @Mapping(
            target = "products",
            ignore = true
    )
    void updateEntity(
            CategoryRequest request,
            @MappingTarget Category category
    );

}