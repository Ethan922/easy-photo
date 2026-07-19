package com.easyphoto.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easyphoto.common.BusinessException;
import com.easyphoto.common.HtmlSanitizer;
import com.easyphoto.dto.*;
import com.easyphoto.entity.*;
import com.easyphoto.mapper.*;
import com.easyphoto.security.AuthUser;
import com.easyphoto.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TutorialService {

    private final TutorialMapper tutorialMapper;
    private final TutorialDimensionMapper tutorialDimensionMapper;
    private final DimensionMapper dimensionMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final TutorialLikeMapper likeMapper;
    private final TutorialFavoriteMapper favoriteMapper;

    public TutorialService(TutorialMapper tutorialMapper,
                           TutorialDimensionMapper tutorialDimensionMapper,
                           DimensionMapper dimensionMapper,
                           CategoryMapper categoryMapper,
                           UserMapper userMapper,
                           TutorialLikeMapper likeMapper,
                           TutorialFavoriteMapper favoriteMapper) {
        this.tutorialMapper = tutorialMapper;
        this.tutorialDimensionMapper = tutorialDimensionMapper;
        this.dimensionMapper = dimensionMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
        this.likeMapper = likeMapper;
        this.favoriteMapper = favoriteMapper;
    }

    // ---------- 创建 ----------

    @Transactional
    public Long create(TutorialRequest req) {
        AuthUser current = SecurityUtil.currentUser();
        validateVisibility(req.getVisibility());
        validateDimensions(req.getDimensionIds());

        Tutorial tutorial = new Tutorial();
        tutorial.setTitle(req.getTitle());
        tutorial.setCoverUrl(req.getCoverUrl());
        tutorial.setContent(HtmlSanitizer.sanitize(req.getContent()));
        tutorial.setVisibility(req.getVisibility());
        tutorial.setAuthorId(current.getUserId());
        tutorial.setLikeCount(0);
        tutorial.setFavoriteCount(0);
        tutorialMapper.insert(tutorial);

        saveDimensions(tutorial.getId(), req.getDimensionIds());
        return tutorial.getId();
    }

    // ---------- 编辑 ----------

    @Transactional
    public void update(Long id, TutorialRequest req) {
        Tutorial tutorial = getExisting(id);
        requireAuthorOrAdmin(tutorial);
        validateVisibility(req.getVisibility());
        validateDimensions(req.getDimensionIds());

        tutorial.setTitle(req.getTitle());
        tutorial.setCoverUrl(req.getCoverUrl());
        tutorial.setContent(HtmlSanitizer.sanitize(req.getContent()));
        tutorial.setVisibility(req.getVisibility());
        tutorialMapper.updateById(tutorial);

        // 重建维度关联：先逻辑删除旧的，再插入新的
        tutorialDimensionMapper.delete(new LambdaQueryWrapper<TutorialDimension>()
                .eq(TutorialDimension::getTutorialId, id));
        saveDimensions(id, req.getDimensionIds());
    }

    // ---------- 删除 ----------

    @Transactional
    public void delete(Long id) {
        Tutorial tutorial = getExisting(id);
        requireAuthorOrAdmin(tutorial);
        tutorialMapper.deleteById(id);
        tutorialDimensionMapper.delete(new LambdaQueryWrapper<TutorialDimension>()
                .eq(TutorialDimension::getTutorialId, id));
    }

    // ---------- 详情 ----------

    public TutorialDetail detail(Long id) {
        Tutorial tutorial = getExisting(id);
        AuthUser current = SecurityUtil.currentUserOrNull();
        if (!canView(tutorial, current)) {
            throw new BusinessException(403, "无权查看该教程");
        }

        TutorialDetail detail = new TutorialDetail();
        detail.setId(tutorial.getId());
        detail.setTitle(tutorial.getTitle());
        detail.setCoverUrl(tutorial.getCoverUrl());
        detail.setContent(tutorial.getContent());
        detail.setVisibility(tutorial.getVisibility());
        detail.setAuthorId(tutorial.getAuthorId());
        detail.setAuthorName(usernameOf(tutorial.getAuthorId()));
        detail.setLikeCount(tutorial.getLikeCount());
        detail.setFavoriteCount(tutorial.getFavoriteCount());
        detail.setCreatedAt(tutorial.getCreatedAt());
        detail.setUpdatedAt(tutorial.getUpdatedAt());
        detail.setDimensions(dimensionRefs(tutorial.getId()));

        if (current != null) {
            detail.setLiked(likeMapper.selectCount(new LambdaQueryWrapper<TutorialLike>()
                    .eq(TutorialLike::getUserId, current.getUserId())
                    .eq(TutorialLike::getTutorialId, id)) > 0);
            detail.setFavorited(favoriteMapper.selectCount(new LambdaQueryWrapper<TutorialFavorite>()
                    .eq(TutorialFavorite::getUserId, current.getUserId())
                    .eq(TutorialFavorite::getTutorialId, id)) > 0);
        }
        return detail;
    }

    // ---------- 列表（按维度筛选 + 可见性 + 分页） ----------

    public PageResult<TutorialCard> list(Long dimensionId, Long authorId, long current, long size) {
        AuthUser user = SecurityUtil.currentUserOrNull();

        // 若按维度筛选，先取该维度下的教程 id
        List<Long> tutorialIds = null;
        if (dimensionId != null) {
            tutorialIds = tutorialDimensionMapper.selectList(new LambdaQueryWrapper<TutorialDimension>()
                    .eq(TutorialDimension::getDimensionId, dimensionId))
                    .stream().map(TutorialDimension::getTutorialId).distinct().collect(Collectors.toList());
            if (tutorialIds.isEmpty()) {
                return new PageResult<>(Collections.emptyList(), 0, current, size);
            }
        }

        LambdaQueryWrapper<Tutorial> wrapper = new LambdaQueryWrapper<>();
        if (tutorialIds != null) {
            wrapper.in(Tutorial::getId, tutorialIds);
        }
        if (authorId != null) {
            wrapper.eq(Tutorial::getAuthorId, authorId);
        }
        applyVisibility(wrapper, user);
        wrapper.orderByDesc(Tutorial::getCreatedAt);

        IPage<Tutorial> page = tutorialMapper.selectPage(new Page<>(current, size), wrapper);
        List<TutorialCard> cards = page.getRecords().stream()
                .map(this::toCard).collect(Collectors.toList());
        return new PageResult<>(cards, page.getTotal(), current, size);
    }

    /**
     * 可见性过滤：管理员看全部；登录用户看 public + 本人 private；游客仅 public。
     */
    private void applyVisibility(LambdaQueryWrapper<Tutorial> wrapper, AuthUser user) {
        if (user != null && "admin".equals(user.getRole())) {
            return; // 管理员不加限制
        }
        if (user != null) {
            Long uid = user.getUserId();
            wrapper.and(w -> w.eq(Tutorial::getVisibility, "public")
                    .or(o -> o.eq(Tutorial::getVisibility, "private").eq(Tutorial::getAuthorId, uid)));
        } else {
            wrapper.eq(Tutorial::getVisibility, "public");
        }
    }

    private boolean canView(Tutorial tutorial, AuthUser user) {
        if ("public".equals(tutorial.getVisibility())) {
            return true;
        }
        if (user == null) {
            return false;
        }
        return "admin".equals(user.getRole()) || tutorial.getAuthorId().equals(user.getUserId());
    }

    /** 供其他 service 复用的可见性判断。 */
    public boolean canViewCard(Tutorial tutorial, AuthUser user) {
        return canView(tutorial, user);
    }

    /** 供其他 service 复用的卡片转换。 */
    public TutorialCard toCardPublic(Tutorial t) {
        return toCard(t);
    }

    private TutorialCard toCard(Tutorial t) {
        TutorialCard card = new TutorialCard();
        card.setId(t.getId());
        card.setTitle(t.getTitle());
        card.setCoverUrl(t.getCoverUrl());
        card.setVisibility(t.getVisibility());
        card.setAuthorId(t.getAuthorId());
        card.setAuthorName(usernameOf(t.getAuthorId()));
        card.setLikeCount(t.getLikeCount());
        card.setFavoriteCount(t.getFavoriteCount());
        card.setCreatedAt(t.getCreatedAt());
        return card;
    }

    private String usernameOf(Long userId) {
        User u = userMapper.selectById(userId);
        return u == null ? null : u.getUsername();
    }

    private List<TutorialDetail.DimensionRef> dimensionRefs(Long tutorialId) {
        List<Long> dimIds = tutorialDimensionMapper.selectList(new LambdaQueryWrapper<TutorialDimension>()
                .eq(TutorialDimension::getTutorialId, tutorialId))
                .stream().map(TutorialDimension::getDimensionId).collect(Collectors.toList());
        if (dimIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Dimension> dims = dimensionMapper.selectBatchIds(dimIds);
        Map<Long, String> catNames = categoryMapper.selectList(null).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        return dims.stream().map(d -> {
            TutorialDetail.DimensionRef ref = new TutorialDetail.DimensionRef();
            ref.setId(d.getId());
            ref.setName(d.getName());
            ref.setCategoryId(d.getCategoryId());
            ref.setCategoryName(catNames.get(d.getCategoryId()));
            return ref;
        }).collect(Collectors.toList());
    }

    // ---------- 校验与辅助 ----------

    private void validateVisibility(String visibility) {
        if (!"public".equals(visibility) && !"private".equals(visibility)) {
            throw new BusinessException("可见范围只能为 public 或 private");
        }
    }

    private void validateDimensions(List<Long> dimensionIds) {
        if (dimensionIds == null || dimensionIds.isEmpty()) {
            throw new BusinessException("至少选择一个维度");
        }
        long valid = dimensionMapper.selectCount(new LambdaQueryWrapper<Dimension>()
                .in(Dimension::getId, dimensionIds));
        if (valid != new HashSet<>(dimensionIds).size()) {
            throw new BusinessException("存在无效的维度");
        }
    }

    private void saveDimensions(Long tutorialId, List<Long> dimensionIds) {
        for (Long dimId : new LinkedHashSet<>(dimensionIds)) {
            TutorialDimension td = new TutorialDimension();
            td.setTutorialId(tutorialId);
            td.setDimensionId(dimId);
            tutorialDimensionMapper.insert(td);
        }
    }

    private Tutorial getExisting(Long id) {
        Tutorial tutorial = tutorialMapper.selectById(id);
        if (tutorial == null) {
            throw new BusinessException(404, "教程不存在");
        }
        return tutorial;
    }

    private void requireAuthorOrAdmin(Tutorial tutorial) {
        AuthUser current = SecurityUtil.currentUser();
        if (!"admin".equals(current.getRole())
                && !tutorial.getAuthorId().equals(current.getUserId())) {
            throw new BusinessException(403, "无权操作他人教程");
        }
    }
}
