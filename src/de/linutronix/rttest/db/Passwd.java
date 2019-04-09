// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest.db;

import javax.persistence.*;

import org.hibernate.annotations.NaturalId;

@Entity
@Table
public class Passwd {

    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    private String username;
    private byte[] salt;
    private Boolean enabled;

    public Passwd() {
    }

    public Passwd(String user, byte[] newsalt) {
        this.username = user;
        this.salt = newsalt;
        this.enabled = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long newid) {
        this.id = newid;
    }

    public String getUser() {
        return username;
    }

    public void setUser(String user) {
        this.username = user;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] newsalt) {
        this.salt = newsalt;
    }

    public Boolean getUserStatus() {
        return enabled;
    }

    public void setUserStatus(Boolean enable) {
        this.enabled = enable;
    }
}
