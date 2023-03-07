package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService implements ConsumerService<Order> {

    private final static String CLASS_NAME = FraudDetectorService.class.getSimpleName();
    private final static CorrelationId id = new CorrelationId(CLASS_NAME);
    private final static String TOPIC = "ECOMMERCE_NEW_ORDER";

    public static void main(String[] args) {
        new ServiceRunner(FraudDetectorService::new).start(1);
    }

    private final static KafkaDispatcher<Order> dispatcher = new KafkaDispatcher<>();

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record)
            throws ExecutionException, InterruptedException {

        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // ignoring
            e.printStackTrace();
        }

        Message<Order> message = record.value();
        Order order = message.getPayLoad();
        CorrelationId correlationId = message.getCorrelationId().continueWith(id.getId());

        if (isFraud(order)) {
            rejected(order, correlationId);
        }  else {
            approved(order, correlationId);
        }
    }

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public String getConsumerGroup() {
        return CLASS_NAME;
    }

    private static void approved(Order order, CorrelationId correlationId) throws ExecutionException, InterruptedException {

        System.out.println("Order approved");
        dispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), correlationId, order);
    }

    private static void rejected(Order order, CorrelationId correlationId) throws ExecutionException, InterruptedException {

        System.out.println("Order isFraud");
        dispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), correlationId, order);
    }

    private  boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }
}
