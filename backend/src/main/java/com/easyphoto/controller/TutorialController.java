package com.easyphoto.controller;

import com.easyphoto.common.Result;
import com.easyphoto.dto.PageResult;
import com.easyphoto.dto.TutorialCard;
import com.easyphoto.dto.TutorialDetail;
import com.easyphoto.dto.TutorialRequest;
import com.easyphoto.service.TutorialService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tutorials")
public class TutorialController {

    private final TutorialService tutorialService;

    public TutorialController(TutorialService tutorialService) {
        this.tutorialService = tutorialService;
    }

    /** 教程列表（公开，可见性在业务层控制）。支持按维度、作者筛选与分页。 */
    @GetMapping
    public Result<PageResult<TutorialCard>> list(
            @RequestParam(required = false) Long dimensionId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "12") long size) {
        return Result.success(tutorialService.list(dimensionId, authorId, current, size));
    }

    /** 教程详情（公开，可见性在业务层控制）。 */
    @GetMapping("/{id}")
    public Result<TutorialDetail> detail(@PathVariable Long id) {
        return Result.success(tutorialService.detail(id));
    }

    /** 创建教程（需登录）。 */
    @PostMapping
    public Result<Map<String, Long>> create(@Valid @RequestBody TutorialRequest req) {
        Long id = tutorialService.create(req);
        Map<String, Long> data = new HashMap<>();
        data.put("id", id);
        return Result.success(data);
    }

    /** 编辑教程（作者或管理员）。 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TutorialRequest req) {
        tutorialService.update(id, req);
        return Result.success();
    }

    /** 删除教程（作者或管理员）。 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tutorialService.delete(id);
        return Result.success();
    }
}
