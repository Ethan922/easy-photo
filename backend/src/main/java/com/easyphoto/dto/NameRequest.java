package com.easyphoto.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NameRequest {
    @NotBlank(message = "名称不能为空")
    private String name;
}
