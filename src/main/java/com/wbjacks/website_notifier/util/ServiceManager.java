package com.wbjacks.website_notifier.util;

import com.wbjacks.website_notifier.request_service.ObservationRequestService;
import jodd.petite.PetiteContainer;

import java.util.ArrayList;
import java.util.List;

public class ServiceManager {
    static final PetiteContainer PETITE_CONTAINER = new PetiteContainer();
    static final List<Class> TASK_SERVICES = new ArrayList<>();
    static final List<Class> REQUEST_SERVICES = new ArrayList<>();


    static {
        REQUEST_SERVICES.add(ObservationRequestService.class);

        PETITE_CONTAINER.getConfig().setDetectDuplicatedBeanNames(true);
    }

    public static void registerServices() {

    }
}
