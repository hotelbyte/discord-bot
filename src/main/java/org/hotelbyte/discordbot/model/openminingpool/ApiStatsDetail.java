package org.hotelbyte.discordbot.model.openminingpool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiStatsDetail {
    private Long lastBlockFound;
    private Long nShares;
    private Long roundShares;
}
