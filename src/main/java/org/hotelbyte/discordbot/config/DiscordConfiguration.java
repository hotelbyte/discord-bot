package org.hotelbyte.discordbot.config;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.hibernate.validator.constraints.NotBlank;
import org.hotelbyte.discordbot.listeners.DiscordListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

import javax.security.auth.login.LoginException;

@Configuration
@PropertySource("classpath:discord.properties")
public class DiscordConfiguration {

    @NotBlank
    @Value("${discord.token}")
    private String token;
    @Value("${discord.watching}")
    private String watching;

    @Autowired
    private DiscordListener discordListener;

    @Bean
    public JDA jdaBuilder() throws LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(token);
        builder.addEventListener(discordListener);
        if (!StringUtils.isEmpty(watching)) {
            builder.setGame(Game.watching(watching));
        }
        return builder.buildAsync();
    }
}
