package com.grew.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashMap;
import java.util.Map;
import org.ektorp.support.CouchDbDocument;

/**
 *
 * @author bashizip
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDoc extends CouchDbDocument implements org.camunda.bpm.engine.identity.User {

    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String type;
    private String createdOn;
    private String lastLogin;
    private String token;
    private String tokenExpirDate;
    private String[] groups;

    private Map<Object, Object> settings;
    private Map<Object, Object> values;

    public UserDoc() {
        settings = new HashMap<>();
    }

    public String[] getGroups() {
        return groups;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void setGroups(String[] group) {
        this.groups = group;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Map<Object, Object> getValues() {
        return values;
    }

    public void setValues(Map<Object, Object> values) {
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenExpirDate() {
        return tokenExpirDate;
    }

    public void setTokenExpirDate(String tokenExpirationDate) {
        this.tokenExpirDate = tokenExpirationDate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Map<Object, Object> getSettings() {
        return settings;
    }

    public void setSettings(Map<Object, Object> settings) {
        this.settings = settings;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getEmail() {
        return email;
    }

}
