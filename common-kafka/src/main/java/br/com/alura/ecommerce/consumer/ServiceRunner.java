package br.com.alura.ecommerce.consumer;

import java.util.concurrent.Executors;

public class ServiceRunner<T> {
    private final ServiceProvider<T> provider;

    public ServiceRunner(ServiceFactory<T> factory) {
        this.provider = new ServiceProvider<>(factory);
    }

    public void start(int threadsCount) {

        var pool  = Executors.newFixedThreadPool(threadsCount);

        for (int i = 0; i <= threadsCount; i++) {
            pool.submit(provider);
        }
    }
}
