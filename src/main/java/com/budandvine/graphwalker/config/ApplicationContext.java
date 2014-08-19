package com.budandvine.graphwalker.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import com.budandvine.graphwalker.RemotePropertiesFactory;

import java.util.Properties;

/**
 * Created by chrweiss on 7/12/14.
 */
@Configuration
@ComponentScan(basePackages = {
        "com.budandvine.graphwalker"
})
@Import({WebAppContext.class, SecurityContext.class, SocialContext.class})
public class ApplicationContext {

    private static final String MESSAGE_SOURCE_BASE_NAME = "i18n/messages";

    @Bean
    public static Properties config() {
        RemotePropertiesFactory factory = new RemotePropertiesFactory();
        factory.setS3Client(s3());
        factory.setLocation("s3:face-graph-walker-config/config.properties");
        return factory.getObject();
    }

    @Bean
    public static AWSCredentialsProvider awsCredentialsProvider() {
        return new DefaultAWSCredentialsProviderChain();
    }

    @Bean
    public static AmazonS3Client s3() {
        return new AmazonS3Client(awsCredentialsProvider());
    }


    @Bean
    public AmazonDynamoDB dynamoDB() {
        return new AmazonDynamoDBClient(awsCredentialsProvider());
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(dynamoDB());
    }


    @Bean
    public static PropertyPlaceholderConfigurer propertyConfigurer() {
        PropertyPlaceholderConfigurer rval = new PropertyPlaceholderConfigurer();
        rval.setProperties(config());
        return rval;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename(MESSAGE_SOURCE_BASE_NAME);
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}