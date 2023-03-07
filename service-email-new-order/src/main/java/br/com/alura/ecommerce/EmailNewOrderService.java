package br.com.alura.ecommerce;

import br.com.alura.ecommerce.consumer.ConsumerService;
import br.com.alura.ecommerce.consumer.ServiceRunner;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailNewOrderService implements ConsumerService<Order> {

    private final static String TOPIC_NEW_ORDER = "ECOMMERCE_NEW_ORDER";
    private final static String TOPIC_SEND_EMAIL = "ECOMMERCE_SEND_EMAIL";
    private final static KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public static void main(String[] args) {
        new ServiceRunner(EmailNewOrderService::new).start(1);
    }

    @Override
    public void parse(ConsumerRecord<String, Message<Order>> record) throws Exception{

        System.out.println("------------------------------------------");
        System.out.println("Processing new order, preparing email");
        System.out.println(record.value());

        var email = record.value().getPayLoad().getEmail();

        CorrelationId correlationId = record.value()
                                     .getCorrelationId()
                                     .continueWith(EmailNewOrderService.class.getSimpleName());

        String message = "Thank you for your order! We are processing your order!";
        emailDispatcher.send(TOPIC_SEND_EMAIL, email, correlationId, message);
    }

    @Override
    public String getTopic() {
        return TOPIC_NEW_ORDER;
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }
}
