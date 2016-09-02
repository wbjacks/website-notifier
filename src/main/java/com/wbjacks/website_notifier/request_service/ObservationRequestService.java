package com.wbjacks.website_notifier.request_service;

import com.wbjacks.website_notifier.data.models.Observer;
import org.quartz.SchedulerException;

import java.util.List;

public interface ObservationRequestService {
    void saveObservationForUser(String email, String url) throws SchedulerException;
}