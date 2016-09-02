package com.wbjacks.website_notifier.request_service;

import com.wbjacks.website_notifier.data.dao.WebsiteDao;
import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.data.models.Website;
import com.wbjacks.website_notifier.task_service.MonitorJobSchedulingService;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.quartz.SchedulerException;

import java.util.List;

@PetiteBean("observationRequestService")
public class ObservationRequestServiceImpl implements ObservationRequestService {

    private final MonitorJobSchedulingService _monitorJobSchedulingService;
    private final WebsiteDao _websiteDao;

    @PetiteInject
    ObservationRequestServiceImpl(MonitorJobSchedulingService monitorJobSchedulingService, WebsiteDao websiteDao) {
        _websiteDao = websiteDao;
        _monitorJobSchedulingService = monitorJobSchedulingService;
    }

    @Override
    public void saveObservationForUser(String email, String url) throws SchedulerException {
        Observer observer = Observer.forEmail(email);
        Website website = Website.forUrl(url);
        website.getObservers().add(observer);
        _websiteDao.saveWebsiteObservation(website);
        _monitorJobSchedulingService.scheduleJobForWebsite(website);
    }
}
