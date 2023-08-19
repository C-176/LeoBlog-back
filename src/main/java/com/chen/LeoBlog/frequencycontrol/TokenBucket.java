package com.chen.LeoBlog.frequencycontrol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class TokenBucket {
    private int capacity;
    private int tokens;
    private Duration refillPeriod;
    private Instant lastRefillTime;
    private int refillTokens;

    public TokenBucket(int capacity, int refillTokens, Duration refillPeriod) {
        this.capacity = capacity;
        this.tokens = capacity;
        this.refillPeriod = Duration.ofMillis(0);
        this.lastRefillTime = Instant.now();
        this.refillTokens = refillTokens;
    }


    public synchronized boolean isSuffient(int tokensToConsume, Instant now) {
        refillTokens(now);
        return tokensToConsume <= tokens;
    }

    public synchronized void addTokens(int tokensToAdd, Instant now) {
        refillTokens(now);
        tokens = Math.min(capacity, tokens + tokensToAdd);
    }

    /**
     * 消耗令牌, 如果令牌不足，返回false
     *
     * @param tokensToConsume
     * @param now
     */
    public synchronized boolean consume(int tokensToConsume, Instant now) {
        refillTokens(now);
        tokens -= tokensToConsume;
        if (tokens < 0) tokens = -1;
        return tokens >= 0;
    }

    /**
     * 计算从上次填充到现在的时间间隔，如果时间间隔大于等于填充周期，就需要填充令牌
     *
     * @param now
     */
    private void refillTokens(Instant now) {
        // 计算从上次填充到现在的时间间隔
        Duration timeElapsed = Duration.between(lastRefillTime, now);
        // 计算从上次填充到现在的周期数
        long numRefills = timeElapsed.toMillis() / refillPeriod.toMillis();
        if (numRefills > 0) { // 如果周期数大于0，说明需要填充令牌
            tokens = Math.min(capacity, tokens + refillTokens);
            lastRefillTime = lastRefillTime.plusMillis(numRefills * refillPeriod.toMillis());
        }
        if (tokens < 0) tokens = -1;
        System.out.println(tokens);
    }
}