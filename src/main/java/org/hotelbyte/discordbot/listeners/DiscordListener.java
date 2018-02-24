package org.hotelbyte.discordbot.listeners;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.hotelbyte.discordbot.model.openminingpool.ApiStats;
import org.hotelbyte.discordbot.model.stockexchange.ApiPrice;
import org.hotelbyte.discordbot.service.CryptoCompareApiService;
import org.hotelbyte.discordbot.service.OpenMiningPoolApiService;
import org.hotelbyte.discordbot.service.StockExchangeApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class DiscordListener extends ListenerAdapter {
    public static final String TOKEN_NAME = "HBF";
    public static final String HELP = "!help";
    public static final String POOLS = "!pools";
    public static final String POOLS_ALT = "!pool";
    public static final String EXCHANGES = "!exchanges";
    public static final String EXCHANGES_ALT = "!exchange";
    public static final String TWITTER = "!twitter";
    public static final String WALLET = "!wallet";
    public static final String WEBSITE = "!website";
    public static final String SUPPLY = "!supply";
    public static final String MASTER_NODE = "!masternodes";
    public static final String MASTER_NODE_ALT = "!masternode";

    @Autowired
    private OpenMiningPoolApiService openMiningPoolApiService;
    @Autowired
    private StockExchangeApiService stockExchangeApiService;
    @Autowired
    private CryptoCompareApiService cryptoCompareApiService;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String message = event.getMessage().getContentRaw().toLowerCase();
        MessageBuilder response = new MessageBuilder();
        switch (message) {
            case HELP:
                fillHelp(response);
                break;
            case EXCHANGES:
            case EXCHANGES_ALT:
                fillExchanges(response);
                break;
            case POOLS:
            case POOLS_ALT:
                fillPools(response, event);
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
            case MASTER_NODE:
            case MASTER_NODE_ALT:
                fillMasterNode(response);
                break;
            default:
                //None
        }
        if (response.isEmpty() && (message.contains("what") || message.contains("?")) && message.contains("supply")) {
            fillSupply(response);
        }
        if (!response.isEmpty()) {
            event.getChannel().sendMessage(response.build()).queue();
        }
    }

    private void fillSupply(MessageBuilder response) {
        long currentBlock = currentBlock();
        if (currentBlock > 0) {
            long totalSupply = 11 * currentBlock;
            response.append("Total supply for now are ([the reward block 9] + [the rewards for the dev and master node 2])* [total numbers of blocks]. ");
            response.append("**Current total supply are ").append(totalSupply).append(" HotelCoin's aprox**. ").append("Like Ethereum do not exist an max limit.");
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

    private void fillWallet(MessageBuilder response) {
        response.append("**Download it!** https://github.com/hotelbyte/distributed-hotel-interface/releases");
    }

    private void fillMasterNode(MessageBuilder response) {
        response.append("Please see our roadmap the masternodes comes on Q3/Q4 of this year, in these dates we going to show you the minimums.");
    }

    private void fillTwitter(MessageBuilder response) {
        response.append("**Follow us!**  https://twitter.com/hotelbyte");
    }

    private void fillWebsite(MessageBuilder response) {
        response.append("https://hotelbyte.org");
    }

    private void fillPools(MessageBuilder response, MessageReceivedEvent event) {

        response.append("List of all known " + TOKEN_NAME + " Mining Pools:\n");

        response.append("\thttps://hbc.openminingpool.org (Official ");
        addMention(response, event, "hotelbyte");
        response.append(")\n");

        response.append("\thttp://hotelbyte.minerpool.net ");
        addMention(response, event, "CHRlS - MINERPOOL.NET");
        response.append("\n");

        response.append("\thttps://hbc.luckypool.io ");
        addMention(response, event, "SB155 (luckypool.io)");
        response.append("\n");

        response.append("\thttp://comining.io ");
        addMention(response, event, "Rom1kz");
        response.append("\n");

        response.append("\thttp://hbc.cryptopool.network ");
        addMention(response, event, "CryptoPool.Network");
        response.append("\n");

        response.append("\thttps://aikapool.com/hbf/index.php\n");
        response.append("\thttp://solo-hbc.2zo.pw\n");
    }


    private void fillExchanges(MessageBuilder response) {
        response.append("List of all " + TOKEN_NAME + " Exchanges:\n");
        stocksExchange(response);
    }

    private void stocksExchange(MessageBuilder response) {
        List<ApiPrice> prices = stockExchangeApiService.getPriceByCoin(TOKEN_NAME);
        response.append("\thttps://stocks.exchange ");
        BigDecimal minValue = null;
        for (ApiPrice price : prices) {
            BigDecimal priceUSD = price.getBuy().multiply(cryptoCompareApiService.getPriceUSD(price.getPairName()));
            if (minValue == null || priceUSD.compareTo(minValue) < 0) {
                minValue = priceUSD;
            }
        }
        if (minValue != null) {
            response.append("$").append(minValue).append(" ");
        }
        for (ApiPrice price : prices) {
            response.append("[").append(price.getPairName()).append(" MaxBuy=")
                    .append(price.getBuy()).append(" ").append(" MinSell=").append(price.getSell()).append("]");
        }
        response.append("\n");
    }

    private void fillHelp(MessageBuilder response) {
        response.append("Available commands:\n");
        addHelpOption(response, POOLS, null);
        addHelpOption(response, EXCHANGES, null);
        addHelpOption(response, TWITTER, null);
        addHelpOption(response, WALLET, null);
        addHelpOption(response, WEBSITE, null);
        addHelpOption(response, SUPPLY, null);
        addHelpOption(response, MASTER_NODE, null);
    }

    private void addHelpOption(MessageBuilder response, String command, String description) {
        response.append('\t');
        response.append(command);
        if (!StringUtils.isEmpty(description)) {
            response.append(": ").append(description);
        }
        response.append('\n');
    }


    private static void addMention(MessageBuilder response, MessageReceivedEvent event, String username) {
        User user = getUserByName(event, username);
        if (user != null) {
            response.append(user);
        } else {
            response.append("@").append(username);
        }
    }

    private static User getUserByName(MessageReceivedEvent event, String name) {
        User user = null;
        if (event != null && event.getGuild() != null) {
            List<Member> result = event.getGuild().getMembersByName(name, true);
            if (result == null || result.isEmpty()) {
                result = event.getGuild().getMembersByEffectiveName(name, true);
                if (result == null || result.isEmpty()) {
                    result = event.getGuild().getMembersByNickname(name, true);
                }
            }
            if (result != null && !result.isEmpty()) {
                Member member = result.get(0);
                if (member != null && member.getUser() != null) {
                    return member.getUser();
                }
            }
        }
        return user;
    }
}
