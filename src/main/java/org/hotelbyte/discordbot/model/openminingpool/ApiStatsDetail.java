package org.hotelbyte.discordbot.model.openminingpool;

import lombok.Data;

@Data
public class ApiStatsDetail {
    private Long lastBlockFound;
    private Long nShares;
    private Long roundShares;
}
