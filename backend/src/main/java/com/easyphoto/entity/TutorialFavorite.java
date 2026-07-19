package com.easyphoto.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tutorial_favorite")
public class TutorialFavorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long tutorialId;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;
}
