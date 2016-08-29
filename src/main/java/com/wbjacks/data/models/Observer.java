package com.wbjacks.data.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "observers")
public class Observer {
    @Id
    @GeneratedValue
    @Column(name = "observer_id", unique = true, nullable = false)
    private long observerId;

    @Column(name = "email", nullable = false)
    private String email;

    @ManyToMany(fetch = EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "observations",
            joinColumns = {@JoinColumn(name = "observer_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "website_id", nullable = false, updatable = false)})
    private final Set<Website> websites = new HashSet<>();

    public Observer() {
    }

    public static Observer forEmail(String email) {
        Observer observer = new Observer();
        observer.email = email;
        return observer;
    }

    public long getObserverId() {
        return observerId;
    }

    public String getEmail() {
        return email;
    }

    public Set<Website> getWebsites() {
        return websites;
    }

    public void setObserverId(long observerId) {
        this.observerId = observerId;
    }
}
