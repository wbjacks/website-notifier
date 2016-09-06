package com.wbjacks.website_notifier.task_service.comm;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;

public interface WebCallService {
    Document doGetRequest(String url) throws WebCallException;

    class WebCallException extends Exception {
        WebCallException(String url, IOException e) {
            super(String.format("Exception when fetching from url [%s]. Exception is: %s", url, e.getMessage()));
        }
    }
}
