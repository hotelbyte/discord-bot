package org.hotelbyte.discordbot.model.openminingpool;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiNode {
    private String difficulty;
    private String height;
    private String lastBeat;
    private String name;
}
