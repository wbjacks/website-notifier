package com.wbjacks.website_notifier.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wbjacks.website_notifier.request_service.ObservationRequestService;
import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import spark.Spark;

import java.util.Map;

@PetiteBean("appController")
public class AppControllerImpl implements AppController {
    private final ObservationRequestService _observationRequestService;

    @PetiteInject
    private AppControllerImpl(ObservationRequestService observationRequestService) {
        _observationRequestService = observationRequestService;
    }

    @Override
    public void initializeRoutes() {
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
            _observationRequestService.saveObservationForUser(params.get("email"), params.get("url"));
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
