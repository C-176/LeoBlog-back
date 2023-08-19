package com.chen.LeoBlog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.LeoBlog.mapper.BlackMapper;
import com.chen.LeoBlog.po.Black;
import com.chen.LeoBlog.service.BlackService;
import org.springframework.stereotype.Service;

/**
 * @author rtg19
 * @description 针对表【lb_black(黑名单)】的数据库操作Service实现
 * @createDate 2023-07-21 15:04:59
 */
@Service
public class BlackServiceImpl extends ServiceImpl<BlackMapper, Black>
        implements BlackService {

}




