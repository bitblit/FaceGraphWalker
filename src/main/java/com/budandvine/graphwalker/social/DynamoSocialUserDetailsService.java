package com.budandvine.graphwalker.social;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.TreeSet;

/**
 * Created by chrweiss on 7/13/14.
 */
public class DynamoSocialUserDetailsService implements SocialUserDetailsService, InitializingBean{
    private static final Logger LOG = LoggerFactory.getLogger(DynamoSocialUserDetailsService.class);
    private DynamoDBMapper dynamoDBMapper;
    private DynamoDBMapperConfig mapperConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(mapperConfig,"You must set a table name");
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
        DynamoSocialUser user = dynamoDBMapper.load(DynamoSocialUser.class, userId, mapperConfig);
        if (user==null)
        {
            throw new UsernameNotFoundException("No user found with id: " + userId);
        }
        return user;
    }

    public boolean createUserIfMissing(UserProfile up)
    {
        boolean rval = false;
        DynamoSocialUser test = dynamoDBMapper.load(DynamoSocialUser.class, up.getEmail(), mapperConfig);
        if (test==null)
        {
            LOG.info("Creating missing user {}",up.getEmail());

            DynamoSocialUser user = new DynamoSocialUser();
            user.setEmail(up.getEmail());
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setName(up.getName());
            user.setFirstName(up.getFirstName());
            user.setLastName(up.getLastName());

            user.setRoles(new TreeSet<String>(Arrays.asList("ROLE_USER")));

            dynamoDBMapper.save(user, mapperConfig);
            rval = true;
        }
        return rval;
    }

    public void setTableName(String name)
    {
        Assert.notNull(name, "Table name may not be null");
        this.mapperConfig = new DynamoDBMapperConfig(new DynamoDBMapperConfig.TableNameOverride(name));
    }

    public void setDynamoDBMapper(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }
}
