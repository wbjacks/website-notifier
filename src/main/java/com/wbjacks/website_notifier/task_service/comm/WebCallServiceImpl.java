package com.wbjacks.website_notifier.task_service.comm;

import jodd.petite.meta.PetiteBean;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@PetiteBean("webCallService")
public class WebCallServiceImpl implements WebCallService {
    private static final Logger LOGGER = Logger.getLogger(WebCallServiceImpl.class);

    public WebCallServiceImpl() {
    }

    @Override
    public Document doGetRequest(String url) throws WebCallException {
        LOGGER.info(String.format("Requesting url [%s]...", url));
        try {
            return Jsoup.connect(url).maxBodySize(0).get();
        } catch (IOException e) {
            throw new WebCallException(url, e);
        }
    }
}