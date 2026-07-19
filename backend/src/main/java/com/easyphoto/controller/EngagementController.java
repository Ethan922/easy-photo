package com.easyphoto.controller;

import com.easyphoto.common.Result;
import com.easyphoto.dto.PageResult;
import com.easyphoto.dto.TutorialCard;
import com.easyphoto.service.EngagementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EngagementController {

    private final EngagementService engagementService;

    public EngagementController(EngagementService engagementService) {
        this.engagementService = engagementService;
    }

    @PostMapping("/tutorials/{id}/like")
    public Result<Void> like(@PathVariable Long id) {
        engagementService.like(id);
        return Result.success();
    }

    @DeleteMapping("/tutorials/{id}/like")
    public Result<Void> unlike(@PathVariable Long id) {
        engagementService.unlike(id);
        return Result.success();
    }

    @PostMapping("/tutorials/{id}/favorite")
    public Result<Void> favorite(@PathVariable Long id) {
        engagementService.favorite(id);
        return Result.success();
    }

    @DeleteMapping("/tutorials/{id}/favorite")
    public Result<Void> unfavorite(@PathVariable Long id) {
        engagementService.unfavorite(id);
        return Result.success();
    }

    /** 收藏页：当前用户收藏的教程。 */
    @GetMapping("/favorites")
    public Result<PageResult<TutorialCard>> myFavorites(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "12") long size) {
        return Result.success(engagementService.myFavorites(current, size));
    }
}
