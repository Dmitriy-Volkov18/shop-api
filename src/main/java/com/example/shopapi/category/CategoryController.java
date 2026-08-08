package com.example.shopapi.category;

import com.example.shopapi.category.dto.CategoryRequest;
import com.example.shopapi.category.dto.CategoryResponse;
import com.example.shopapi.category.dto.CategoryTreeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryFacade facade;

    @PostMapping
    public CategoryResponse create(
            @Valid @RequestBody CategoryRequest request
    ){
        return facade.create(request);
    }

    @GetMapping
    public List<CategoryResponse> getAll(){
        return facade.getAll();
    }

    @GetMapping("/tree")
    public List<CategoryTreeResponse> getTree() {
        return facade.getTree();
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(
            @PathVariable Long id
    ){
        return facade.getById(id);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ){
        return facade.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id
    ){
        facade.delete(id);
    }

}