package org.hotelbyte.discordbot.service;

import lombok.extern.slf4j.Slf4j;
import org.hotelbyte.discordbot.enums.CacheEnum;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@Slf4j
public class CryptoCompareApiService {

    @Autowired
    private RestTemplate rest;

    @Cacheable(value = CacheEnum.CRYPTO_COMPARE_CACHE, unless = "#result == null ")
    public BigDecimal getPriceUSD(String currency) {
        log.debug("Calling cryptocompare for {}...", currency);
        String response = rest.getForObject("https://min-api.cryptocompare.com/data/pricehistorical?fsym=" +
                currency + "&tsyms=USD", String.class);
        return new JSONObject(response).getJSONObject(currency).getBigDecimal("USD");
    }
}
