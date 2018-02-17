package org.hotelbyte.discordbot.enums;

import lombok.Getter;

@Getter
public enum CacheEnum {

    CRYPTO_COMPARE(-1, 500, -1),
    STOCKS_EXCHANGE(-1, 500, -1);

    public static final String STOCKS_EXCHANGE_CACHE = "STOCKS_EXCHANGE";
    public static final String CRYPTO_COMPARE_CACHE = "CRYPTO_COMPARE";

    private long readExpiration = -1;// By default without expiration
    private long writeExpiration = -1;// By default without expiration
    private long limit = -1;// By default without expiration

    CacheEnum() {

    }

    CacheEnum(long readExpiration, long writeExpiration, long limit) {
        this.readExpiration = readExpiration;
        this.writeExpiration = writeExpiration;
        this.limit = limit;
    }
}
