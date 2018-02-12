package org.hotelbyte.discordbot.listeners;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.hotelbyte.discordbot.model.openminingpool.ApiStats;
import org.hotelbyte.discordbot.service.OpenMiningPoolApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class DiscordListener extends ListenerAdapter {

    public static final String HELP = "!help";
    public static final String POOLS = "!pools";
    public static final String EXCHANGES = "!exchanges";
    public static final String TWITTER = "!twitter";
    public static final String WALLET = "!wallet";
    public static final String WEBSITE = "!website";
    public static final String SUPPLY = "!supply";

    @Autowired
    private OpenMiningPoolApiService openMiningPoolApiService;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String message = event.getMessage().getContentRaw().toLowerCase();
        StringBuilder response = new StringBuilder();
        switch (message) {
            case HELP:
                fillHelp(response);
                break;
            case EXCHANGES:
                fillExchanges(response);
                break;
            case POOLS:
                fillPools(response);
                break;
            case WEBSITE:
                fillWebsite(response);
                break;
            case TWITTER:
                fillTwitter(response);
                break;
            case WALLET:
                fillWallet(response);
                break;
            case SUPPLY:
                fillSupply(response);
                break;
            default:
                //None
        }
        if (response.length() == 0 && (message.contains("what") || message.contains("?")) && message.contains("supply")) {
            fillSupply(response);
        }
        if (response.length() > 0) {
            event.getChannel().sendMessage(response).queue();
        }
    }

    private void fillSupply(StringBuilder response) {
        long currentBlock = currentBlock();
        if (currentBlock > 0) {
            long totalSupply = 11 * currentBlock;
            response.append("Total supply for now are ([the reward block 9] + [the rewards for the dev and master node 2])* [total numbers of blocks]. ");
            response.append("**Current total supply are ").append(totalSupply).append(" HotelCoin's**. ").append("Like Ethereum do not exist an max limit.");
        } else {
            response.append("Total supply for now are ([the reward block 9] + [the rewards for the dev and master node 2])* [total numbers of blocks]. " +
                    "Like Ethereum do not exist an max limit.");
        }
    }

    private long currentBlock() {
        try {
            ApiStats stats = openMiningPoolApiService.getStats();
            if (stats != null && stats.getNodes() != null && !stats.getNodes().isEmpty()) {
                String height = stats.getNodes().get(0).getHeight();
                return Long.parseLong(height);
            }
        } catch (Exception e) {
            log.warn("Error when obtain last block", e);
        }
        return -1;
    }

    private void fillWallet(StringBuilder response) {
        response.append("**Download it!** https://github.com/hotelbyte/distribution-hotel-interface/releases");
    }

    private void fillTwitter(StringBuilder response) {
        response.append("**Follow us!**  https://twitter.com/hotelbyte");
    }

    private void fillWebsite(StringBuilder response) {
        response.append("https://hotelbyte.org");
    }

    private void fillPools(StringBuilder response) {
        response.append("List of all known Mining Pools:\n");
        response.append("\thttps://hbc.openminingpool.org (Official)\n");
        response.append("\thttp://hotelbyte.minerpool.net\n");
        response.append("\thttps://hbc.luckypool.io\n");
        response.append("\thttp://comining.io");
    }

    private void fillExchanges(StringBuilder response) {
        response.append("Coming Soon");
    }

    private void fillHelp(StringBuilder response) {
        response.append("Possible commands are:\n");
        addHelpOption(response, POOLS, null);
        addHelpOption(response, EXCHANGES, null);
        addHelpOption(response, TWITTER, null);
        addHelpOption(response, WALLET, null);
        addHelpOption(response, WEBSITE, null);
        addHelpOption(response, SUPPLY, null);
    }

    private void addHelpOption(StringBuilder response, String command, String description) {
        response.append('\t');
        response.append(command);
        if (!StringUtils.isEmpty(description)) {
            response.append(": ").append(description);
        }
        response.append('\n');
    }
}
