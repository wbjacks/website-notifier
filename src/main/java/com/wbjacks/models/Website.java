package com.wbjacks.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "websites")
public class Website {
    @Id
    @GeneratedValue
    @Column(name = "website_id", unique = true, nullable = false)
        private long websiteId;

    @Column(name = "url", unique = true, nullable = false)
    private String url;

    @Column(name = "lastVisited")
    private Date lastVisited;

    @Column(name = "hash")
    private String hash;

    @ManyToMany(mappedBy = "websites", cascade = PERSIST, fetch = EAGER)
    private final List<Observer> observers = new ArrayList<>();

    private Website() {
        /* For hibernate */
    }

    // TODO: (wbjacks) for test, hide me
    public Website(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    // TODO: (wbjacks) only for test, hide me
    public void setUrl(String url) {
        this.url = url;
    }

    public Date getLastVisited() {
        return lastVisited;
    }

    public String getHash() {
        return hash;
    }

    public List<Observer> getObservers() {
        return observers;
    }

    public long getWebsiteId() {
        return websiteId;
    }
}
