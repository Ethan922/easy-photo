package com.easyphoto.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教程卡片（列表项）：封面 + 标题 + 基础统计。
 */
@Data
public class TutorialCard {
    private Long id;
    private String title;
    private String coverUrl;
    private String visibility;
    private Long authorId;
    private String authorName;
    private Integer likeCount;
    private Integer favoriteCount;
    private LocalDateTime createdAt;
}
