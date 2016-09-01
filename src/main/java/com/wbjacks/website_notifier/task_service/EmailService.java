package com.wbjacks.website_notifier.task_service;

import com.wbjacks.website_notifier.data.models.Observer;

public interface EmailService {
    void sendEmailToObserver(Observer observer) throws EmailServiceImpl.EmailConfigurationException;
}
