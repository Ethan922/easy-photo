package com.easyphoto.controller;

import com.easyphoto.common.Result;
import com.easyphoto.dto.CategoryWithDimensions;
import com.easyphoto.dto.NameRequest;
import com.easyphoto.entity.Category;
import com.easyphoto.entity.Dimension;
import com.easyphoto.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** 查询所有分类及维度（公开） */
    @GetMapping
    public Result<List<CategoryWithDimensions>> list() {
        return Result.success(categoryService.listWithDimensions());
    }

    /** 新增分类（管理员） */
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public Result<Category> createCategory(@Valid @RequestBody NameRequest req) {
        return Result.success(categoryService.createCategory(req.getName()));
    }

    /** 删除分类（管理员） */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /** 在分类下新增维度（管理员） */
    @PostMapping("/{categoryId}/dimensions")
    @PreAuthorize("hasRole('admin')")
    public Result<Dimension> createDimension(@PathVariable Long categoryId,
                                             @Valid @RequestBody NameRequest req) {
        return Result.success(categoryService.createDimension(categoryId, req.getName()));
    }

    /** 删除维度（管理员） */
    @DeleteMapping("/dimensions/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> deleteDimension(@PathVariable Long id) {
        categoryService.deleteDimension(id);
        return Result.success();
    }
}
