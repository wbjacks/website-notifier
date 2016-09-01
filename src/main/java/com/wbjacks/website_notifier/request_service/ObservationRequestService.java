package com.wbjacks.website_notifier.request_service;

public interface ObservationRequestService {
    void saveObservationForUser(String email, String url);
}