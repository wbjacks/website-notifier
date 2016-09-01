package com.wbjacks.website_notifier.request_service;

import com.wbjacks.website_notifier.data.dao.WebsiteDao;
import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.data.models.Website;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean("observationRequestService")
public class ObservationRequestServiceImpl implements ObservationRequestService {

    private final WebsiteDao _websiteDao;

    @PetiteInject
    ObservationRequestServiceImpl(WebsiteDao websiteDao) {
        _websiteDao = websiteDao;
    }

    @Override
    public void saveObservationForUser(String email, String url) {
        Observer observer = Observer.forEmail(email);
        Website website = Website.forUrl(url);
        website.getObservers().add(observer);
    }
}
