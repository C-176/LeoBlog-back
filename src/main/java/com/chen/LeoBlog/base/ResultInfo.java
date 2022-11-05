package com.chen.LeoBlog.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultInfo {
    private Integer code = 200;
    private String msg = "success";
    private Object data;


    public static ResultInfo success(){
        return new ResultInfo(200, "success", null);
    }
    public static ResultInfo success(Object data){
        return new ResultInfo(200, "success", data);
    }
    public static ResultInfo success(String data){
        return new ResultInfo(200, "success", data);
    }
    public static ResultInfo success(Page<?> page){
        Map<String,Object> map = new HashMap<>();
        map.put("total", page.getTotal());
        map.put("pages", page.getPages());
        map.put("size", page.getSize());
        map.put("current", page.getCurrent());
        map.put("records", page.getRecords());
        return new ResultInfo(200, "success", map);
    }
    public static ResultInfo success(List<?> data){
        return new ResultInfo(200, "success", data);
    }
    public static ResultInfo fail(String data){
        return new ResultInfo(300, "error", data);
    }
    public static ResultInfo fail(){
        return new ResultInfo(300,"error", null);
    }
    public static ResultInfo fail(Integer code,String data){
        return new ResultInfo(code, "error", data);
    }
}
