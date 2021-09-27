package com.company.payloadsstatsbackend.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

// TODO: Use lombok constructor
// TODO: Check @data constructor
@Entity
@Table(name = "stats")
@Getter
@Setter
@DynamicUpdate
public class Stat {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Instant time;

    @Column(nullable = false)
    private String customer;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long cdn;

    @Column(nullable = false)
    private Long p2p;

    @Column(nullable = false)
    private Long sessions;

    public Stat() {

    }

    public Stat(Instant time, String customer, String content, Long cdn, Long p2p) {

        this.time = time;
        this.customer = customer;
        this.content = content;
        this.cdn = cdn;
        this.p2p = p2p;
        this.sessions = 0L;
    }

    public Stat(Instant time, String customer, String content, Long cdn, Long p2p, Long sessions) {

        this.time = time;
        this.customer = customer;
        this.content = content;
        this.cdn = cdn;
        this.p2p = p2p;
        this.sessions = sessions;
    }

    public Stat(Long id, Instant time, String customer, String content, Long cdn, Long p2p, Long sessions) {
        this.id = id;
        this.time = time;
        this.customer = customer;
        this.content = content;
        this.cdn = cdn;
        this.p2p = p2p;
        this.sessions = sessions;
    }
}
