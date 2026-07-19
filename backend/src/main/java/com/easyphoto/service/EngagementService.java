package com.easyphoto.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyphoto.common.BusinessException;
import com.easyphoto.dto.PageResult;
import com.easyphoto.dto.TutorialCard;
import com.easyphoto.entity.Tutorial;
import com.easyphoto.entity.TutorialFavorite;
import com.easyphoto.entity.TutorialLike;
import com.easyphoto.mapper.TutorialFavoriteMapper;
import com.easyphoto.mapper.TutorialLikeMapper;
import com.easyphoto.mapper.TutorialMapper;
import com.easyphoto.security.AuthUser;
import com.easyphoto.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EngagementService {

    private final TutorialMapper tutorialMapper;
    private final TutorialLikeMapper likeMapper;
    private final TutorialFavoriteMapper favoriteMapper;
    private final TutorialService tutorialService;

    public EngagementService(TutorialMapper tutorialMapper,
                             TutorialLikeMapper likeMapper,
                             TutorialFavoriteMapper favoriteMapper,
                             TutorialService tutorialService) {
        this.tutorialMapper = tutorialMapper;
        this.likeMapper = likeMapper;
        this.favoriteMapper = favoriteMapper;
        this.tutorialService = tutorialService;
    }

    // ---------- 点赞 ----------

    @Transactional
    public void like(Long tutorialId) {
        AuthUser user = SecurityUtil.currentUser();
        Tutorial tutorial = requireVisible(tutorialId, user);
        boolean exists = likeMapper.selectCount(new LambdaQueryWrapper<TutorialLike>()
                .eq(TutorialLike::getUserId, user.getUserId())
                .eq(TutorialLike::getTutorialId, tutorialId)) > 0;
        if (exists) {
            return; // 幂等
        }
        TutorialLike like = new TutorialLike();
        like.setUserId(user.getUserId());
        like.setTutorialId(tutorialId);
        likeMapper.insert(like);
        tutorial.setLikeCount(tutorial.getLikeCount() + 1);
        tutorialMapper.updateById(tutorial);
    }

    @Transactional
    public void unlike(Long tutorialId) {
        AuthUser user = SecurityUtil.currentUser();
        int removed = likeMapper.delete(new LambdaQueryWrapper<TutorialLike>()
                .eq(TutorialLike::getUserId, user.getUserId())
                .eq(TutorialLike::getTutorialId, tutorialId));
        if (removed > 0) {
            Tutorial tutorial = tutorialMapper.selectById(tutorialId);
            if (tutorial != null) {
                tutorial.setLikeCount(Math.max(0, tutorial.getLikeCount() - removed));
                tutorialMapper.updateById(tutorial);
            }
        }
    }

    // ---------- 收藏 ----------

    @Transactional
    public void favorite(Long tutorialId) {
        AuthUser user = SecurityUtil.currentUser();
        Tutorial tutorial = requireVisible(tutorialId, user);
        boolean exists = favoriteMapper.selectCount(new LambdaQueryWrapper<TutorialFavorite>()
                .eq(TutorialFavorite::getUserId, user.getUserId())
                .eq(TutorialFavorite::getTutorialId, tutorialId)) > 0;
        if (exists) {
            return; // 幂等
        }
        TutorialFavorite fav = new TutorialFavorite();
        fav.setUserId(user.getUserId());
        fav.setTutorialId(tutorialId);
        favoriteMapper.insert(fav);
        tutorial.setFavoriteCount(tutorial.getFavoriteCount() + 1);
        tutorialMapper.updateById(tutorial);
    }

    @Transactional
    public void unfavorite(Long tutorialId) {
        AuthUser user = SecurityUtil.currentUser();
        int removed = favoriteMapper.delete(new LambdaQueryWrapper<TutorialFavorite>()
                .eq(TutorialFavorite::getUserId, user.getUserId())
                .eq(TutorialFavorite::getTutorialId, tutorialId));
        if (removed > 0) {
            Tutorial tutorial = tutorialMapper.selectById(tutorialId);
            if (tutorial != null) {
                tutorial.setFavoriteCount(Math.max(0, tutorial.getFavoriteCount() - removed));
                tutorialMapper.updateById(tutorial);
            }
        }
    }

    // ---------- 收藏页 ----------

    public PageResult<TutorialCard> myFavorites(long current, long size) {
        AuthUser user = SecurityUtil.currentUser();
        List<Long> tutorialIds = favoriteMapper.selectList(new LambdaQueryWrapper<TutorialFavorite>()
                .eq(TutorialFavorite::getUserId, user.getUserId())
                .orderByDesc(TutorialFavorite::getCreatedAt))
                .stream().map(TutorialFavorite::getTutorialId).collect(Collectors.toList());
        if (tutorialIds.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0, current, size);
        }
        // 手动分页收藏的教程 id，再过滤可见性
        int from = (int) Math.max(0, (current - 1) * size);
        if (from >= tutorialIds.size()) {
            return new PageResult<>(Collections.emptyList(), tutorialIds.size(), current, size);
        }
        int to = (int) Math.min(tutorialIds.size(), from + size);
        List<Long> pageIds = tutorialIds.subList(from, to);
        List<TutorialCard> cards = tutorialMapper.selectBatchIds(pageIds).stream()
                .filter(t -> tutorialService.canViewCard(t, user))
                .map(tutorialService::toCardPublic)
                .collect(Collectors.toList());
        return new PageResult<>(cards, tutorialIds.size(), current, size);
    }

    private Tutorial requireVisible(Long tutorialId, AuthUser user) {
        Tutorial tutorial = tutorialMapper.selectById(tutorialId);
        if (tutorial == null) {
            throw new BusinessException(404, "教程不存在");
        }
        if (!tutorialService.canViewCard(tutorial, user)) {
            throw new BusinessException(403, "无权操作该教程");
        }
        return tutorial;
    }
}
