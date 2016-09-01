package com.wbjacks.website_notifier.data.models;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "websites")
public class Website {
    @ManyToMany(cascade = ALL)
    @JoinTable(name = "observations", joinColumns = {@JoinColumn(name = "website_id", nullable = false, updatable =
            false)}, inverseJoinColumns = {@JoinColumn(name = "observer_id", nullable = false, updatable = false)})
    private final Set<Observer> observers = new HashSet<>();
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

    private Website() {
        /* For hibernate */
    }

    public static Website forUrl(String url) {
        Website website = new Website();
        website.url = url;
        return website;
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

    public Set<Observer> getObservers() {
        return observers;
    }

    public long getWebsiteId() {
        return websiteId;
    }
}
