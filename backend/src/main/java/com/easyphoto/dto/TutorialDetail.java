package com.easyphoto.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 教程详情：元数据 + 富文本正文 + 作者 + 维度 + 当前用户互动状态。
 */
@Data
public class TutorialDetail {
    private Long id;
    private String title;
    private String coverUrl;
    private String content;
    private String visibility;
    private Long authorId;
    private String authorName;
    private Integer likeCount;
    private Integer favoriteCount;
    private List<DimensionRef> dimensions;
    private boolean liked;
    private boolean favorited;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class DimensionRef {
        private Long id;
        private String name;
        private Long categoryId;
        private String categoryName;
    }
}
