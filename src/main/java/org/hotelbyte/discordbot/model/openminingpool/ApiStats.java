package org.hotelbyte.discordbot.model.openminingpool;

import lombok.Data;

import java.util.List;

@Data
public class ApiStats {
    private Long candidatesTotal;
    private Long hashrate;
    private Long immatureTotal;
    private Long maturedTotal;
    private Long minersTotal;
    private List<ApiNode> nodes;
    private Long now;
    private ApiStatsDetail stats;
}
