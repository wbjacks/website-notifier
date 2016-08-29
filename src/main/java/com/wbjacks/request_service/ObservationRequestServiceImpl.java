package com.wbjacks.request_service;

import com.wbjacks.data.dao.WebsiteDao;
import com.wbjacks.data.models.Observer;
import com.wbjacks.data.models.Website;
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
