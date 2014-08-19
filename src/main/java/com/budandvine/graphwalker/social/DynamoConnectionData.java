package com.budandvine.graphwalker.social;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.ConnectionData;

/**
 * Created by chrweiss on 7/13/14.
 */
@DynamoDBTable(tableName = "override-me")
public class DynamoConnectionData {
    private static final Logger LOG = LoggerFactory.getLogger(DynamoConnectionData.class);

    private String providerId;
    private String providerUserId;
    private String displayName;
    private String profileUrl;
    private String imageUrl;
    private String accessToken;
    private String secret;
    private String refreshToken;
    private Long expireTime;

    private String userId;

    public ConnectionData toConnectionData() {
        return new ConnectionData(providerId, providerUserId, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime);
    }

    public void fromConnectionData(ConnectionData cd) {
        if (cd != null) {
            providerId = cd.getProviderId();
            providerUserId = cd.getProviderUserId();
            displayName = cd.getDisplayName();
            profileUrl = cd.getProfileUrl();
            imageUrl = cd.getImageUrl();
            accessToken = cd.getAccessToken();
            secret = cd.getSecret();
            refreshToken = cd.getRefreshToken();
            expireTime = cd.getExpireTime();

        }
    }

    @DynamoDBHashKey
    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    @DynamoDBRangeKey
    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    @DynamoDBAttribute
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @DynamoDBAttribute
    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @DynamoDBAttribute
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @DynamoDBAttribute
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @DynamoDBAttribute
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @DynamoDBAttribute
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @DynamoDBAttribute
    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    @DynamoDBAttribute
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
