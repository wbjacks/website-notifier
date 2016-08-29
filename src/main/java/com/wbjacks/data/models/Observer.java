package com.wbjacks.data.models;

import javax.persistence.*;

@Entity
@Table(name = "observers")
public class Observer {
    @Id
    @GeneratedValue
    @Column(name = "observer_id", unique = true, nullable = false)
    private long observerId;

    @Column(name = "email", nullable = false)
    private String email;

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

    public void setObserverId(long observerId) {
        this.observerId = observerId;
    }
}
