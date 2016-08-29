package com.wbjacks.request_service;

import com.wbjacks.data.dao.ObserverDao;
import com.wbjacks.data.models.Observer;
import com.wbjacks.data.models.Website;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean("observationRequestService")
public class ObservationRequestServiceImpl implements ObservationRequestService {

    private final ObserverDao _observerDao;

    @PetiteInject
    ObservationRequestServiceImpl(ObserverDao observerDao) {
        _observerDao = observerDao;
    }

    @Override
    public void saveObservationForUser(String email, String url) {
        Observer observer = Observer.forEmail(email);
        Website website = Website.forUrl(url);
        observer.getWebsites().add(website);
        _observerDao.saveObserver(observer);
    }
}
