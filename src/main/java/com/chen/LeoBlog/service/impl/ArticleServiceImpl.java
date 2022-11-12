package com.chen.LeoBlog.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.base.Local;
import com.chen.LeoBlog.base.ResultInfo;
import com.chen.LeoBlog.constant.RedisConstant;
import com.chen.LeoBlog.mapper.ArticleMapper;
import com.chen.LeoBlog.po.Article;
import com.chen.LeoBlog.po.Label;
import com.chen.LeoBlog.po.User;
import com.chen.LeoBlog.service.*;
import com.chen.LeoBlog.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 1
* @description 针对表【lb_article】的数据库操作Service实现
* @createDate 2022-10-14 17:35:27
*/
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService {
    @Autowired
    private UserService userService;
    @Resource
    private ArticleMapper articleMapper;
    @Autowired
    private IdUtil idUtil;
    @Resource
    private LabelService labelService;
    @Resource
    private CommentService commentService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SetArticleLabelService setArticleLabelService;

    @Override
    public ResultInfo getArticleList(Integer page, Integer size) {
        log.info("page: [{}], size: [{}]", page, size);
        Page<Article> pageObj = new Page<>(page, size);
        articleMapper.selectPage(pageObj,new QueryChainWrapper<>(articleMapper).eq("is_article",1).orderByDesc("article_update_date").getWrapper());
        return ResultInfo.success(pageObj);
    }

    @Override
    public ResultInfo getArticle(Long articleId) {
        log.info("articleId: [{}]", articleId);
        Article article = query().eq("article_id", articleId).one();
        if (article == null) {
            log.info("articleId: [{}] 不存在", articleId);
            return ResultInfo.fail("该文章不存在");
        }
        User user = userService.query().eq("user_id", article.getUserId()).one();
        ResultInfo labelList = labelService.getLabelList(articleId);

        Object labels = labelList.getData();
        //将article转为json字符串，并且加入user信息
        String json = JSONUtil.toJsonStr(article);
        HashMap<String,Object> map = JSONUtil.toBean(json, HashMap.class);
        map.put("user", JSONUtil.toJsonStr(user));
        map.put("labels", labels);
        return ResultInfo.success(map);
    }

    @Override
    public ResultInfo addArticle(Map<String, Object> map) {
        Article article = new Article();
        List<Label> labels = JSONUtil.toList(JSONUtil.toJsonStr(map.get("labels")), Label.class);
        List<Long> ids = labels.stream().map(Label::getLabelId).toList();
        article.setArticleId(idUtil.nextId("article"));
        article.setUserId(Local.getUser().getUserId());
        article.setArticleTitle(map.get("articleTitle").toString());
        article.setArticleContent(map.get("articleContent").toString());
        article.setArticleUpdateDate(new Date());
        article.setArticlePic(map.getOrDefault("articlePic","").toString());
        article.setIsArticle(Integer.parseInt( map.get("isArticle").toString()));
        setArticleLabelService.setLabelList(article.getArticleId(), ids);
        boolean isSuccess = save(article);
        if (!isSuccess) {
            return ResultInfo.fail("添加失败");
        }
        return ResultInfo.success(article.getArticleId());

    }

    @Override
    public ResultInfo deleteArticle(Long articleId) {
//        //只有作者才能删除，需要判断
//        UserDto user = Local.getUser();
//        if (user == null) return ResultInfo.fail("请先登录");
//        Long userId = user.getUserId();
//        System.out.println(Local.getUser());
        Article article = query().eq("article_id", articleId).one();
        if (article == null) {
            return ResultInfo.fail("该文章不存在，请刷新页面");
        }
//        if (article.getUserId() != userId) {
//            return ResultInfo.fail("权限不足");
//        }
        boolean isSuccess = update().eq("article_id", articleId).remove();
        commentService.deleteCommentByArticleId(articleId);
        if (!isSuccess) {
            return ResultInfo.fail("删除失败");
        }
        return ResultInfo.success("删除成功");
    }

    @Override
    @Transactional
    public ResultInfo updateArticle(Map<String, Object> map) {
        if(-1L == Long.parseLong(map.get("articleId").toString())){
            return addArticle(map);
        }
        List<Label> labels = JSONUtil.toList(JSONUtil.toJsonStr(map.get("labels")), Label.class);
        List<Long> ids = labels.stream().map(Label::getLabelId).toList();
        Article article = query().eq("article_id", map.get("articleId")).one();
        article.setArticleTitle(map.get("articleTitle").toString());
        article.setArticleContent(map.get("articleContent").toString());
        article.setArticleUpdateDate(new Date());
        article.setArticlePic(map.getOrDefault("articlePic","").toString());
        article.setIsArticle(Integer.parseInt( map.get("isArticle").toString()));
        setArticleLabelService.setLabelList(article.getArticleId(), ids);
        boolean isSuccess = updateById(article);
        if (!isSuccess) {
            return ResultInfo.fail("更新失败");
        }
        return ResultInfo.success(article.getArticleId());
    }

    @Override
    public ResultInfo getArticleListByUserId(Long userId, Integer page, Integer size) {
        Page<Article> articlePage = new Page<>(page, size);
        articleMapper.selectPage(articlePage, new QueryChainWrapper<>(articleMapper).eq("user_id", userId).eq("is_article", 1).orderByDesc("article_update_date").getWrapper());
        return ResultInfo.success(articlePage);
    }
    @Override
    public ResultInfo getArticlesListByUserId(Long userId, Integer page, Integer size) {
        Page<Article> articlePage = new Page<>(page, size);
        articleMapper.selectPage(articlePage, new QueryChainWrapper<>(articleMapper).eq("user_id", userId).eq("is_article", 0).orderByDesc("article_update_date").getWrapper());
        return ResultInfo.success(articlePage);
    }

    @Override
    public ResultInfo collectArticle(Long articleId) {
        String key = RedisConstant.ARTICLE_COLLECT + articleId;
        Long userId = query().eq("article_id", articleId).one().getUserId();
        if(Objects.equals(Local.getUser().getUserId(), userId)) {
            return ResultInfo.fail("不能收藏自己的文章");
        }
        Boolean isContain = redisTemplate.opsForSet().isMember(key, Local.getUser().getUserId().toString());
        if (isContain) {
            redisTemplate.opsForSet().remove(key, Local.getUser().getUserId().toString());
            update().eq("article_id", articleId).setSql("article_collects = article_collects - 1").update();
            return ResultInfo.success("取消收藏成功");
        }
        boolean isSuccess = false;
        try{
            redisTemplate.opsForSet().add(key, Local.getUser().getUserId().toString());
            isSuccess = update().eq("article_id", articleId).setSql("article_collects = article_collects + 1").update();

        }catch (Exception e) {
            log.error("收藏失败", e);
        }
        if (!isSuccess) {
            return ResultInfo.fail("收藏失败");
        }
        return ResultInfo.success("收藏成功");
    }

    @Override
    public ResultInfo getArticleListByContent(String content, Integer page, Integer size) {
        Page<Article> articlePage = new Page<>(page, size);
        articleMapper.selectPage(articlePage, new QueryChainWrapper<>(articleMapper).like("article_content", content).orderByDesc("article_update_date").getWrapper());
        return ResultInfo.success(articlePage);
    }

    @Override
    public ResultInfo getArticleSumByUserId(Long userId) {
        Integer articleSize = query().eq("user_id", userId).eq("is_article",1).count();
        Integer scriptSize = query().eq("user_id", userId).eq("is_article",0).count();
        HashMap<String, Object> map = new HashMap<>();
        map.put("articleSize", articleSize);
        map.put("scriptSize", scriptSize);
        map.put("commentSize", commentService.query().eq("user_id", userId).eq("comment_parent_id",-1).count());
        return ResultInfo.success(map);
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
    public ResultInfo getArticleListByLabelId(Long labelId,Map<String,Object> map, Integer page, Integer size) {
        System.out.println(map.get("ids"));
        System.out.println(map);
        String[] ids = map.get("ids").toString().substring(1, map.get("ids").toString().length() - 1).split(", ");
        List<Long> longs = Arrays.stream(ids).toList().stream().map(Long::parseLong).collect(Collectors.toList());
        Page<Article> articlePage = new Page<>(page, size);
        List<Long> articles = new ArrayList<>();
        setArticleLabelService.query().eq("label_id", labelId).list().forEach(setArticleLabel -> {
            if(longs.contains(setArticleLabel.getArticleId())){
                articles.add(setArticleLabel.getArticleId());
            }
        });
        articleMapper.selectPage(articlePage, new QueryChainWrapper<>(articleMapper).in("article_id", articles.toString().substring(1,articles.toString().length()-1)).orderByDesc("article_update_date").getWrapper());
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
        Long userId = query().eq("article_id", articleId).one().getUserId();
        if(Objects.equals(Local.getUser().getUserId(), userId)) {
            return ResultInfo.fail("不能给自己的文章点赞");
        }
        Boolean isContain = redisTemplate.opsForSet().isMember(key, Local.getUser().getUserId().toString());
        if (isContain) {
            redisTemplate.opsForSet().remove(key, Local.getUser().getUserId().toString());
            update().eq("article_id", articleId).setSql("article_likes = article_likes - 1").update();
            return ResultInfo.success("取消点赞成功");
        }
        boolean isSuccess = false;
        try{
            redisTemplate.opsForSet().add(key, Local.getUser().getUserId().toString());
            isSuccess = update().eq("article_id", articleId).setSql("article_likes = article_likes + 1").update();

        }catch (Exception e) {
            log.error("点赞失败", e);
        }
        if (!isSuccess) {
            return ResultInfo.fail("点赞失败");
        }
        return ResultInfo.success("点赞成功");
    }

}




