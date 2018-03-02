package org.hotelbyte.discordbot.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.slf4j.Slf4j;
import org.hotelbyte.discordbot.model.openminingpool.ApiStats;
import org.hotelbyte.discordbot.util.PoolUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static org.hotelbyte.discordbot.enums.CacheEnum.SCRAP_SERVICE_CACHE;


@Slf4j
@Service
public class ScrapService {

    @Cacheable(value = SCRAP_SERVICE_CACHE, key = "'getCominingIoPoolStats'", unless = "#result == null || #result.getHashRate() == null")
    public ApiStats getCominingIoPoolStats() {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        long hashRate = 0;
        try {
            String searchUrl = "http://comining.io/";
            HtmlPage page = client.getPage(searchUrl);
            List<HtmlElement> items = (List<HtmlElement>) page.getByXPath("//td");
            boolean found = false;
            int index = 0;

            for (HtmlElement item : items) {
                try {
                    if (item.getFirstChild() != null && item.getFirstChild() instanceof DomText) {
                        String value = ((DomText) item.getFirstChild()).getData();
                        if ("HBC".equalsIgnoreCase(value) || "HBF".equalsIgnoreCase(value)) {
                            found = true;
                        } else if (found) {
                            if (index == 4 || index == 5 || index == 6) {
                                log.info("Found: {}", value);
                                int pointIndex = value.indexOf('.');
                                if (pointIndex > 0 && pointIndex + 4 < value.length()) {
                                    BigDecimal amount = new BigDecimal(value.substring(0, pointIndex + 3));
                                    log.debug("Found Amount: '{}'", amount);
                                    String unit = value.substring(pointIndex + 4);
                                    log.debug("Found Unit: '{}'", unit);
                                    hashRate += getHashRate(amount, unit);
                                }

                            }
                            index++;
                        }
                    }
                } catch (Exception e) {
                    log.error("Error parsing comining.io hashrate", e);
                }
            }
            log.debug("Total hashRate for HBF are {}", hashRate);
        } catch (Exception e) {
            log.error("Error parsing comining.io hashrate", e);
        }
        ApiStats stats = new ApiStats();
        stats.setHashRate(hashRate);
        return stats;
    }

    @Cacheable(value = SCRAP_SERVICE_CACHE, key = "'getAikaPoolStats'", unless = "#result == null || #result.getHashRate() == null")
    public ApiStats getAikaPoolStats() {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        long hashRate = 0;
        try {
            String searchUrl = "https://aikapool.com/hbf/index.php?page=statistics&action=pool";
            HtmlPage page = client.getPage(searchUrl);
            HtmlElement element = (HtmlElement) page.getElementById("b-hashrate");
            BigDecimal amount = new BigDecimal(((DomText) element.getFirstChild()).getData());
            String unit = ((DomText) element.getNextSibling()).getData().substring(1, 2);
            hashRate = getHashRate(amount, unit);
            log.debug("Total hashRate for HBF are {}", hashRate);
        } catch (Exception e) {
            log.error("Error parsing aikapool.com hashrate", e);
        }
        ApiStats stats = new ApiStats();
        stats.setHashRate(hashRate);
        return stats;
    }

    private static long getHashRate(BigDecimal amount, String unit) {
        switch (unit) {
            case "Y":
                amount = amount.multiply(PoolUtils.KILO_BIT);
            case "Z":
                amount = amount.multiply(PoolUtils.KILO_BIT);
            case "E":
                amount = amount.multiply(PoolUtils.KILO_BIT);
            case "T":
                amount = amount.multiply(PoolUtils.KILO_BIT);
            case "G":
                amount = amount.multiply(PoolUtils.KILO_BIT);
            case "M":
                amount = amount.multiply(PoolUtils.KILO_BIT);
            case "K":
                amount = amount.multiply(PoolUtils.KILO_BIT);
            default:
        }
        log.debug("Total: {}", amount);
        return amount.longValue();
    }
}
