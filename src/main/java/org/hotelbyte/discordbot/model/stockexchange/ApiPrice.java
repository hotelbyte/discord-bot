package org.hotelbyte.discordbot.model.stockexchange;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApiPrice {
    private BigDecimal buy;
    private BigDecimal sell;
    private String market_name;
    private String coinName;
    private String pairName;
    private Long updated_time;
    private Long Server_time;

}
