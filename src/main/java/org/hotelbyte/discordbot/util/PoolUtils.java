package org.hotelbyte.discordbot.util;

import java.math.BigDecimal;

public class PoolUtils {
    private static final String[] HASH_UNIT = {"H", "KH", "MH", "GH", "TH", "EH", "ZH", "YH"};
    public static final BigDecimal KILO_BIT = BigDecimal.valueOf(1000);

    public static String getHashRate(Long statsHashRate) {
        int unitIndex = 0;
        BigDecimal hashRate = BigDecimal.ZERO;
        if (statsHashRate != null) {
            hashRate = BigDecimal.valueOf(statsHashRate);
            while (hashRate.compareTo(KILO_BIT) > 0) {
                hashRate = hashRate.divide(KILO_BIT, BigDecimal.ROUND_HALF_DOWN).setScale(2);
                unitIndex++;
            }
        }
        return hashRate + " " + HASH_UNIT[unitIndex] + "/s";
    }
}
