package com.wbjacks.website_notifier.task_service;

import com.wbjacks.website_notifier.data.models.Website;
import org.quartz.SchedulerException;

public interface MonitorJobSchedulingService {
    void launchJobsForAllWebsites() throws SchedulerException;
    void scheduleJobForWebsite(Website website) throws SchedulerException;
}
