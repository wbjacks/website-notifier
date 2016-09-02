package com.wbjacks.website_notifier.task_service;

import com.wbjacks.website_notifier.data.models.Observer;

import java.util.Set;

public interface EmailTaskManagerService {
    void sendEmailToObservers(Set<Observer> observers);
}
