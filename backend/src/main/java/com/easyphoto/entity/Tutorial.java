package com.easyphoto.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tutorial")
public class Tutorial {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String coverUrl;

    /** 富文本正文（HTML） */
    private String content;

    /** public / private */
    private String visibility;

    private Long authorId;

    private Integer likeCount;

    private Integer favoriteCount;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
