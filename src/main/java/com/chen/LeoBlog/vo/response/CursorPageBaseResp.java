package com.chen.LeoBlog.vo.response;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@Data
@ApiModel("游标翻页返回")
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseResp<T> {

    @ApiModelProperty("游标（下次翻页带上这参数）")
    private String cursor;

    @ApiModelProperty("是否最后一页")
    private Boolean isLast = Boolean.FALSE;
    @ApiModelProperty("偏移量")
    private Integer offset = 0;

    @ApiModelProperty("数据列表")
    private List<T> list;

    public static <T> CursorPageBaseResp<T> init(CursorPageBaseResp cursorPage, List<T> list) {
        CursorPageBaseResp<T> cursorPageBaseResp = new CursorPageBaseResp<>();
        cursorPageBaseResp.setIsLast(cursorPage.getIsLast());
        cursorPageBaseResp.setList(list);
        cursorPageBaseResp.setCursor(cursorPage.getCursor());
        cursorPageBaseResp.setOffset(cursorPage.getOffset());
        return cursorPageBaseResp;
    }

    @JsonIgnore
    public static Boolean isEmpty(List<?> list) {
        return CollectionUtil.isEmpty(list);
    }

    public static Boolean isLast(List<?> list, Integer pageSize) {
        return CollectionUtil.isEmpty(list) || list.size() < pageSize;
    }

    public static <T> CursorPageBaseResp<T> empty() {
        CursorPageBaseResp<T> cursorPageBaseResp = new CursorPageBaseResp<>();
        cursorPageBaseResp.setIsLast(true);
        cursorPageBaseResp.setList(new ArrayList<>());
        return cursorPageBaseResp;
    }

    public static <T> CursorPageBaseResp<T> of(String cursor, Integer offset, List<T> list, Integer pageSize) {
        return new CursorPageBaseResp<>(cursor, isLast(list, pageSize), offset, list);

    }


}
