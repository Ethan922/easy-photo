package com.easyphoto.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dimension")
public class Dimension {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String name;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createdAt;
}
