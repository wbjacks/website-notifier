package com.wbjacks.website_notifier.request_service;

import com.wbjacks.website_notifier.data.dao.WebsiteDao;
import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.data.models.Website;
import com.wbjacks.website_notifier.task_service.MonitorJobSchedulingService;
import com.wbjacks.website_notifier.task_service.comm.WebCallService;
import com.wbjacks.website_notifier.util.HashService;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

@PetiteBean("observationRequestService")
public class ObservationRequestServiceImpl implements ObservationRequestService {
    private static final Logger LOGGER = Logger.getLogger(ObservationRequestServiceImpl.class);
    private final HashService _hashService;
    private final MonitorJobSchedulingService _monitorJobSchedulingService;
    private final WebCallService _webCallService;
    private final WebsiteDao _websiteDao;

    @PetiteInject
    ObservationRequestServiceImpl(HashService hashService, MonitorJobSchedulingService monitorJobSchedulingService,
                                  WebCallService webCallService, WebsiteDao websiteDao) {
        _hashService = hashService;
        _monitorJobSchedulingService = monitorJobSchedulingService;
        _webCallService = webCallService;
        _websiteDao = websiteDao;
    }

    @Override
    public void saveObservationForUser(String email, String url) throws SchedulerException, BadInputUrl {
        Observer observer = Observer.forEmail(email);
        Website website = Website.forUrl(url);
        try {
            website.setHash(_hashService.getHash(_webCallService.doGetRequest(website.getUrl()).body().toString()));
        } catch (WebCallService.WebCallException e) {
            LOGGER.error(String.format("Requesting site from user [email=%s] caused exception. Repacking exception "
                    + "for user. Original exception is: %s", email, e.getMessage()));
            throw new BadInputUrl(url);
        }
        website.getObservers().add(observer);
        _websiteDao.saveWebsiteObservation(website);
        _monitorJobSchedulingService.scheduleJobForWebsite(website);
    }
}
