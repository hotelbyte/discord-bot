package org.hotelbyte.discordbot.service;

import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.hotelbyte.discordbot.model.stockexchange.ApiPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hotelbyte.discordbot.enums.CacheEnum.STOCKS_EXCHANGE_CACHE;
import static org.hotelbyte.discordbot.enums.CacheEnum.STOCKS_EXCHANGE_NEAR_CACHE;

@Service
@Slf4j
public class StockExchangeApiService {
    @Autowired
    private RestTemplate rest;
    @Autowired
    private CacheManager cacheManager;

    @Cacheable(value = STOCKS_EXCHANGE_NEAR_CACHE, unless = "#result == null || #result.isEmpty()")
    @Retryable(value = {HttpClientErrorException.class}, backoff = @Backoff(delay = 1000))
    public List<ApiPrice> getPriceByCoin(String coinName) {
        GuavaCache guavaCache = (GuavaCache) cacheManager.getCache(STOCKS_EXCHANGE_CACHE);
        Cache<Object, Object> cache = guavaCache.getNativeCache();
        try {
            List<ApiPrice> prices = updatePriceByCoin(coinName);
            cache.asMap().put(coinName, prices);
        } catch (HttpClientErrorException e) {
            log.error("Error obtaining priceByCoin: {}", e.getMessage());
        }
        return (List<ApiPrice>) cache.asMap().get(coinName);
    }

    private List<ApiPrice> updatePriceByCoin(String coinName) {
        log.debug("Calling stocks.exchange for {}...", coinName);
        ApiPrice[] prices = rest.getForObject("https://stocks.exchange/api2/prices", ApiPrice[].class);
        return Arrays.stream(prices).filter(Objects::nonNull).filter(price -> price.getMarket_name() != null).map(price -> {
            String[] split = price.getMarket_name().split("_");
            if (split.length == 2) {
                price.setCoinName(split[0]);
                price.setPairName(split[1]);
            }
            return price;
        }).filter(price -> price.getCoinName() != null && price.getPairName() != null).filter(price -> price.getCoinName().equals(coinName)).collect(Collectors.toList());
    }


}
