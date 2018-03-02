package org.hotelbyte.discordbot.listeners;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.hotelbyte.discordbot.model.PoolInfo;
import org.hotelbyte.discordbot.model.openminingpool.ApiStats;
import org.hotelbyte.discordbot.model.stockexchange.ApiPrice;
import org.hotelbyte.discordbot.service.CryptoCompareApiService;
import org.hotelbyte.discordbot.service.OpenEthereumPoolApiService;
import org.hotelbyte.discordbot.service.ScrapService;
import org.hotelbyte.discordbot.service.StockExchangeApiService;
import org.hotelbyte.discordbot.util.PoolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static org.hotelbyte.discordbot.service.OpenEthereumPoolApiService.*;

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
    public static final String CLEAR = "!clear";

    @Autowired
    private OpenEthereumPoolApiService openMiningPoolApiService;
    @Autowired
    private StockExchangeApiService stockExchangeApiService;
    @Autowired
    private CryptoCompareApiService cryptoCompareApiService;
    @Autowired
    private ScrapService scrapService;
    private ExecutorService executor = Executors.newFixedThreadPool(100);

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
                fillExchanges(event, response);
                break;
            case POOLS:
            case POOLS_ALT:
                fillPools(event, response);
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
            case CLEAR:
                clear(event);
                break;
            default:
                //None
        }
        if (response.isEmpty() && (message.contains("what") || message.contains("?")) && message.contains("supply")) {
            fillSupply(response);
            event.getChannel().sendMessage(response.build()).queue();
        } else if (!response.isEmpty()) {
            if (!isAdmin(event) && !event.getChannel().getName().equals("bot")) {
                response = new MessageBuilder();
                List<TextChannel> channels = event.getGuild().getTextChannelsByName("bot", true);
                response.append(event.getAuthor()).append(" please go to ");
                if (channels != null && !channels.isEmpty()) {
                    response.append(channels.get(0));
                } else {
                    response.append("#bot");
                }
                response.append(" channel to use me.");
                event.getChannel().sendMessage(response.build()).queue();
            } else {
                event.getChannel().sendMessage(response.build()).queue();
            }
        }
    }

    private void clear(MessageReceivedEvent event) {
        if (isAdmin(event)) {
            event.getChannel().deleteMessageById(event.getMessage().getId()).queue();
            for (Message message : event.getChannel().getIterableHistory()) {
                if (!message.isPinned() && message.getAuthor().isBot() && message.getCreationTime() != null) {
                    OffsetDateTime creationTime = message.getCreationTime();
                    OffsetDateTime now = OffsetDateTime.now();
                    log.info("Message deleted: {}", message.getContentRaw());
                    event.getChannel().deleteMessageById(message.getId()).queue();
                }
            }
        } else {
            MessageBuilder response = new MessageBuilder();
            response.append(event.getAuthor()).append(", this command is only available for admins.");
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
            ApiStats stats = openMiningPoolApiService.getPoolStats(OFFICIAL);
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

    private void fillPools(MessageReceivedEvent event, MessageBuilder response) {
        response.append("List of all known " + TOKEN_NAME + " Mining Pools:\n");
        List<Future<PoolInfo>> futures = new ArrayList<>();
        futures.add(executor.submit(poolInfoCallable("https://hbc.openminingpool.org **OFFICIAL**", () -> openMiningPoolApiService.getPoolStats(OFFICIAL), "hotelbyte")));
        futures.add(executor.submit(poolInfoCallable("http://hotelbyte.minerpool.net", () -> openMiningPoolApiService.getPoolStats(MINER_POOL), "CHRlS - MINERPOOL.NET")));
        futures.add(executor.submit(poolInfoCallable("https://hbc.luckypool.io", () -> openMiningPoolApiService.getPoolStats(LUCKY_POOL), "SB155 (luckypool.io)")));
        futures.add(executor.submit(poolInfoCallable("http://comining.io", () -> scrapService.getCominingIoPoolStats(), "Rom1kz")));
        futures.add(executor.submit(poolInfoCallable("http://hbc.cryptopool.network", () -> openMiningPoolApiService.getPoolStats(CRYPTO_POOL), "CryptoPool.Network")));
        futures.add(executor.submit(poolInfoCallable("https://aikapool.com/hbf/index.php", () -> scrapService.getAikaPoolStats(), null)));
        futures.add(executor.submit(poolInfoCallable("http://solo-hbc.2zo.pw", () -> openMiningPoolApiService.getPoolStats(TWOZO_PW), null)));
        List<PoolInfo> pools = new ArrayList<>();
        for (Future<PoolInfo> future : futures) {
            try {
                pools.add(future.get());
            } catch (Exception e) {
                log.error("Error getting future", e);
            }
        }
        Collections.sort(pools, (o1, o2) -> {
            ApiStats stats1 = o1.getStats();
            ApiStats stats2 = o2.getStats();
            int result = 0;
            if (stats1.getHashRate() != null && stats2.getHashRate() == null) {
                result = -1;
            } else if (stats1.getHashRate() == null && stats2.getHashRate() != null) {
                result = 1;
            } else if (stats1.getHashRate() != null && stats2.getHashRate() != null) {
                result = stats1.getHashRate() > stats2.getHashRate() ? -1 : 1;
            }
            return result;
        });
        long totalHashRate = 0;
        for (PoolInfo poolInfo : pools) {
            response.append("\t");
            if (poolInfo.getStats() != null && poolInfo.getStats().getHashRate() != null) {
                response.append("**").append(PoolUtils.getHashRate(poolInfo.getStats().getHashRate())).append("** ");
                totalHashRate += poolInfo.getStats().getHashRate();
            } else {
                response.append("**???.?? H/s** ");
            }
            response.append(poolInfo.getDescription()).append(" ");
            if (poolInfo.getDiscordUser() != null) {
                addMention(response, event, poolInfo.getDiscordUser());
            }
            response.append("\n");
        }
        response.append("Total network hash rate are **").append(PoolUtils.getHashRate(totalHashRate)).append("**");
    }

    private static Callable<PoolInfo> poolInfoCallable(String description, Supplier<ApiStats> poolStats, String discordUser) {
        return () -> new PoolInfo(description, poolStats.get(), discordUser);
    }


    private void fillExchanges(MessageReceivedEvent event, MessageBuilder response) {
        response.append("List of all " + TOKEN_NAME + " Exchanges:\n");
        stocksExchange(response);
    }

    private void stocksExchange(MessageBuilder response) {
        response.append("\thttps://stocks.exchange ");
        try {
            List<ApiPrice> prices = stockExchangeApiService.getPriceByCoin(TOKEN_NAME);
            if (prices != null) {
                StringBuilder priceString = new StringBuilder();
                BigDecimal minValue = null;
                for (ApiPrice price : prices) {
                    BigDecimal priceUSD = price.getBuy().multiply(cryptoCompareApiService.getPriceUSD(price.getPairName()));
                    if (minValue == null || priceUSD.compareTo(minValue) < 0) {
                        minValue = priceUSD;
                    }
                }
                if (minValue != null) {
                    priceString.append("$").append(minValue).append(" ");
                }
                for (ApiPrice price : prices) {
                    priceString.append("[").append(price.getPairName()).append(" MaxBuy=")
                            .append(price.getBuy()).append(" ").append(" MinSell=").append(price.getSell()).append("]");
                }
                response.append(priceString);
            }
        } catch (Exception e) {
            log.error("Error when retrieve stocksExchangePrice", e);
            response.append("$?,?? Something is wrong");
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

    /*
     * DISCORD UTILS
     */

    private static boolean isAdmin(MessageReceivedEvent event) {
        boolean isAdmin = false;
        List<Role> roles = event.getGuild().getRolesByName("@official-dev", true);
        if (roles != null && !roles.isEmpty()) {
            Role admin = roles.get(0);
            if (event.getMember().getRoles().contains(admin)) {
                isAdmin = true;
            }
        }
        return isAdmin;
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
