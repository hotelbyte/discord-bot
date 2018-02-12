package org.hotelbyte.discordbot.service;

import org.hotelbyte.discordbot.model.openminingpool.ApiStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenMiningPoolApiService {

    @Autowired
    private RestTemplate rest;

    public ApiStats getStats() {
        return rest.getForObject("https://api.openminingpool.org/api/stats", ApiStats.class);
    }
}
