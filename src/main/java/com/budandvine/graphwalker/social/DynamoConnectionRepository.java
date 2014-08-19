package com.budandvine.graphwalker.social;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chrweiss on 7/13/14.
 */
public class DynamoConnectionRepository implements ConnectionRepository {
    private static final Logger LOG = LoggerFactory.getLogger(DynamoConnectionRepository.class);

    private String userId;
    private DynamoDBMapper dynamoDBMapper;
    private DynamoDBMapperConfig mapperConfig;
    private ConnectionFactoryLocator connectionFactoryLocator;

    public DynamoConnectionRepository(String userId, DynamoDBMapper dynamoDBMapper, DynamoDBMapperConfig mapperConfig, ConnectionFactoryLocator connectionFactoryLocator) {
        this.userId = userId;
        this.dynamoDBMapper = dynamoDBMapper;
        this.mapperConfig = mapperConfig;
        this.connectionFactoryLocator = connectionFactoryLocator;
    }

    @Override
    public MultiValueMap<String, Connection<?>> findAllConnections() {
        MultiValueMap<String,Connection<?>> rval = new LinkedMultiValueMap<>();

        for (Connection<?> c:findAllConnectionsAsList())
        {
            rval.add(c.getKey().getProviderId(), c);
        }

        return rval;
    }

    public List<Connection<?>> findAllConnectionsAsList() {
        // TODO: This is REALLY inefficient in DynamoDB
        PaginatedScanList<DynamoConnectionData> psl = dynamoDBMapper.scan(DynamoConnectionData.class, new DynamoDBScanExpression(), mapperConfig);

        List<DynamoConnectionData> temp = new LinkedList<>();
        for (DynamoConnectionData d:psl)
        {
            if (userId!=null && userId.equals(d.getUserId()))
            {
                temp.add(d);
            }
        }

        return fromConnectionData(temp);
    }

    @Override
    public List<Connection<?>> findConnections(String providerId) {
        return findAllConnections().get(providerId);
    }

    @Override
    public <A> List<Connection<A>> findConnections(Class<A> aClass) {
        List<Connection<A>> rval = new LinkedList<>();

        for (Connection<?> c:findAllConnectionsAsList())
        {
            if (aClass.isAssignableFrom(c.getApi().getClass()))
            {
                rval.add((Connection<A>)c);
            }
        }
        return rval;
    }

    @Override
    public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> stringStringMultiValueMap) {
        LOG.warn("not implemented yet");
        throw new UnsupportedOperationException();
    }

    @Override
    public Connection<?> getConnection(ConnectionKey connectionKey) {
        DynamoConnectionData dcd = dynamoDBMapper.load(DynamoConnectionData.class, connectionKey.getProviderId(), connectionKey.getProviderUserId());
        return (dcd==null)?null:connectionFactoryLocator.getConnectionFactory(dcd.getProviderId()).createConnection(dcd.toConnectionData());
    }

    @Override
    public <A> Connection<A> getConnection(Class<A> aClass, String providerUserId) {
        Connection<A> rval =null;
        List<Connection<A>> l = findConnections(aClass);
        for (Iterator<Connection<A>> i = l.iterator();i.hasNext() && rval==null;)
        {
            Connection<A> test = i.next();
            if (providerUserId!=null && providerUserId.equals(test.getKey().getProviderUserId()))
            {
                rval = test;
            }
        }
        return rval;
    }

    @Override
    public <A> Connection<A> getPrimaryConnection(Class<A> aClass) {
        List<Connection<A>> l = findConnections(aClass);
        return (l==null || l.size()==0)?null:l.get(0);
    }

    @Override
    public <A> Connection<A> findPrimaryConnection(Class<A> aClass) {
        List<Connection<A>> l = findConnections(aClass);
        return (l==null || l.size()==0)?null:l.get(0);
    }

    @Override
    public void addConnection(Connection<?> connection) {
        LOG.info("Adding connection {}",connection);
        saveConnection(connection);
    }

    @Override
    public void updateConnection(Connection<?> connection) {
        LOG.info("Updating connection {}",connection);
        saveConnection(connection);
    }

    @Override
    public void removeConnections(String providerId) {
        LOG.info("Removing connections {}",providerId);
        List<Connection<?>> l = findAllConnections().get(providerId);
        for (Connection<?> c:l)
        {
            removeConnection(c.getKey());
        }
    }

    @Override
    public void removeConnection(ConnectionKey connectionKey) {
        LOG.info("Removing connections {}",connectionKey);

        DynamoConnectionData dcd = new DynamoConnectionData();
        dcd.setProviderId(connectionKey.getProviderId());
        dcd.setProviderUserId(connectionKey.getProviderUserId());
        dynamoDBMapper.delete(dcd);
    }

    public String saveConnection(Connection<?> connection)
    {

        // Create the connection data...
        DynamoConnectionData toSave = new DynamoConnectionData();
        toSave.setProviderId(connection.getKey().getProviderId());
        toSave.setProviderUserId(connection.getKey().getProviderUserId());
        toSave.setDisplayName(connection.getDisplayName());
        toSave.setImageUrl(connection.getImageUrl());
        toSave.setProfileUrl(connection.getProfileUrl());

        ConnectionData cd = connection.createData();
        toSave.setAccessToken(cd.getAccessToken());
        toSave.setRefreshToken(cd.getRefreshToken());
        toSave.setExpireTime(cd.getExpireTime());
        toSave.setSecret(cd.getSecret());

        UserProfile up = connection.fetchUserProfile();
        toSave.setUserId(up.getEmail());
        dynamoDBMapper.save(toSave, mapperConfig);

        return toSave.getUserId();
    }

    // internal helpers

    public List<Connection<?>> fromConnectionData(List<DynamoConnectionData> list)
    {
        List<Connection<?>> rval = new ArrayList<Connection<?>>(list.size());
        for (DynamoConnectionData c:list)
        {
            rval.add(connectionFactoryLocator.getConnectionFactory(c.getProviderId()).createConnection(c.toConnectionData()));
        }
        return rval;
    }
}
