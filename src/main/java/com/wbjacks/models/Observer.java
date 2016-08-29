package com.wbjacks.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.PERSIST;
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

    @ManyToMany(cascade = PERSIST, fetch = EAGER)
    @JoinTable(name = "observations",
            joinColumns = {@JoinColumn(name = "observer_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "website_id", nullable = false, updatable = false)})
    private final List<Website> websites = new ArrayList<>();

    @SuppressWarnings("unused") // needed for Hibernate
    public Observer() {
    }

    public long getObserverId() {
        return observerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Website> getWebsites() {
        return websites;
    }
}
