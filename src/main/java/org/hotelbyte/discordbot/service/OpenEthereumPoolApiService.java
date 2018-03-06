package org.hotelbyte.discordbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hotelbyte.discordbot.model.openminingpool.ApiStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hotelbyte.discordbot.enums.CacheEnum.OPEN_ETHEREUM_POOL_CACHE;

@Service
@Slf4j
public class OpenEthereumPoolApiService {
    public static final String OFFICIAL = "https://api.openminingpool.org";
    public static final String MINER_POOL = "http://hotelbyte.minerpool.net";
    public static final String LUCKY_POOL = "https://hbc.luckypool.io/api";
    public static final String CRYPTO_POOL = "https://hbc.cryptopool.network";
    public static final String TWOZO_PW = "https://hbf.2zo.pw";

    @Autowired
    private RestTemplate rest;
    @Autowired
    private ObjectMapper mapper;

    @Cacheable(value = OPEN_ETHEREUM_POOL_CACHE, unless = "#result == null || #result.getHashRate() == null")
    public ApiStats getPoolStats(String url) {
        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 Firefox/26.0");
            return rest.exchange(url + "/api/stats", HttpMethod.GET, new HttpEntity<String>(headers), ApiStats.class).getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error obtaining hashRate for {}: {} -> {}", url, e.getMessage(), e.getResponseBodyAsString());
        }
        return new ApiStats();
    }


}