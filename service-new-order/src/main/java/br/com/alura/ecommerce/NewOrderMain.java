package br.com.alura.ecommerce;

import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import br.com.alura.ecommerce.util.BigDecimalRandomUtil;
import br.com.alura.ecommerce.util.EmailGeneratorRanddomUtil;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    private static final String TOPIC = "ECOMMERCE_NEW_ORDER";
    private final static CorrelationId id = new CorrelationId(NewOrderMain.class.getSimpleName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            var email = EmailGeneratorRanddomUtil.generateRandomEmail();
            for (var i = 0; i < 10; i++) {

                var orderId = UUID.randomUUID().toString();
                var amount = BigDecimalRandomUtil.random(5000 + 1);
                var order = new Order(orderId, email, amount);

                orderDispatcher.send(TOPIC, email, id, order);
            }
        }
    }

}
