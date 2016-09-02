package com.wbjacks.website_notifier.task_service;

import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.task_service.comm.EmailService;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@PetiteBean("emailTaskManagerService")
public class EmailTaskManagerServiceImpl implements EmailTaskManagerService {
    private final EmailService _emailService;
    private final Executor _executor;

    @PetiteInject
    private EmailTaskManagerServiceImpl(EmailService emailService) {
        _emailService = emailService;
        _executor = Executors.newSingleThreadExecutor(); // TODO: (wbjacks) could use fixed thread pool here
    }

    @Override
    public void sendEmailToObservers(Set<Observer> observers) {
        for (Observer observer : observers) {
            _executor.execute(() -> {
                try {
                    _emailService.sendEmailToObserver(observer);
                } catch (EmailService.EmailConfigurationException e) {
                    // TODO: (wbjacks) add logger
                }
            });
        }
    }
}
