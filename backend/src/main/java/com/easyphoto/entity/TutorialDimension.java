package com.easyphoto.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tutorial_dimension")
public class TutorialDimension {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tutorialId;

    private Long dimensionId;

    @TableLogic
    private Integer deleted;
}
