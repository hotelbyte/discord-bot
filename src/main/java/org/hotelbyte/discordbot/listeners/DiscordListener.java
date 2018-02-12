package org.hotelbyte.discordbot.listeners;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class DiscordListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String message = event.getMessage().getContentRaw();
        String response = null;
        switch (message) {
            case "!help":
                response = "Commands:\n";
                response += "\t!pools\n";
                response += "\t!exchanges\n";
                response += "\t!twitter\n";
                response += "\t!wallet\n";
                response += "\t!website\n";
                response += "\t!supply";
                break;
            case "!exchanges":
                response = "Coming Soon";
                break;
            case "!pools":
                response = "List of all known Mining Pools:\n";
                response += "\thttps://hbc.openminingpool.org (Official)\n";
                response += "\thttp://hotelbyte.minerpool.net\n";
                response += "\thttps://hbc.luckypool.io\n";
                response += "\thttp://comining.io";
                break;
            case "!website":
                response = "https://hotelbyte.org";
                break;
            case "!twitter":
                response = "Follow us!  https://twitter.com/hotelbyte";
                break;
            case "!wallet":
                response = "Download it! https://github.com/hotelbyte/distribution-hotel-interface/releases";
                break;
            case "!supply":
                response = "Total supply for now are ([the reward block 9] + [the rewards for the dev and master node 2])* [total numbers of blocks]. Like Ethereum do not exist an max limit.";
                break;
            default:
                //None
        }
        if (response != null) {
            event.getChannel().sendMessage(response).queue();
        }

    }
}
