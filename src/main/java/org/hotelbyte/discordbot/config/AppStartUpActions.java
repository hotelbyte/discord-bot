package org.hotelbyte.discordbot.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class AppStartUpActions {

    // Any startup sync action
    @PostConstruct
    public void startUpActionsSync() {
        // Start async tasks thread
        StartUpActionsAsync startActions = new StartUpActionsAsync();
        startActions.start();
    }

    @AllArgsConstructor
    private class StartUpActionsAsync extends Thread {

        @Override
        public void run() {
            // Any startup async action
        }

    }
}
