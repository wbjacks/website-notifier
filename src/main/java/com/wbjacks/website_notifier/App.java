package com.wbjacks.website_notifier;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wbjacks.website_notifier.data.models.Observer;
import com.wbjacks.website_notifier.request_service.ObservationRequestService;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import spark.Spark;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class App {
    private static final PetiteContainer CONTAINER = new PetiteContainer();

    public static void main(String[] args) {
        new AutomagicPetiteConfigurator().configure(CONTAINER);
        ObservationRequestService observationRequestService = CONTAINER.getBean(ObservationRequestService.class);

        // TODO: (wbjacks) migrate me to a controller
        Spark.get("/hello", ((request, response) -> "Hello world!"));
        Spark.get("/getObservers", (request, response) -> observationRequestService.getAllObserversForManualTesting()
                .stream().map(Observer::getEmail).collect(Collectors.joining(", ")));
        Spark.post("/observeUrl", ((request, response) -> {
            Map<String, String> params;
            try {
                params = parseJsonToSimpleStringMap(request.body());
                if (!params.containsKey("email") || !params.containsKey("url")) {
                    throw new Exception("Required parameter missing.");
                }
            }
            catch(Exception e) {
                throw new MalformedInputException(request.body(), e);
            }
            observationRequestService.saveObservationForUser(params.get("email"), params.get("url"));
            return "ok";
        }));
    }

    private static class MalformedInputException extends Exception {
        private MalformedInputException(String input, Exception exception) {
            super(String.format("Bad input from request [%s]. Error is: %s", input, exception.getMessage()));
        }
    }

    private static Map<String, String> parseJsonToSimpleStringMap(String json) {
        return new Gson().fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
    }
}