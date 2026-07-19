package com.easyphoto.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class TutorialRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "封面图不能为空")
    private String coverUrl;

    @NotBlank(message = "正文不能为空")
    private String content;

    /** public / private，缺省 public */
    private String visibility = "public";

    /** 关联维度 id，至少一个 */
    @NotEmpty(message = "至少选择一个维度")
    private List<Long> dimensionIds;
}
