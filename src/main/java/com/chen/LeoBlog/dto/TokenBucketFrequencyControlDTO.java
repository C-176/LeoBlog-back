package com.chen.LeoBlog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBucketFrequencyControlDTO extends FrequencyControlDTO {

    /**
     * 令牌桶的容量
     */
    private int capacity;

    /**
     * 每次填充的令牌数量
     */
    private int refillTokens;

    /**
     * 填充周期的时间长度
     */
    private long refillPeriod;

    /**
     * 每次消耗的令牌数量
     */
    private int tokens;


}