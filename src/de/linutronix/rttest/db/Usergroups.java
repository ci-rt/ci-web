// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

/**
 * class Usergroups for Usermanagement.
 *
 * @author Benedikt Spranger
 */
@Entity
@Table
public class Usergroups {

    /**
     * The user ID.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The group name.
     */
    @NaturalId
    private String groupname;

    /**
     * The group status.
     */
    private Boolean enabled;

    /**
     * User group constructor.
     */
    public Usergroups() {
    }

    /**
     * User group constructor.
     *
     * @param newgroup group name
     */
    public Usergroups(final String newgroup) {
        this.groupname = newgroup;
        this.enabled = true;
    }

    /**
     * Get group ID.
     *
     * @return group ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set group ID.
     *
     * @param newid group ID
     */
    public void setId(final Long newid) {
        this.id = newid;
    }

    /**
     * Get group name.
     *
     * @return group name
     */
    public String getGroup() {
        return groupname;
    }

    /**
     * Set group name.
     *
     * @param group group name
     */
    public void setGroup(final String group) {
        this.groupname = group;
    }

    /**
     * Get group status.
     *
     * @return group status
     */
    public Boolean getGroupStatus() {
        return enabled;
    }

    /**
     * Set group status.
     *
     * @param enable group status
     */
    public void setGroupStatus(final Boolean enable) {
        this.enabled = enable;
    }

    /**
     * Represent Usergroup object as String.
     *
     * @return string representation of object
     */
    public String toString() {
        String status;
        if (enabled) {
            status = "enabled";
        } else {
            status = "disabled";
        }

        return "'" + groupname + "' " + status;
    }
}
