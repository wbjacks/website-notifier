package com.wbjacks.website_notifier.task_service;

import com.wbjacks.website_notifier.data.dao.WebsiteDao;
import com.wbjacks.website_notifier.data.models.Website;
import com.wbjacks.website_notifier.task_service.comm.WebCallService;
import com.wbjacks.website_notifier.util.ConfigurationManager;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@PetiteBean("monitorJobSchedulingService")
public class MonitorJobSchedulingServiceImpl implements MonitorJobSchedulingService {
    private static final String TRIGGER_GROUP = "web-trigger";
    private static final String JOB_GROUP = "web-job";
    private static final Logger LOGGER = Logger.getLogger(MonitorJobSchedulingServiceImpl.class);

    private final EmailTaskManagerService _emailTaskManagerService;
    private final ConfigurationManager.JobSchedulingConfigurations _jobSchedulingConfigurations;
    private final Scheduler _scheduler;
    private final WebCallService _webCallService;
    private final WebsiteDao _websiteDao;


    @PetiteInject
    private MonitorJobSchedulingServiceImpl(EmailTaskManagerService emailTaskManagerService, ConfigurationManager
            configurationManager, WebCallService webCallService, WebsiteDao websiteDao) throws SchedulerException {
        _emailTaskManagerService = emailTaskManagerService;
        _jobSchedulingConfigurations = configurationManager.getJobSchedulingConfigurations();
        _scheduler = StdSchedulerFactory.getDefaultScheduler();
        _webCallService = webCallService;
        _websiteDao = websiteDao;

        // Scheduler context used to inject dependencies to job:
        // http://stackoverflow.com/questions/12777057/how-to-pass-instance-variables-into-quartz-job
        _scheduler.getContext().put("emailTaskManagerService", _emailTaskManagerService);
        _scheduler.getContext().put("webCallService", _webCallService);
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
        }
        catch (SchedulerException e) {
            LOGGER.error("Error scheduling job", e);
            throw e;
        }
        LOGGER.info(String.format("Job [%s] scheduled for URL [%s].", job.getKey(), website.getUrl()));
    }

    public static class MonitorJob implements Job {
        private static final String HASHING_ALGORITHM = "MD5";
        private static final Logger LOGGER = Logger.getLogger(MonitorJob.class);

        private String _monitoredUrl;

        private EmailTaskManagerService _emailTaskManagerService;
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

            String hash;
            try {
                hash = new HexBinaryAdapter().marshal(MessageDigest.getInstance(HASHING_ALGORITHM).digest(document
                        .body().toString().getBytes()));
            } catch (NoSuchAlgorithmException e) {
                // This is fatal for this (and all...) jobs
                JobExecutionException jobExecutionException = new JobExecutionException(e);
                jobExecutionException.unscheduleAllTriggers();
                throw jobExecutionException;
            }

            if (!hash.equals(website.getHash())) {
                _websiteDao.updateWebsiteHash(website.getWebsiteId(), hash);
                _emailTaskManagerService.sendEmailToObservers(website.getObservers());
            }
        }

        // The below are used for context injection from the scheduler
        public void setEmailTaskManagerService(EmailTaskManagerService emailTaskManagerService) {
            _emailTaskManagerService = emailTaskManagerService;
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
