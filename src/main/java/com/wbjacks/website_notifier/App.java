package com.wbjacks.website_notifier;

import com.wbjacks.website_notifier.request_service.ObservationRequestService;
import jodd.petite.PetiteContainer;
import jodd.petite.config.AutomagicPetiteConfigurator;
import spark.Spark;

public class App {
    private static final PetiteContainer CONTAINER = new PetiteContainer();

    public static void main(String[] args) {
        Spark.get("/hello", ((request, response) -> "Hello world!"));
        Spark.post("/observeUrl", ((request, response) -> {
            CONTAINER.getBean(ObservationRequestService.class).saveObservationForUser(request.params("email"),
                    request.params("url"));
            return "ok";
        }));
        new AutomagicPetiteConfigurator().configure(CONTAINER);
    }
}