package com.chen.LeoBlog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.activityEvent.Activity;
import com.chen.LeoBlog.activityEvent.ActivityData;
import com.chen.LeoBlog.activityEvent.ActivityEnum;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.config.ThreadPoolConfig;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.dto.UserDTO;
import com.chen.LeoBlog.enums.MsgTypeEnum;
import com.chen.LeoBlog.mapper.ArticleMapper;
import com.chen.LeoBlog.po.Article;
import com.chen.LeoBlog.po.Label;
import com.chen.LeoBlog.po.Message;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.publisher.ActivityEventPublisher;
import com.chen.LeoBlog.service.*;
import com.chen.LeoBlog.utils.BaseUtil;
import com.chen.LeoBlog.utils.IdUtil;
import com.chen.LeoBlog.utils.MessageUtil;
import com.chen.LeoBlog.utils.RedisUtil;
import com.chen.LeoBlog.vo.request.PageBaseReq;
import com.chen.LeoBlog.vo.request.PageBaseReqWithUserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 1
 * @description 针对表【lb_article】的数据库操作Service实现
 * @createDate 2022-10-14 17:35:27
 */
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private UserService userService;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private LabelService labelService;
    @Resource
    private CommentService commentService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SetArticleLabelService setArticleLabelService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private IdUtil idUtil;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageUtil messageUtil;
    @Resource
    private ActivityEventPublisher activityEventPublisher;

    @Resource(name = ThreadPoolConfig.BLOG_EXECUTOR)
    private ThreadPoolTaskExecutor asyncExecutor;

    @Override
    public ResultInfo<?> getArticleList(PageBaseReq pageBaseReq) {
        Integer size = pageBaseReq.getPageSize();
        Integer page = pageBaseReq.getPageNo();
        log.debug("page: [{}], size: [{}]", page, size);
        Page<Article> pageObj = new Page<>(page, size);
        articleMapper.selectPage(pageObj, new QueryChainWrapper<>(articleMapper).eq("is_article", 1).orderByDesc("article_update_date").getWrapper());
        return ResultInfo.success(pageObj);
    }

    @Override
    public ResultInfo getArticle(Long articleId) {
        log.debug("articleId: [{}]", articleId);
        String key = RedisConstant.ARTICLE_INFO + articleId;
        Article article = redisUtil.getObjWithCache(key, articleId, Article.class, RedisConstant.ARTICLE_INFO_TTL, TimeUnit.DAYS, (id) -> query().eq("article_id", id).one());
        if (article == null) {
            log.debug("articleId: [{}] 不存在", articleId);
            return ResultInfo.fail("该文章不存在");
        }
        User user = (User) userService.getUser(article.getUserId()).getData();
        Object labels = labelService.getLabelList(articleId).getData();

        Map<String, Object> map = new HashMap<>();
        BeanUtil.beanToMap(article, map, false, true);
        map.put("user", user);
        map.put("labels", labels);
        return ResultInfo.success(map);
    }

    @Override
    public ResultInfo addArticle(Map<String, Object> map) {

        Article article = new Article();
        Object labels1 = map.get("labels");
        Long articleId = idUtil.nextId(Article.class);
        if (labels1 != null) {
            List<Label> labels = JSONUtil.toList(JSONUtil.toJsonStr(labels1), Label.class);
            List<Long> ids = labels.stream().map(Label::getLabelId).toList();
            setArticleLabelService.setLabelList(articleId, ids);
        }

        article.setArticleId(articleId);
        UserDTO user = BaseUtil.getUserFromLocal();
        Long userId = user.getUserId();
        article.setUserId(user.getUserId());
        article.setArticleTitle(map.get("articleTitle").toString());
        article.setArticleContent(map.get("articleContent").toString());
        article.setArticleUpdateDate(new Date());
        article.setArticlePic(map.getOrDefault("articlePic", "").toString());
        article.setIsArticle(Integer.parseInt(map.getOrDefault("isArticle", 0).toString()));

        boolean isSuccess = save(article);
        if (!isSuccess) {
            return ResultInfo.fail("添加失败");
        }

//        String msgTitle = messageUtil.getArticleMessage("", article.getArticleTitle());

//        String key = RedisConstant.ARTICLE_INFO + articleId;
//        redisUtil.saveObjAsJson(key, article, RedisConstant.ARTICLE_INFO_TTL, TimeUnit.DAYS);
        // TODO：推送文章给关注者
//        // 如果是草稿，无需推送
//        if (article.getIsArticle() == 0) return ResultInfo.success("保存成功");
//        // 将文章推送到关注者的收件箱中
//        String followKey = RedisConstant.FAN_USER_LIST + user.getUserId();
//        Set<String> followers = redisTemplate.opsForSet().members(followKey);
//        if (followers != null && !followers.isEmpty()) {
//            List<Long> list = followers.stream().map(Long::parseLong).toList();
//            list.forEach(id -> {
//                // 异步保存消息
//                asyncExecutor.execute(() -> {
//                    // 遍历添加到收件箱中
//                    Long msgId = idUtil.nextId(Message.class);
//                    Message message = new Message(msgId, user.getUserId(), id, msgTitle, MsgTypeEnum.PUBLISH_ARTICLE, articleId.toString());
//                    boolean isSaved = messageService.saveActivityMessage(message);
//                    if (!isSaved) {
//                        log.error("消息保存失败:{}", msgId);
//                    }
//                    redisTemplate.opsForZSet().add(RedisConstant.MESSAGE_BOX_PREFIX + id, msgId + "", System.currentTimeMillis());
//                });
//            });
//        }
        ActivityData build = ActivityData.builder().articleId(articleId).articleTitle(article.getArticleTitle()).build();
        Activity activity = Activity.builder().userId(userId).targetId(userId)
                .createTime(new Date())
                .type(ActivityEnum.ARTICLE_PUBLISH.getActivityEventId())
                .activityData(build).build();

        activityEventPublisher.publish(activity);


        return ResultInfo.success(articleId);

    }

    @Override
    public ResultInfo deleteArticle(Long articleId) {
        //只有作者才能删除，需要判断
        UserDTO user = BaseUtil.getUserFromLocal();
        if (user == null) return ResultInfo.fail("请先登录");
        Article article = query().eq("article_id", articleId).one();
        if (article == null) {
            return ResultInfo.fail("该文章不存在，请刷新页面");
        }
        if (!article.getUserId().equals(user.getUserId())) {
            return ResultInfo.fail("权限不足");
        }
        boolean isSuccess = update().eq("article_id", articleId).remove();
        String key = RedisConstant.ARTICLE_INFO + articleId;
        redisTemplate.delete(key);
        commentService.deleteCommentByArticleId(articleId);
        if (!isSuccess) {
            return ResultInfo.fail("删除失败");
        }
        return ResultInfo.success("删除成功");
    }

    @Override
    @Transactional
    public ResultInfo updateArticle(Map<String, Object> map) {
        if (-1L == Long.parseLong(map.get("articleId").toString())) {
            return addArticle(map);
        }
        Article article = query().eq("article_id", map.get("articleId")).one();
        Object labels1 = map.get("labels");
        if (labels1 != null) {
            List<Label> labels = JSONUtil.toList(JSONUtil.toJsonStr(labels1), Label.class);
            List<Long> ids = labels.stream().map(Label::getLabelId).toList();
            setArticleLabelService.setLabelList(article.getArticleId(), ids);
        }

        article.setArticleTitle(map.get("articleTitle").toString());
        article.setArticleContent(map.get("articleContent").toString());
        article.setArticleUpdateDate(new Date());
        article.setArticlePic(map.getOrDefault("articlePic", "").toString());
        article.setIsArticle(Integer.parseInt(map.get("isArticle").toString()));

        boolean isSuccess = updateById(article);
        String key = RedisConstant.ARTICLE_INFO + article.getArticleId();
        redisTemplate.delete(key);
        if (!isSuccess) {
            return ResultInfo.fail("更新失败");
        }
        return ResultInfo.success(article.getArticleId());
    }

    @Override
    public ResultInfo<?> getArticleListByUserId(PageBaseReqWithUserId pageBaseReq) {
        Long userId = pageBaseReq.getUserId();
        if (userId == null) userId = BaseUtil.getUserFromLocal().getUserId();
        Page<Article> articlePage = new Page<>(pageBaseReq.getPageNo(), pageBaseReq.getPageSize());
        articleMapper.selectPage(articlePage, new QueryChainWrapper<>(articleMapper).eq("user_id", userId).eq("is_article", 1).orderByDesc("article_update_date").getWrapper());
        return ResultInfo.success(articlePage);
    }

    @Override
    public ResultInfo getArticlesListByUserId(PageBaseReq pageBaseReq) {
        Long userId = BaseUtil.getUserFromLocal().getUserId();
        Integer page = pageBaseReq.getPageNo();
        Integer size = pageBaseReq.getPageSize();
        Page<Article> articlePage = new Page<>(page, size);
        articleMapper.selectPage(articlePage, new QueryChainWrapper<>(articleMapper).eq("user_id", userId).eq("is_article", 0).orderByDesc("article_update_date").getWrapper());
        return ResultInfo.success(articlePage);
    }

    @Override
    public ResultInfo collectArticle(Long articleId) {
        String key = RedisConstant.ARTICLE_COLLECT + articleId;
        Article article = query().eq("article_id", articleId).one();
        Long receiverId = article.getUserId();
        UserDTO user = BaseUtil.getUserFromLocal();
        Long userId = user.getUserId();
        if (userId.equals(receiverId)) {
            return ResultInfo.fail("不能收藏自己的文章");
        }
        Boolean isContain = redisTemplate.opsForSet().isMember(key, userId.toString());
        if (Boolean.TRUE.equals(isContain)) {
            redisTemplate.opsForSet().remove(key, userId.toString());
            update().eq("article_id", articleId).setSql("article_collects = article_collects - 1").update();
            redisTemplate.delete(RedisConstant.ARTICLE_INFO + articleId);
            return ResultInfo.success("取消收藏成功");
        } else {
            boolean isSuccess = false;
            try {
                redisTemplate.opsForSet().add(key, userId.toString());
                isSuccess = update().eq("article_id", articleId).setSql("article_collects = article_collects + 1").update();
                redisTemplate.delete(RedisConstant.ARTICLE_INFO + articleId);
                // 异步保存消息
//                asySaveMsg(article, user, 2);

                // 封装活动事件
                ActivityData activityData = ActivityData.builder().articleId(articleId).articleTitle(article.getArticleTitle())
                        .userId(userId).build();
                Activity activity = Activity.builder().type(ActivityEnum.ARTICLE_COLLECT.getActivityEventId())
                        .targetId(article.getUserId()).userId(userId)
                        .createTime(new Date()).activityData(activityData).build();
                activityEventPublisher.publish(activity);
            } catch (Exception e) {
                log.error("收藏失败", e);
            }
            return isSuccess ? ResultInfo.success("收藏成功") : ResultInfo.fail("收藏失败");
        }
    }

    @Override
    public ResultInfo getArticleListByContent(String content, Integer page, Integer size) {
        Page<Article> articlePage = new Page<>(page, size);
        articleMapper.selectPage(articlePage, new QueryChainWrapper<>(articleMapper).like("article_content", content).orderByDesc("article_update_date").getWrapper());
        return ResultInfo.success(articlePage);
    }

    @Override
    public ResultInfo getFollowArticles(int offset, Long lastScore) {
        UserDTO user = BaseUtil.getUserFromLocal();
        int count = 10;
        String messageBox = RedisConstant.MESSAGE_BOX_PREFIX + user.getUserId();
        // 取出所有的文章id
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(messageBox, 0, lastScore, offset, count);
        if (typedTuples == null || typedTuples.isEmpty()) {
            return ResultInfo.success(new ArrayList<>());
        }
        // 转化为Long
        Double score = RandomUtil.randomDouble();
        // 重置偏移量
        offset = 1;
        List<Long> articleIds = new ArrayList<>();
        int originalSize = typedTuples.size();

        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            assert score != null;
            // 计算当前查到数据中最后一个分数的重复个数
            if (score.equals(typedTuple.getScore())) {
                offset++;
            } else { // 如果不相等，说明已经到了下一个分数的数据，重置偏移量
                score = typedTuple.getScore();
                offset = 1;
            }
            long value = Long.parseLong(Objects.requireNonNull(typedTuple.getValue()));
            articleIds.add(value);
        }
        String ids = StrUtil.join(",", articleIds);
        // 查询，并且保证顺序
        //无需担心文章被删除，因为文章被删除后，就查不出对应的文章。
        List<Article> articles = query().in("article_id", articleIds).last("order by field(article_id," + ids + ")").list();
        Set<Long> existIds = articles.stream().map(Article::getArticleId).collect(Collectors.toSet());
        // 如果查询出来的文章id和redis中的id不一致，说明有文章被删除了，需要删除redis中的数据
        if (existIds.size() != originalSize) {
            for (Long id : articleIds) {
                if (!existIds.contains(id)) {
                    redisTemplate.opsForZSet().remove(messageBox, id.toString());
                }
            }
        }

        return ResultInfo.success(Map.of("articles", articles, "offset", offset, "lastScore", score.longValue()));
    }

    @Override
    public ResultInfo getArticleSumByUserId(Long userId) {
        Integer articleSize = query().eq("user_id", userId).eq("is_article", 1).count();
        Integer scriptSize = query().eq("user_id", userId).eq("is_article", 0).count();
        Integer commentSize = commentService.query().eq("user_id", userId).eq("comment_parent_id", -1).count();
        return ResultInfo.success(Map.of("articleSize", articleSize, "scriptSize", scriptSize, "commentSize", commentSize));
    }

    @Override
    public ResultInfo getArticleListBySortId(Long sortId) {
        List<Article> articleList = query().eq("sort_id", sortId).orderByDesc("article_update_date").list();
        return ResultInfo.success(articleList);
    }

    @Override
    public ResultInfo getArticleSumBySortId(Long sortId) {
        Integer count = query().eq("sort_id", sortId).count();
        return ResultInfo.success(count);
    }

    @Override
    public ResultInfo getArticleListByLabelId(Long labelId, Map<String, Object> map, Integer page, Integer size) {
//        System.out.println(map.get("ids"));
//        System.out.println(map);
        String[] ids = map.get("ids").toString().substring(1, map.get("ids").toString().length() - 1).split(", ");
        List<Long> longs = Arrays.stream(ids).toList().stream().map(Long::parseLong).toList();
        Page<Article> articlePage = new Page<>(page, size);
        List<Long> articles = new ArrayList<>();
        setArticleLabelService.query().eq("label_id", labelId).list().forEach(setArticleLabel -> {
            if (longs.contains(setArticleLabel.getArticleId())) {
                articles.add(setArticleLabel.getArticleId());
            }
        });
        articleMapper.selectPage(articlePage, new QueryChainWrapper<>(articleMapper).in("article_id", articles.toString().substring(1, articles.toString().length() - 1)).orderByDesc("article_update_date").getWrapper());
        return ResultInfo.success(articlePage);
    }

    @Override
    public ResultInfo getArticleSumByLabelId(Long labelId) {
        Integer count = query().like("article_label", labelId).count();
        return ResultInfo.success(count);
    }

    @Override
    public ResultInfo getArticleListByKeyword(String keyword, Integer page, Integer size) {
        Page<Article> articlePage = new Page<>(page, size);
        articleMapper.selectPage(articlePage, new QueryChainWrapper<>(articleMapper).like("article_title", keyword).orderByDesc("article_update_date").getWrapper());
        return ResultInfo.success(articlePage);
    }

    @Override
    public ResultInfo likeArticle(Long articleId) {
        String key = RedisConstant.ARTICLE_LIKE + articleId;
        Article article = query().eq("article_id", articleId).one();
        Long receiverId = article.getUserId();
        UserDTO user = BaseUtil.getUserFromLocal();
        Long userId = user.getUserId();
        if (userId.equals(receiverId)) {
            return ResultInfo.fail("不能点赞自己的文章");
        }
        Boolean isContain = redisTemplate.opsForSet().isMember(key, userId.toString());
        if (Boolean.TRUE.equals(isContain)) {
            redisTemplate.opsForSet().remove(key, userId.toString());
            update().eq("article_id", articleId).setSql("article_likes = article_likes - 1").update();
            redisTemplate.delete(RedisConstant.ARTICLE_INFO + articleId);
            return ResultInfo.success("取消点赞成功");
        } else {
            boolean isSuccess = false;
            try {
                redisTemplate.opsForSet().add(key, userId.toString());
                isSuccess = update().eq("article_id", articleId).setSql("article_likes = article_likes + 1").update();
                redisTemplate.delete(RedisConstant.ARTICLE_INFO + articleId);
                // 异步保存消息
//                asySaveMsg(article, user, 3);
                // 封装活动事件
                ActivityData activityData = ActivityData.builder().articleId(articleId).articleTitle(article.getArticleTitle())
                        .userId(userId).build();
                Activity activity = Activity.builder().type(ActivityEnum.ARTICLE_LIKE.getActivityEventId())
                        .targetId(article.getUserId()).userId(userId)
                        .createTime(new Date()).activityData(activityData).build();
                activityEventPublisher.publish(activity);

            } catch (Exception e) {
                log.error("点赞失败", e);
            }
            return isSuccess ? ResultInfo.success("点赞成功") : ResultInfo.fail("点赞失败");
        }

    }

    /**
     * type:0-发表文章 1-评论文章 2-收藏文章 3-点赞文章
     *
     * @param article
     * @param user
     * @param type
     */
    private void asySaveMsg(Article article, UserDTO user, Integer type) {
        asyncExecutor.execute(() -> {
            // 遍历添加到收件箱中
            Long msgId = idUtil.nextId(Message.class);
            Long receiverId = article.getUserId();
            String msgTitle = "";
            switch (type) {
                case 0 -> msgTitle = messageUtil.getArticleMessage("", article.getArticleTitle());
                case 1 -> msgTitle = messageUtil.getCommentMessage("", article.getArticleTitle());
                case 2 -> msgTitle = messageUtil.getCollectMessage("", article.getArticleTitle());
                case 3 -> msgTitle = messageUtil.getLikeMessage("", article.getArticleTitle());
            }

            MsgTypeEnum m;
            if (type == 0) {
                m = MsgTypeEnum.PUBLISH_ARTICLE;
            } else if (type == 1) {
                m = MsgTypeEnum.COMMENT_ARTICLE;
            } else if (type == 2) {
                m = MsgTypeEnum.COLLECT_ARTICLE;
            }
            {
                m = MsgTypeEnum.LIKE_ARTICLE;
            }

            Message message = new Message(msgId, user.getUserId(), receiverId, msgTitle, article.getArticleId().toString());
            boolean isSaved = messageService.save(message);
            if (!isSaved) {
                log.error("消息保存失败:{}", msgId);
            }
            redisTemplate.opsForZSet().add(RedisConstant.MESSAGE_BOX_PREFIX + receiverId, msgId + "", System.currentTimeMillis());
        });
    }

}




