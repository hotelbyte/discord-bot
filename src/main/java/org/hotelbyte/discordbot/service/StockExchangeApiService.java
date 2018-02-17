package org.hotelbyte.discordbot.service;

import lombok.extern.slf4j.Slf4j;
import org.hotelbyte.discordbot.enums.CacheEnum;
import org.hotelbyte.discordbot.model.stockexchange.ApiPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockExchangeApiService {
    @Autowired
    private RestTemplate rest;


    @Cacheable(value = CacheEnum.STOCKS_EXCHANGE_CACHE)
    public List<ApiPrice> getPriceByCoin(String coinName) {
        log.info("Calling stocks.exchange for {}...", coinName);
        ApiPrice[] prices = rest.getForObject("https://stocks.exchange/api2/prices", ApiPrice[].class);
        return getApiPrices(prices).stream().filter(price -> price.getCoinName().equals(coinName)).collect(Collectors.toList());
    }

    private List<ApiPrice> getApiPrices(ApiPrice[] prices) {
        return Arrays.stream(prices).filter(Objects::nonNull).filter(price -> price.getMarket_name() != null).map(price -> {
            String[] split = price.getMarket_name().split("_");
            if (split.length == 2) {
                price.setCoinName(split[0]);
                price.setPairName(split[1]);
            }
            return price;
        }).filter(price -> price.getCoinName() != null && price.getPairName() != null).collect(Collectors.toList());
    }
}
