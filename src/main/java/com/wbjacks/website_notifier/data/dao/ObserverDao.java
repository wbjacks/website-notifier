package com.wbjacks.website_notifier.data.dao;

import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.data.models.Website;
import org.hibernate.Session;

public interface ObserverDao {
    void saveObserverInsideDbSession(Website website, Observer observer, Session session);
}
