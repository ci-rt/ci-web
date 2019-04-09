// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest.db;

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
public class Passwd {

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
     * The user password salt.
     */
    private byte[] salt;

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
     * @param newsalt password salt
     */
    public Passwd(String user, byte[] newsalt) {
        this.username = user;
        this.salt = newsalt;
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
    public void setId(Long newid) {
        this.id = newid;
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
    public void setUser(String user) {
        this.username = user;
    }

    /**
     * Get password salt.
     *
     * @return password salt
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Set password salt.
     *
     * @param newsalt password salt
     */
    public void setSalt(byte[] newsalt) {
        this.salt = newsalt;
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
    public void setUserStatus(Boolean enable) {
        this.enabled = enable;
    }
}
