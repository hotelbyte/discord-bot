package org.hotelbyte.discordbot.model.openminingpool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiStats {
    private Long candidatesTotal;
    @JsonProperty("hashrate")
    private Long hashRate;
    private Long immatureTotal;
    private Long maturedTotal;
    private Long minersTotal;
    private List<ApiNode> nodes;
    private Long now;
    private ApiStatsDetail stats;
}
