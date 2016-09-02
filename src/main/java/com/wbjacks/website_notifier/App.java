package com.wbjacks.website_notifier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wbjacks.website_notifier.request_service.ObservationRequestService;
import com.wbjacks.website_notifier.task_service.MonitorJobSchedulingService;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import org.quartz.SchedulerException;
import spark.Spark;

import java.util.Map;

public class App {
    private static final PetiteContainer CONTAINER = new PetiteContainer();

    public static void main(String[] args) {
        new AutomagicPetiteConfigurator().configure(CONTAINER);
        ObservationRequestService observationRequestService = CONTAINER.getBean(ObservationRequestService.class);
        try {
            CONTAINER.getBean(MonitorJobSchedulingService.class).launchJobsForAllWebsites();
        } catch (SchedulerException e) {
            // TODO: (wbjacks) log
            throw new RuntimeException(String.format("Error scheduling job: %s", e.getMessage()));
        }

        // TODO: (wbjacks) migrate me to a controller
        Spark.get("/hello", ((request, response) -> "Hello world!"));
        Spark.post("/observeUrl", ((request, response) -> {
            Map<String, String> params;
            try {
                params = parseJsonToSimpleStringMap(request.body());
                if (!params.containsKey("email") || !params.containsKey("url")) {
                    throw new Exception("Required parameter missing.");
                }
            } catch (Exception e) {
                throw new MalformedInputException(request.body(), e);
            }
            observationRequestService.saveObservationForUser(params.get("email"), params.get("url"));
            return "ok";
        }));
    }

    private static Map<String, String> parseJsonToSimpleStringMap(String json) {
        return new Gson().fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    private static class MalformedInputException extends Exception {
        private MalformedInputException(String input, Exception exception) {
            super(String.format("Bad input from request [%s]. Error is: %s", input, exception.getMessage()));
        }
    }
}