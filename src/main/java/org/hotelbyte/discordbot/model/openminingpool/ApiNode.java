package org.hotelbyte.discordbot.model.openminingpool;

import lombok.Data;

@Data
public class ApiNode {
    private String difficulty;
    private String height;
    private String lastBeat;
    private String name;
}
