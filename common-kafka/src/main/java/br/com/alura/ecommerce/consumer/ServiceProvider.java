package br.com.alura.ecommerce.consumer;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ServiceProvider<T> implements Callable<Void> {

    private final ServiceFactory<T> factory;
    public ServiceProvider(ServiceFactory<T> factory) {
        this.factory = factory;
    }

    public Void call() throws ExecutionException, InterruptedException {

        var serviceFactory = factory.create();

        try (var service = new KafkaService(serviceFactory.getConsumerGroup(),
                                            serviceFactory.getTopic(),
                                            serviceFactory::parse,
                                            String.class,
                                            Map.of())) {
            service.run();
        }

        return null;
    }
}
