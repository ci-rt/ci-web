// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest.db;

import javax.persistence.*;

import org.hibernate.annotations.NaturalId;

@Entity
@Table
public class Usergroups {

    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    private String groupname;
    private Boolean enabled;

    public Usergroups() {
    }

    public Usergroups(String group) {
        this.groupname = group;
        this.enabled = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroup() {
        return groupname;
    }

    public void setGroup(String group) {
        this.groupname = group;
    }

    public Boolean getGroupStatus() {
        return enabled;
    }

    public void setGroupStatus(Boolean enabled) {
        this.enabled = enabled;
    }

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
