package com.wbjacks.data.dao;

import com.wbjacks.data.models.Observer;
import com.wbjacks.data.models.Website;
import org.hibernate.Session;

public interface ObserverDao {
    void saveObserverInsideDbSession(Website website, Observer observer, Session session);
}
