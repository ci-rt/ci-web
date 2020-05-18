// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest.db;

import java.io.Serializable;
import java.util.Base64;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

/**
 * Passwd class for user management.
 *
 * @author Benedikt Spranger
 */
@Entity
@Table
public class Passwd implements Serializable {

    /**
     * The user ID.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The user name.
     */
    @NaturalId
    private String username;

    /**
     * The real name.
     */
    private String realname;

    /**
     * The user password salt.
     */
    private String salt;

    /**
     * The user status.
     */
    private Boolean enabled;

    /**
     * Passwd class constructor.
     */
    public Passwd() {
    }

    /**
     * Passwd class constructor.
     *
     * @param user username
     * @param real realname
     * @param newsalt password salt
     */
    public Passwd(final String user, final String real, final byte[] newsalt) {
        this.username = user;
        this.realname = real;
        this.salt = Base64.getEncoder().encodeToString(newsalt);
        this.enabled = true;
    }

    /**
     * Get user ID.
     *
     * @return user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set user ID.
     *
     * @param newid user ID
     */
    public void setId(final Long newid) {
        this.id = newid;
    }

    /**
     * Get real name.
     *
     * @return real name
     */
    public String getRealname() {
        return realname;
    }

    /**
     * Set real name.
     *
     * @param real name
     */
    public void setRealname(final String real) {
        this.realname = real;
    }

    /**
     * Get user name.
     *
     * @return user name
     */
    public String getUser() {
        return username;
    }

    /**
     * Set user name.
     *
     * @param user user name
     */
    public void setUser(final String user) {
        this.username = user;
    }

    /**
     * Get password salt.
     *
     * @return password salt
     */
    public byte[] getSalt() {
        return Base64.getDecoder().decode(salt);
    }

    /**
     * Set password salt.
     *
     * @param newsalt password salt
     */
    public void setSalt(final byte[] newsalt) {
        this.salt = Base64.getEncoder().encodeToString(newsalt);
    }

    /**
     * Get user status.
     *
     * @return user status
     */
    public Boolean getUserStatus() {
        return enabled;
    }

    /**
     * Set user status.
     *
     * @param enable user status
     */
    public void setUserStatus(final Boolean enable) {
        this.enabled = enable;
    }
}
