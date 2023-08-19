package com.chen.LeoBlog.frequencycontrol;

import com.chen.LeoBlog.dto.TokenBucketFrequencyControlDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TokenBucketFrequencyControlService extends AbstractFrequencyControlService<TokenBucketFrequencyControlDTO> {

    private final Map<String, TokenBucket> tokenBuckets = new ConcurrentHashMap<>();

    @Override
    protected boolean reachRateLimit(Map<String, TokenBucketFrequencyControlDTO> frequencyControlMap) {
        Instant now = Instant.now();
        for (Map.Entry<String, TokenBucketFrequencyControlDTO> entry : frequencyControlMap.entrySet()) {
            String key = entry.getKey();
            TokenBucketFrequencyControlDTO frequencyControlDTO = entry.getValue();
            TokenBucket tokenBucket = tokenBuckets.computeIfAbsent(key, k -> createTokenBucket(frequencyControlDTO));
            int tokensToConsume = frequencyControlDTO.getTokens();
            if (!tokenBucket.consume(tokensToConsume, now)) {
                return true; // 频控限制
            }
        }
        return false;
    }

    @Override
    /**
     * 添加限流统计次数，实际上就是消耗令牌
     */
    protected void addFrequencyControlStatisticsCount(Map<String, TokenBucketFrequencyControlDTO> frequencyControlMap) {
        // 在这里不需要消耗令牌，因为在reachRateLimit()方法中已经消耗了令牌
//        Instant now = Instant.now();
//        for (Map.Entry<String, TokenBucketFrequencyControlDTO> entry : frequencyControlMap.entrySet()) {
//            String key = entry.getKey();
//            TokenBucketFrequencyControlDTO frequencyControlDTO = entry.getValue();
//            TokenBucket tokenBucket = tokenBuckets.computeIfAbsent(key, k -> createTokenBucket(frequencyControlDTO));
//            int tokensToConsume = frequencyControlDTO.getTokens();
//            tokenBucket.consume(tokensToConsume, now);
//        }
    }

    @Override
    protected String getStrategyName() {
        return FrequencyControlStrategyFactory.TOKEN_BUCKET_FREQUENCY_CONTROLLER;
    }

    private TokenBucket createTokenBucket(TokenBucketFrequencyControlDTO frequencyControlDTO) {
        return TokenBucket.builder()
                .capacity(frequencyControlDTO.getCapacity())
                .tokens(frequencyControlDTO.getCapacity())
                .refillPeriod(Duration.ofMillis(frequencyControlDTO.getRefillPeriod()))
                .lastRefillTime(Instant.now())
                .refillTokens(frequencyControlDTO.getRefillTokens())
                .build();
    }
}