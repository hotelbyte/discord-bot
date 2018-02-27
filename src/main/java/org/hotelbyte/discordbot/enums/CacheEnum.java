package org.hotelbyte.discordbot.enums;

import lombok.Getter;

@Getter
public enum CacheEnum {

    CRYPTO_COMPARE(-1, 3600, -1),
    STOCKS_EXCHANGE,//Never update, used when stocks fails
    STOCKS_EXCHANGE_NEAR(-1, 120, -1);//Update stocks value

    public static final String STOCKS_EXCHANGE_CACHE = "STOCKS_EXCHANGE";
    public static final String STOCKS_EXCHANGE_NEAR_CACHE = "STOCKS_EXCHANGE_NEAR";
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
