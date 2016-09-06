package com.wbjacks.website_notifier;

import com.wbjacks.website_notifier.task_service.MonitorJobSchedulingService;
import com.wbjacks.website_notifier.util.AppController;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

public class App {
    private static final PetiteContainer CONTAINER = new PetiteContainer();
    private static final Logger LOGGER = Logger.getLogger(App.class);

    public static void main(String[] args) {
        new AutomagicPetiteConfigurator().configure(CONTAINER);
        MonitorJobSchedulingService monitorJobSchedulingService = CONTAINER.getBean(MonitorJobSchedulingService.class);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                monitorJobSchedulingService.shutdown();
            } catch (SchedulerException e) {
                LOGGER.error("Error closing job scheduler, message is: " + e);
            }
        }));

        try {
            monitorJobSchedulingService.launchJobsForAllWebsites();
        } catch (SchedulerException e) {
            LOGGER.error(String.format("Error scheduling job: %s", e.getMessage()));
            System.exit(1);
        }
        CONTAINER.getBean(AppController.class).initializeRoutes();
    }
}