package com.budandvine.graphwalker.social;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUserDetails;

import java.util.*;

/**
 * Created by chrweiss on 7/12/14.
 */
@DynamoDBTable(tableName = "override-me")
public class DynamoSocialUser implements SocialUserDetails{

    private String email;

    private boolean enabled=true;
    private boolean accountNonExpired=true;
    private boolean credentialsNonExpired=true;
    private boolean accountNonLocked=true;

    private String name;
    private String firstName;
    private String lastName;

    private Set<String> roles = Collections.EMPTY_SET;

    /*
    public SocialUser toSocialUser()
    {
        List<GrantedAuthority> auth = new LinkedList<>();
        for (String s:roles)
        {
            auth.add(new SimpleGrantedAuthority(s));
        }
        // Email is the user key, id is the connection key
        SocialUser rval = new SocialUser(email, "--password--",  enabled,  accountNonExpired,  credentialsNonExpired,  accountNonLocked, auth);
        return rval;
    }*/

    @DynamoDBHashKey
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @DynamoDBAttribute
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @DynamoDBAttribute
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @DynamoDBAttribute
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @DynamoDBAttribute
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }


    @DynamoDBAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @DynamoDBAttribute
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    //-----------------------------

    @Override
    @DynamoDBIgnore
    public String getUserId() {
        return email;
    }

    @Override
    @DynamoDBIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auth = new LinkedList<>();
        for (String s:roles)
        {
            auth.add(new SimpleGrantedAuthority(s));
        }
        return auth;
    }

    @Override
    @DynamoDBIgnore
    public String getPassword() {
        return "N/A";
    }

    @Override
    @DynamoDBIgnore
    public String getUsername() {
        return email;
    }
}
