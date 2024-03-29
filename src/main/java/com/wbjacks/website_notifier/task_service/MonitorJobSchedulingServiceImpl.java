package com.wbjacks.website_notifier.task_service;

import com.wbjacks.website_notifier.data.dao.WebsiteDao;
import com.wbjacks.website_notifier.data.models.Website;
import com.wbjacks.website_notifier.task_service.comm.WebCallService;
import com.wbjacks.website_notifier.util.ConfigurationManager;
import com.wbjacks.website_notifier.util.HashService;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.UUID;

@PetiteBean("monitorJobSchedulingService")
public class MonitorJobSchedulingServiceImpl implements MonitorJobSchedulingService {
    private static final String TRIGGER_GROUP = "web-trigger";
    private static final String JOB_GROUP = "web-job";
    private static final Logger LOGGER = Logger.getLogger(MonitorJobSchedulingServiceImpl.class);

    private final ConfigurationManager.JobSchedulingConfigurations _jobSchedulingConfigurations;
    private final Scheduler _scheduler;
    private final WebsiteDao _websiteDao;


    @PetiteInject
    private MonitorJobSchedulingServiceImpl(EmailTaskManagerService emailTaskManagerService, ConfigurationManager
            configurationManager, HashService hashService, WebCallService webCallService, WebsiteDao websiteDao)
            throws SchedulerException, ConfigurationException {
        _jobSchedulingConfigurations = configurationManager.getJobSchedulingConfigurations();
        _scheduler = StdSchedulerFactory.getDefaultScheduler();
        _websiteDao = websiteDao;

        // Scheduler context used to inject dependencies to job:
        // http://stackoverflow.com/questions/12777057/how-to-pass-instance-variables-into-quartz-job
        _scheduler.getContext().put("emailTaskManagerService", emailTaskManagerService);
        _scheduler.getContext().put("hashService", hashService);
        _scheduler.getContext().put("webCallService", webCallService);
        _scheduler.getContext().put("websiteDao", _websiteDao);

        _scheduler.start();
    }

    @Override
    public void launchJobsForAllWebsites() throws SchedulerException {
        for (Website website : _websiteDao.getAllWebsites()) {
            scheduleJobForWebsite(website);
        }
    }

    @Override
    public void scheduleJobForWebsite(Website website) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(MonitorJob.class) //
                .withIdentity(UUID.randomUUID().toString(), JOB_GROUP) //
                .usingJobData("monitoredUrl", website.getUrl()).build();
        SimpleTrigger trigger = TriggerBuilder.<SimpleTrigger>newTrigger() //
                .withIdentity(UUID.randomUUID().toString(), TRIGGER_GROUP) //
                .withSchedule(SimpleScheduleBuilder.simpleSchedule() //
                        .withIntervalInSeconds(_jobSchedulingConfigurations.getJobWaitTimeInSeconds()) //
                        .repeatForever()).startNow().build();
        try {
            _scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            LOGGER.error("Error scheduling job", e);
            throw e;
        }
        LOGGER.info(String.format("Job [%s] scheduled for URL [%s].", job.getKey(), website.getUrl()));
    }

    @Override
    public void shutdown() throws SchedulerException {
        _scheduler.shutdown();
    }

    public static class MonitorJob implements Job {
        private static final Logger LOGGER = Logger.getLogger(MonitorJob.class);

        private String _monitoredUrl;

        private EmailTaskManagerService _emailTaskManagerService;
        private HashService _hashService;
        private WebCallService _webCallService;
        private WebsiteDao _websiteDao;

        public MonitorJob() {
        }

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            LOGGER.info(String.format("Job [%s] executing", jobExecutionContext.getJobDetail().getKey()));
            System.out.println(String.format("Job [%s] executing", jobExecutionContext.getJobDetail().getKey()));
            Website website = _websiteDao.getByUrl(_monitoredUrl);

            Document document;
            try {
                document = _webCallService.doGetRequest(website.getUrl());
            } catch (Exception e) {
                JobExecutionException jobExecutionException = new JobExecutionException(e);
                jobExecutionException.refireImmediately();
                throw jobExecutionException;
            }

            String hash = _hashService.getHash(document.body().toString());
            if (!hash.equals(website.getHash())) {
                LOGGER.info(String.format("Website [%s] has changed, notifying users.", website.getUrl()));
                _websiteDao.updateWebsiteHash(website.getWebsiteId(), hash);
                _emailTaskManagerService.sendEmailToObservers(website.getObservers());
            }
        }

        // The below are used for context injection from the scheduler
        public void setEmailTaskManagerService(EmailTaskManagerService emailTaskManagerService) {
            _emailTaskManagerService = emailTaskManagerService;
        }

        public void setHashService(HashService hashService) {
            _hashService = hashService;
        }

        public void setMonitoredUrl(String monitoredUrl) {
            _monitoredUrl = monitoredUrl;
        }

        public void setWebCallService(WebCallService webCallService) {
            _webCallService = webCallService;
        }

        public void setWebsiteDao(WebsiteDao websiteDao) {
            _websiteDao = websiteDao;
        }
    }
}
