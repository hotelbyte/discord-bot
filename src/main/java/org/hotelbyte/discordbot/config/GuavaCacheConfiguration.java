package org.hotelbyte.discordbot.config;

import com.google.common.cache.CacheBuilder;
import org.hotelbyte.discordbot.enums.CacheEnum;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class GuavaCacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<GuavaCache> caches = new ArrayList<GuavaCache>();
        for (CacheEnum cache : CacheEnum.values()) {
            CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
            if (cache.getReadExpiration() > 0) {
                builder.expireAfterAccess(cache.getReadExpiration(), TimeUnit.SECONDS);
            }
            if (cache.getWriteExpiration() > 0) {
                builder.expireAfterWrite(cache.getWriteExpiration(), TimeUnit.SECONDS);
            }
            if (cache.getLimit() > 0) {
                builder.maximumSize(cache.getLimit());
            }
            GuavaCache guavaCache = new GuavaCache(cache.name(), builder.build());
            caches.add(guavaCache);
        }
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
