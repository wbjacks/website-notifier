package com.wbjacks.website_notifier.request_service;

import com.wbjacks.website_notifier.data.models.Observer;

import java.util.List;

public interface ObservationRequestService {
    void saveObservationForUser(String email, String url);

    List<Observer> getAllObserversForManualTesting();
}