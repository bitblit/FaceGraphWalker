package com.budandvine.graphwalker.social;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.social.connect.*;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Created by chrweiss on 7/13/14.
 */
public class DynamoUserConnectionRepository implements UsersConnectionRepository, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(DynamoUserConnectionRepository.class);
    private DynamoDBMapper dynamoDBMapper;
    private DynamoDBMapperConfig mapperConfig;
    private ConnectionFactoryLocator connectionFactoryLocator;
    private DynamoSocialUserDetailsService dynamoSocialUserDetailsService;
    private boolean autoCreateConnection = true;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(mapperConfig,"You must set a table name");
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        // If empty, no app users currently associated (auto-register or send to registration process)
        // If 1, use it
        // If >1 treat as an exception
        ConnectionKey key = connection.getKey();
        Set<String> rval = findUserIdsConnectedTo(key.getProviderId(),new TreeSet<String>(Arrays.asList(key.getProviderUserId())));

        if (rval.size()==0 && autoCreateConnection)
        {
            LOG.info("No connection found and autoCreate is specified");
            UserProfile up = connection.fetchUserProfile();
            // Do not need to save the connection, that will happen later
            dynamoSocialUserDetailsService.createUserIfMissing(up);
        }
        return new ArrayList<>(rval);
    }

    @Override
    /**
     * providerId eg facebook
     * providerUserId eg 123000
     * returns the set of user ids connected to those service provider ids (empty if none)
     */
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        // TODO: use batch get to make this faster
        Set<String> rval = new TreeSet<>();
        for (String pui:providerUserIds) {
            DynamoConnectionData dcd = dynamoDBMapper.load(DynamoConnectionData.class, providerId, pui, mapperConfig);
            if (dcd!=null)
            {
                rval.add(dcd.getUserId());
            }
        }
        return rval;
    }

    @Override
    public ConnectionRepository createConnectionRepository(String userId) {
        return new DynamoConnectionRepository(userId, dynamoDBMapper,mapperConfig,connectionFactoryLocator);
    }

    public void setTableName(String name)
    {
        Assert.notNull(name,"Table name may not be null");
        this.mapperConfig = new DynamoDBMapperConfig(new DynamoDBMapperConfig.TableNameOverride(name));
    }

    public void setDynamoDBMapper(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public void setConnectionFactoryLocator(ConnectionFactoryLocator connectionFactoryLocator) {
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    public void setDynamoSocialUserDetailsService(DynamoSocialUserDetailsService dynamoSocialUserDetailsService) {
        this.dynamoSocialUserDetailsService = dynamoSocialUserDetailsService;
    }
}
