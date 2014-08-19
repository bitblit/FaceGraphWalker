package com.budandvine.graphwalker.config;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.budandvine.graphwalker.social.DynamoSocialUserDetailsService;
import com.budandvine.graphwalker.social.DynamoUserConnectionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SocialUserDetailsService;

import javax.annotation.Resource;

/**
 * Created by chrweiss on 7/12/14.
 */
@Configuration
@EnableSocial
public class SocialContext implements SocialConfigurer {
    @Value("${facebook.app.id}")
    private String facebookAppId;
    @Value("${facebook.app.secret}")
    private String facebookAppSecret;


    @Resource
    private DynamoDBMapper dynamoDBMapper;

    /**
     * Configures the connection factories for Facebook and Twitter.
     *
     * @param cfConfig
     * @param env
     */
    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
        cfConfig.addConnectionFactory(new FacebookConnectionFactory(
                facebookAppId,
                facebookAppSecret
        ));
    }

    /**
     * The UserIdSource determines the account ID of the user. The example application
     * uses the username as the account ID.
     */
    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    /**
     * This bean manages the connection flow between the account provider and
     * the example application.
     */
    @Bean
    public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
        return new ConnectController(connectionFactoryLocator, connectionRepository);
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        /*
        DynamoUsersConnectionRepository rval = new DynamoUsersConnectionRepository();
        rval.setConnectionTableName("graphwalker-connections");
        rval.setUserTableName("graphwalker-users");
        rval.setDynamoDBMapper(dynamoDBMapper());
        rval.setConnectionFactoryLocator(connectionFactoryLocator);
        return rval;
        */
        //DynamicUsersConnectionRepository rval = new DynamicUsersConnectionRepository(connectionDataPersistence(), connectionFactoryLocator);
        DynamoUserConnectionRepository rval = new DynamoUserConnectionRepository();
        rval.setConnectionFactoryLocator(connectionFactoryLocator);
        rval.setDynamoDBMapper(dynamoDBMapper);
        rval.setTableName("graph-walker-connections");
        rval.setDynamoSocialUserDetailsService((DynamoSocialUserDetailsService)socialUserDetailsService());
        return rval;
    }

    @Bean
    public SocialUserDetailsService socialUserDetailsService()
    {
        DynamoSocialUserDetailsService rval = new DynamoSocialUserDetailsService();
        rval.setDynamoDBMapper(dynamoDBMapper);
        rval.setTableName("graph-walker-users");
        return rval;
    }

}

