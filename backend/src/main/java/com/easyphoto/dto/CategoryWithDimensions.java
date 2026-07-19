package com.easyphoto.dto;

import lombok.Data;

import java.util.List;

/**
 * 分类含其下维度的返回结构。
 */
@Data
public class CategoryWithDimensions {
    private Long id;
    private String name;
    private List<DimensionItem> dimensions;

    @Data
    public static class DimensionItem {
        private Long id;
        private Long categoryId;
        private String name;
    }
}
