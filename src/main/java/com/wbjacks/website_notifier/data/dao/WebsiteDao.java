package com.wbjacks.website_notifier.data.dao;

import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.data.models.Website;

import java.util.Collection;

public interface WebsiteDao {
    void saveWebsiteObservation(Website website);

    Collection<Website> getAllWebsites();

    Website getByUrl(String url);

    void updateWebsiteHash(long websiteId, String hash);
}
