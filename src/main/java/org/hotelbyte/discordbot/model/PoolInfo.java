package org.hotelbyte.discordbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hotelbyte.discordbot.model.openminingpool.ApiStats;

@Data
@AllArgsConstructor
public class PoolInfo {
    private String description;
    private ApiStats stats;
    private String discordUser;

}
