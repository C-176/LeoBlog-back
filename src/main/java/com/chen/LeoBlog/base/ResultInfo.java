package com.chen.LeoBlog.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.LeoBlog.enums.ErrorEnum;
import com.chen.LeoBlog.vo.response.CursorPageBaseResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultInfo<T> {
    private Integer code = 200;
    private String msg = "success";
    private T data;


    public static ResultInfo<String> success() {
        return new ResultInfo<>(200, "success", null);
    }

    public static <T> ResultInfo<T> success(T data) {
        return new ResultInfo<>(200, "success", data);
    }

    public static ResultInfo<String> success(String data) {
        return new ResultInfo<>(200, "success", data);
    }

    public static ResultInfo<Map<String, Object>> success(Page<?> page) {
        Map<String, Object> map = new HashMap<>();
        map.put("total", page.getTotal());
        map.put("pages", page.getPages());
        map.put("size", page.getSize());
        map.put("current", page.getCurrent());
        map.put("records", page.getRecords());
        return new ResultInfo<>(200, "success", map);
    }

    public static ResultInfo<List<?>> success(List<?> data) {
        return new ResultInfo<>(200, "success", data);
    }

    public static ResultInfo<String> fail(String data) {
        return new ResultInfo<>(300, "error", data);
    }

    public static ResultInfo<String> fail() {
        return new ResultInfo<>(300, "error", null);
    }

    public static ResultInfo<String> fail(Integer code, String data) {
        return new ResultInfo<>(code, "error", data);
    }

    public static ResultInfo<String> fail(ErrorEnum errorEnum) {
        return new ResultInfo<>(errorEnum.getErrorCode(), errorEnum.getErrorMsg(), null);
    }

    public static ResultInfo<?> success(CursorPageBaseResp<?> cursorPageBaseResp) {
        return new ResultInfo<>(200, "success", cursorPageBaseResp);
    }
}
