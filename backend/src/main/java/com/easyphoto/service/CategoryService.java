package com.easyphoto.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyphoto.common.BusinessException;
import com.easyphoto.dto.CategoryWithDimensions;
import com.easyphoto.entity.Category;
import com.easyphoto.entity.Dimension;
import com.easyphoto.mapper.CategoryMapper;
import com.easyphoto.mapper.DimensionMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final DimensionMapper dimensionMapper;

    public CategoryService(CategoryMapper categoryMapper, DimensionMapper dimensionMapper) {
        this.categoryMapper = categoryMapper;
        this.dimensionMapper = dimensionMapper;
    }

    // ---------- 分类 ----------

    public Category createCategory(String name) {
        Long count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, name));
        if (count != null && count > 0) {
            throw new BusinessException("分类名称已存在");
        }
        Category category = new Category();
        category.setName(name);
        categoryMapper.insert(category);
        return category;
    }

    public void deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        // 逻辑删除该分类及其下维度
        categoryMapper.deleteById(id);
        dimensionMapper.delete(new LambdaQueryWrapper<Dimension>()
                .eq(Dimension::getCategoryId, id));
    }

    // ---------- 维度 ----------

    public Dimension createDimension(Long categoryId, String name) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        // 同分类内维度名唯一（不同分类下可同名）
        Long count = dimensionMapper.selectCount(new LambdaQueryWrapper<Dimension>()
                .eq(Dimension::getCategoryId, categoryId)
                .eq(Dimension::getName, name));
        if (count != null && count > 0) {
            throw new BusinessException("该分类下维度名称已存在");
        }
        Dimension dimension = new Dimension();
        dimension.setCategoryId(categoryId);
        dimension.setName(name);
        dimensionMapper.insert(dimension);
        return dimension;
    }

    public void deleteDimension(Long id) {
        Dimension dimension = dimensionMapper.selectById(id);
        if (dimension == null) {
            throw new BusinessException(404, "维度不存在");
        }
        dimensionMapper.deleteById(id);
    }

    // ---------- 查询 ----------

    public List<CategoryWithDimensions> listWithDimensions() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getId));
        List<Dimension> dimensions = dimensionMapper.selectList(
                new LambdaQueryWrapper<Dimension>().orderByAsc(Dimension::getId));

        List<CategoryWithDimensions> result = new ArrayList<>();
        for (Category category : categories) {
            CategoryWithDimensions cwd = new CategoryWithDimensions();
            cwd.setId(category.getId());
            cwd.setName(category.getName());
            cwd.setDimensions(dimensions.stream()
                    .filter(d -> d.getCategoryId().equals(category.getId()))
                    .map(d -> {
                        CategoryWithDimensions.DimensionItem item = new CategoryWithDimensions.DimensionItem();
                        item.setId(d.getId());
                        item.setCategoryId(d.getCategoryId());
                        item.setName(d.getName());
                        return item;
                    })
                    .collect(Collectors.toList()));
            result.add(cwd);
        }
        return result;
    }
}
