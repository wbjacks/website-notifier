package com.wbjacks.website_notifier.request_service;

import org.quartz.SchedulerException;

public interface ObservationRequestService {
    void saveObservationForUser(String email, String url) throws SchedulerException, BadInputUrl;

    class BadInputUrl extends Exception {
        BadInputUrl(String url) {
            super("Given url [%s] does not resolve to a website.");
        }
    }
}