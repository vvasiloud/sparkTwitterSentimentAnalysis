package org.vvasiloud.twitterAnalysis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import twitter4j.conf.ConfigurationBuilder;

@Component
public class TwitterConfig {

    private final TwitterOauthProperties oauthProperties;

    @Autowired
    public TwitterConfig(TwitterOauthProperties oauthProperties) {
        this.oauthProperties = oauthProperties;
    }

    @Bean
    public ConfigurationBuilder getTwitterConfig() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(oauthProperties.getConsumerKey())
                .setOAuthConsumerSecret(oauthProperties.getConsumerSecret())
                .setOAuthAccessToken(oauthProperties.getAccessToken())
                .setOAuthAccessTokenSecret(oauthProperties.getAccessTokenSecret());
        return cb;
    }
}
