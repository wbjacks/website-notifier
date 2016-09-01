package com.wbjacks.website_notifier.data.dao;

import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.data.models.Website;

import java.util.List;

public interface WebsiteDao {
    void saveWebsiteObservation(Website website);

    List<Observer> getAllObserversForManualTesting();
}
